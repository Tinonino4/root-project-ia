#!/bin/bash

# ==============================================================================
# SCRIPT DE TRANSFERENCIA Y DESPLIEGUE - MI CACHÉ
# ==============================================================================

set -e # Detener en caso de cualquier error no capturado

VPS_IP="37.27.197.244"
echo "🚀 Iniciando transferencia y despliegue en VPS ($VPS_IP)..."

# 1. Compilar Frontend Vue 3 Legacy localmente si existe la carpeta
if [ -d "frontend" ]; then
    echo "📦 Compilando Frontend Vue 3 estático localmente..."
    (cd frontend && npm run build)
fi

# 1.1. Compilar Backend Java si existe la carpeta
if [ -d "backend" ]; then
    echo "☕ Compilando Backend Spring Boot JAR localmente..."
    (cd backend && mvn package -DskipTests)
fi

# 2. Crear directorios remotos necesarios en VPS
echo "📂 Verificando directorios remotos..."
ssh root@$VPS_IP "mkdir -p /var/web/micache /var/www/micache /var/www/micache-nuxt /var/web/micache/observability"

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

# 5. Transferir Frontend Vue 3 compilado
if [ -d "frontend/dist" ]; then
    echo "🎨 Copiando archivos estáticos de Vue 3 (a /var/www/micache/)..."
    ssh root@$VPS_IP "rm -rf /var/www/micache/*"
    scp -r frontend/dist/* root@$VPS_IP:/var/www/micache/
fi

# 6. Transferir código de Frontend Nuxt 3
echo "🎨 Sincronizando código fuente de Nuxt 3..."
rsync -avz --delete \
    --exclude 'node_modules' \
    --exclude '.nuxt' \
    --exclude '.output' \
    --exclude '.git' \
    frontend-nuxt/ root@$VPS_IP:/var/www/micache-nuxt/

# 7. Compilar e iniciar Nuxt 3 SSR en el VPS con PM2 (Puerto interno 3001)
echo "⚡ Instalando dependencias, compilando y reiniciando Nuxt 3 SSR con PM2..."
ssh root@$VPS_IP "cd /var/www/micache-nuxt && npm install && npm run build && (PORT=3001 pm2 restart micache-nuxt --update-env || PORT=3001 pm2 start .output/server/index.mjs --name 'micache-nuxt') && pm2 save"

# 8. Asegurar permiso de puerto 3000 en UFW Firewall
echo "🛡️ Verificando puerto 3000 en el Firewall (UFW)..."
ssh root@$VPS_IP "ufw allow 3000/tcp comment 'Nuxt 3 SSR Parallel Preview' || true"

# 9. Transferir archivos de Observabilidad (OTel Collector)
if [ -d "observability" ]; then
    echo "📊 Copiando archivos de observabilidad..."
    scp observability/docker-compose.yml root@$VPS_IP:/var/web/micache/observability/docker-compose.yml
    scp observability/otel-collector-config.yaml root@$VPS_IP:/var/web/micache/observability/otel-collector-config.yaml
    if [ -f observability/.env ]; then
        scp observability/.env root@$VPS_IP:/var/web/micache/observability/.env
    fi
fi

# 10. Validar Nginx y reiniciar servicios
echo "🔄 Validando Nginx y aplicando cambios de servicio..."
ssh root@$VPS_IP '
    nginx -t && systemctl reload nginx
    systemctl restart micache-backend || systemctl start micache-backend
    if [ -f /var/web/micache/observability/docker-compose.yml ]; then
        cd /var/web/micache/observability && docker compose up -d
    fi
'

# 11. Ejecución opcional de Pruebas E2E de humo post-despliegue
if [ "$1" == "--test" ]; (cd .. 2>/dev/null || true; npm run test:e2e:vue && npm run test:e2e:nuxt); then
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

