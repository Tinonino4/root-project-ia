# Guía de Comandos del Servidor - Mi Caché

Este documento contiene los comandos esenciales para conectarse, administrar y monitorizar el servidor de producción de Mi Caché (`37.27.197.244`).

---

## 1. Conexión al Servidor (SSH)

Para conectarte al VPS por línea de comandos utilizando la clave SSH configurada:

```bash
ssh root@37.27.197.244
```

---

## 2. Gestión de Servicios (Systemd)

El backend de Java corre directamente en el sistema operativo como un servicio administrado por Systemd.

| Acción | Comando |
| :--- | :--- |
| **Ver estado del Backend** | `systemctl status micache-backend` |
| **Reiniciar el Backend** | `systemctl restart micache-backend` |
| **Detener el Backend** | `systemctl stop micache-backend` |
| **Iniciar el Backend** | `systemctl start micache-backend` |
| **Ver estado de Nginx** | `systemctl status nginx` |
| **Reiniciar Nginx** | `systemctl restart nginx` |

---

## 3. Monitorización de Logs

### Logs del Backend (Spring Boot)
Utiliza `journalctl` para ver la salida de consola y errores del backend:

*   **Ver logs en tiempo real (modo *follow*):**
    ```bash
    journalctl -u micache-backend -f
    ```
*   **Ver las últimas 100 líneas sin paginar:**
    ```bash
    journalctl -u micache-backend -n 100 --no-pager
    ```
*   **Buscar errores específicos en los logs:**
    ```bash
    journalctl -u micache-backend | grep -i "error"
    ```

### Logs de Nginx
*   **Logs de accesos en tiempo real:**
    ```bash
    tail -f /var/log/nginx/access.log
    ```
*   **Logs de errores en tiempo real:**
    ```bash
    tail -f /var/log/nginx/error.log
    ```

---

## 4. Administración de Nginx

Nginx actúa como proxy inverso y sirve el frontend compilado.

*   **Validar sintaxis de la configuración (Ejecutar siempre antes de reiniciar):**
    ```bash
    nginx -t
    ```
*   **Recargar configuración sin desconectar usuarios (downtime cero):**
    ```bash
    systemctl reload nginx
    ```
*   **Rutas de archivos importantes:**
    *   Configuración del sitio: `/etc/nginx/sites-available/micache` (enlazado a `/etc/nginx/sites-enabled/micache`)
    *   Archivos estáticos del Frontend: `/var/www/micache/`

---

## 5. Gestión de Docker y Observabilidad

Las bases de datos y el colector de OpenTelemetry se gestionan a través de Docker y Docker Compose.

### Comandos Generales de Docker
*   **Listar contenedores activos:**
    ```bash
    docker ps
    ```
*   **Listar todos los contenedores (incluyendo apagados):**
    ```bash
    docker ps -a
    ```
*   **Ver logs de un contenedor específico:**
    ```bash
    docker logs <nombre_o_id_contenedor> -f
    ```

### Comandos de Docker Compose (Observabilidad / BD)
Los servicios de observabilidad y recolector OTel están en `/var/web/micache/observability/`:

*   **Ir al directorio de Docker Compose:**
    ```bash
    cd /var/web/micache/observability
    ```
*   **Ver estado de los contenedores en ese stack:**
    ```bash
    docker compose ps
    ```
*   **Reiniciar todos los servicios del stack:**
    ```bash
    docker compose restart
    ```
*   **Ver logs del stack de observabilidad:**
    ```bash
    docker compose logs -f
    ```
*   **Apagar y volver a levantar el stack (para aplicar cambios de configuración):**
    ```bash
    docker compose down && docker compose up -d
    ```
