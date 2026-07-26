# Guía de Comandos y Despliegue del Servidor - Mi Caché

Este documento contiene los comandos esenciales para conectarse, administrar, desplegar y monitorizar los frontends (Vue 3 / Nuxt 3) y el backend de producción de Mi Caché en el VPS (`37.27.197.244`).

---

## 1. Conexión al Servidor (SSH)

Para conectarte al VPS por línea de comandos utilizando la clave SSH configurada:

```bash
ssh root@37.27.197.244
```

---

## 2. Arquitectura de Despliegue Paralelo (Migración Front)

Para realizar una migración progresiva y comparaciones manuales en vivo sin romper el sitio en producción:

| Servicio | URL de Acceso | Tipo | Directorio en VPS | Puerto Interno |
| :--- | :--- | :--- | :--- | :--- |
| **Vue 3 (Legacy / Producción)** | `https://www.micache.es` | Estático SPA | `/var/www/micache/` | Nginx Directo (443) |
| **Nuxt 3 (SSR / Desarrollo)** | `https://www.micache.es:3000` | Node SSR (PM2) | `/var/www/micache-nuxt/` | PM2 en `127.0.0.1:3001` |
| **Backend Spring Boot** | Interfaz `/api/` | Java JAR | `/var/web/micache/backend.jar` | Systemd en `127.0.0.1:8080` |

Ambos frontends comparten los mismos endpoints de backend (`/api/`), autenticación OAuth2 (`/oauth2/`) y carpeta de avatares/archivos (`/uploads/`).

---

## 3. Gestión de Servicios (Systemd & PM2)

### Backend y Nginx (Systemd)

| Acción | Comando |
| :--- | :--- |
| **Ver estado del Backend** | `systemctl status micache-backend` |
| **Reiniciar el Backend** | `systemctl restart micache-backend` |
| **Detener el Backend** | `systemctl stop micache-backend` |
| **Iniciar el Backend** | `systemctl start micache-backend` |
| **Ver estado de Nginx** | `systemctl status nginx` |
| **Validar sintaxis Nginx** | `nginx -t` |
| **Recargar Nginx (sin downtime)** | `systemctl reload nginx` |

### Frontend Nuxt 3 SSR (PM2)

| Acción | Comando |
| :--- | :--- |
| **Ver estado de Nuxt 3** | `pm2 status` / `pm2 info micache-nuxt` |
| **Ver logs en tiempo real** | `pm2 logs micache-nuxt` |
| **Reiniciar servicio Nuxt** | `PORT=3001 pm2 restart micache-nuxt --update-env` |
| **Guardar estado PM2** | `pm2 save` |

---

## 4. Despliegue de Cambios en Nuxt 3

### Opción A: Despliegue Completo (Recomendado)
Desde tu equipo local (en la raíz del proyecto), ejecuta el script unificado de automatización:

```bash
./deploy/upload.sh
```

Este script realiza automáticamente los siguientes pasos:
1. Compila la versión estática de Vue 3 (si existe `frontend/`).
2. Transfiere la configuración actualizada de Nginx.
3. Sincroniza el código fuente de `frontend-nuxt/` excluyendo `node_modules` y builds locales.
4. Ejecuta en el VPS: `npm install && npm run build`.
5. Reinicia el proceso PM2 `micache-nuxt` escuchando en el puerto interno `3001`.
6. Valida la sintaxis de Nginx (`nginx -t`) y aplica recarga en caliente (`systemctl reload nginx`).
7. Asegura la apertura del puerto `3000` en el Firewall (UFW).

---

### Opción B: Despliegue Rápido (Solo Cambios en Nuxt 3)
Si solo estás trabajando en el proyecto Nuxt y quieres desplegar únicamente los cambios del frontal de forma rápida:

**Paso 1: Sincronizar código desde tu máquina local**
```bash
rsync -avz --delete \
    --exclude 'node_modules' \
    --exclude '.nuxt' \
    --exclude '.output' \
    --exclude '.git' \
    frontend-nuxt/ root@37.27.197.244:/var/www/micache-nuxt/
```

**Paso 2: Compilar y reiniciar PM2 en el VPS**
```bash
ssh root@37.27.197.244 "cd /var/www/micache-nuxt && npm install && npm run build && PORT=3001 pm2 restart micache-nuxt --update-env && pm2 save"
```

---

## 5. Promoción Definitiva de Nuxt 3 al Puerto Principal (443)

Cuando la migración a Nuxt 3 esté completamente terminada y comprobada:

1. **Editar la configuración de Nginx en el VPS (`/etc/nginx/sites-available/micache` o en el repositorio `deploy/nginx-micache.conf`)**:
   En el bloque `server` del puerto `443` (`www.micache.es`), reemplaza el bloque `location /` que sirve Vue 3 estático por el proxy a Nuxt 3:

   ```nginx
   # Sustituir la sección de Vue 3 en el puerto 443 por:
   location / {
       proxy_pass http://127.0.0.1:3001;
       proxy_http_version 1.1;
       proxy_set_header Upgrade $http_upgrade;
       proxy_set_header Connection 'upgrade';
       proxy_set_header Host $host;
       proxy_cache_bypass $http_upgrade;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
       proxy_set_header X-Forwarded-Proto $scheme;
   }

   # Añadir ruta de caché de estáticos Nuxt:
   location /_nuxt/ {
       alias /var/www/micache-nuxt/.output/public/_nuxt/;
       expires 1y;
       add_header Cache-Control "public, max-age=31536000, immutable";
       access_log off;
   }
   ```

2. **Validar y recargar Nginx en el VPS**:
   ```bash
   ssh root@37.27.197.244 "nginx -t && systemctl reload nginx"
   ```

---

## 6. Monitorización de Logs

### Logs de Nuxt 3 (PM2)
```bash
ssh root@37.27.197.244 "pm2 logs micache-nuxt --lines 100"
```

### Logs del Backend (Spring Boot)
```bash
ssh root@37.27.197.244 "journalctl -u micache-backend -f"
```

### Logs de Nginx (Accesos y Errores)
```bash
ssh root@37.27.197.244 "tail -f /var/log/nginx/access.log /var/log/nginx/error.log"
```

---

## 7. Gestión de Docker y Observabilidad

Las bases de datos y el colector de OpenTelemetry se gestionan a través de Docker en `/var/web/micache/observability/`:

* **Ver estado de los contenedores:**
  ```bash
  ssh root@37.27.197.244 "cd /var/web/micache/observability && docker compose ps"
  ```
* **Reiniciar el stack de observabilidad:**
  ```bash
  ssh root@37.27.197.244 "cd /var/web/micache/observability && docker compose restart"
  ```
