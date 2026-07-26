#!/bin/bash

# ==============================================================================
# SCRIPT DE TRANSFERENCIA Y DESPLIEGUE - MI CACHÉ (NUXT 3 SSR)
# ==============================================================================

set -e # Detener en caso de cualquier error no capturado

VPS_IP="37.27.197.244"
echo "🚀 Iniciando transferencia y despliegue en VPS ($VPS_IP)..."

# 1. Compilar Backend Java si existe la carpeta
if [ -d "backend" ]; then
    echo "☕ Compilando Backend Spring Boot JAR localmente..."
    (cd backend && mvn package -DskipTests)
fi

# 2. Crear directorios remotos necesarios en VPS y limpiar legacy Vue 3 (/var/www/micache)
echo "📂 Verificando directorios remotos y limpiando legacy Vue 3..."
ssh root@$VPS_IP "mkdir -p /var/web/micache /var/www/micache-nuxt /var/web/micache/observability && rm -rf /var/www/micache"

# 3. Transferir docker-compose.yml y backend.jar
if [ -f "docker-compose.yml" ]; then
    echo "🐳 Copiando docker-compose.yml..."
    scp docker-compose.yml root@$VPS_IP:/var/web/micache/docker-compose.yml
fi

if [ -f "backend/target/backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "☕ Copiando backend.jar..."
    scp backend/target/backend-0.0.1-SNAPSHOT.jar root@$VPS_IP:/var/web/micache/backend.jar
fi

# 4. Transferir configuración Nginx
echo "⚙️ Copiando configuración de Nginx..."
scp deploy/nginx-micache.conf root@$VPS_IP:/etc/nginx/sites-available/micache

# 5. Transferir código de Frontend Nuxt 3
echo "🎨 Sincronizando código fuente de Nuxt 3..."
rsync -avz --delete \
    --exclude 'node_modules' \
    --exclude '.nuxt' \
    --exclude '.output' \
    --exclude '.git' \
    frontend-nuxt/ root@$VPS_IP:/var/www/micache-nuxt/

# 6. Compilar e iniciar Nuxt 3 SSR en el VPS con PM2 (Puerto interno 3001)
echo "⚡ Instalando dependencias, compilando y reiniciando Nuxt 3 SSR con PM2..."
ssh root@$VPS_IP "cd /var/www/micache-nuxt && npm install && npm run build && (PORT=3001 pm2 restart micache-nuxt --update-env || PORT=3001 pm2 start .output/server/index.mjs --name 'micache-nuxt') && pm2 save"

# 7. Transferir archivos de Observabilidad (OTel Collector)
if [ -d "observability" ]; then
    echo "📊 Copiando archivos de observabilidad..."
    scp observability/docker-compose.yml root@$VPS_IP:/var/web/micache/observability/docker-compose.yml
    scp observability/otel-collector-config.yaml root@$VPS_IP:/var/web/micache/observability/otel-collector-config.yaml
    if [ -f observability/.env ]; then
        scp observability/.env root@$VPS_IP:/var/web/micache/observability/.env
    fi
fi

# 8. Validar Nginx y reiniciar servicios
echo "🔄 Validando Nginx y aplicando cambios de servicio..."
ssh root@$VPS_IP '
    nginx -t && systemctl reload nginx
    systemctl restart micache-backend || systemctl start micache-backend
    if [ -f /var/web/micache/observability/docker-compose.yml ]; then
        cd /var/web/micache/observability && docker compose up -d
    fi
'

# 9. Ejecución opcional de Pruebas E2E post-despliegue
if [ "$1" == "--test" ]; then
    echo "🧪 Ejecutando pruebas E2E..."
    npm run test:e2e
    echo "🧪 Pruebas E2E completadas con éxito tras el despliegue."
fi

echo "=========================================================================="
echo "✅ Despliegue completado con éxito."
echo "=========================================================================="
echo "🌐 Nuxt 3 (Producción):       https://www.micache.es"
echo "=========================================================================="
echo "👉 Logs Backend: ssh root@$VPS_IP 'journalctl -u micache-backend -f'"
echo "👉 Logs Nuxt 3:  ssh root@$VPS_IP 'pm2 logs micache-nuxt'"
echo "👉 Ejecutar Pruebas E2E: npm run test:e2e"
