#!/bin/bash

# ==============================================================================
# SCRIPT DE TRANSFERENCIA PARA DESPLIEGUE - MI CACHÉ
# ==============================================================================

VPS_IP="37.27.197.244"
echo "🚀 Iniciando transferencia de archivos al VPS ($VPS_IP)..."

# 1. Crear directorios remotos necesarios
echo "📂 Creando directorios remotos..."
ssh root@$VPS_IP "mkdir -p /var/web/micache /var/www/micache"

# 2. Transferir docker-compose.yml
echo "🐳 Copiando docker-compose.yml..."
scp docker-compose.yml root@$VPS_IP:/var/web/micache/docker-compose.yml

# 3. Transferir JAR del Backend
echo "☕ Copiando backend.jar..."
scp backend/target/backend-0.0.1-SNAPSHOT.jar root@$VPS_IP:/var/web/micache/backend.jar

# 4. Transferir archivos de configuración
echo "⚙️ Copiando archivos de configuración de servicios..."
#scp deploy/micache-backend.service root@$VPS_IP:/etc/systemd/system/micache-backend.service
scp deploy/nginx-micache.conf root@$VPS_IP:/etc/nginx/sites-available/micache

# 5. Transferir Frontend compilado
echo "🎨 Copiando archivos del Frontend (Vue3)..."
# Limpiamos el directorio remoto del frontend primero
ssh root@$VPS_IP "rm -rf /var/www/micache/*"
# Subimos la carpeta dist de forma recursiva
scp -r frontend/dist/* root@$VPS_IP:/var/www/micache/

# 6. Transferir archivos de Observabilidad (OTel Collector)
echo "📊 Copiando archivos de configuración de Observabilidad..."
ssh root@$VPS_IP "mkdir -p /var/web/micache/observability"
scp observability/docker-compose.yml root@$VPS_IP:/var/web/micache/observability/docker-compose.yml
scp observability/otel-collector-config.yaml root@$VPS_IP:/var/web/micache/observability/otel-collector-config.yaml
if [ -f observability/.env ]; then
  echo "🔒 Copiando credenciales de observabilidad (.env)..."
  scp observability/.env root@$VPS_IP:/var/web/micache/observability/.env
fi

# 7. Descargar el agente de OpenTelemetry Java en el VPS (si no existe)
echo "🔭 Verificando agente de OpenTelemetry Java..."
ssh root@$VPS_IP 'if [ ! -f /var/web/micache/opentelemetry-javaagent.jar ]; then
  echo "⬇️ Descargando opentelemetry-javaagent.jar..."
  curl -sL https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -o /var/web/micache/opentelemetry-javaagent.jar
  echo "✅ Agente descargado."
else
  echo "✅ Agente ya existe, saltando descarga."
fi'

# 8. Reiniciar servicios
echo "🔄 Reiniciando servicios en el VPS..."
ssh root@$VPS_IP "systemctl daemon-reload && systemctl restart micache-backend && cd /var/web/micache/observability && docker compose down && docker compose up -d"

echo "✅ ¡Archivos transferidos y servicios reiniciados con éxito!"
echo "👉 Verifica los logs con: ssh root@$VPS_IP 'journalctl -u micache-backend -f'"
