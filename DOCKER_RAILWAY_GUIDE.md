# 🐳 Guía de Despliegue Docker + Railway - Spring Boot

## ✅ Cambios Realizados

### 📦 Archivos Docker Creados/Actualizados:

1. **`Dockerfile`** - Multi-stage build optimizado
   - Stage 1: Build con Maven 3.9.5 + JDK 17
   - Stage 2: Runtime con JRE 17 Alpine (imagen ligera)
   - Usuario no-root para seguridad
   - Health check integrado
   - Optimizaciones de memoria (256MB-512MB)

2. **`.dockerignore`** - Excluye archivos innecesarios del build
   - Reduce tamaño de contexto de build
   - Excluye target/, logs/, IDEs, etc.

3. **`railway.json`** - Configuración actualizada para Docker
   - Builder: DOCKERFILE
   - Health check: /actuator/health
   - Restart policy configurado

4. **`docker-compose.yml`** - Para desarrollo local
   - PostgreSQL + Spring Boot
   - Networking configurado
   - Health checks
   - Volumes para persistencia

### 🔧 Dependencias Agregadas:

- ✅ **Spring Boot Actuator** - Para health checks y métricas
- ✅ Endpoints configurados en `application-railway.properties`
- ✅ Security config actualizado para permitir `/actuator/health`

### 📊 Compilación Exitosa:

```
✅ BUILD SUCCESS
✅ JAR: 50.4 MB
✅ 36 archivos Java compilados
✅ Actuator configurado
✅ Docker optimizado
```

---

## 🚀 Despliegue en Railway

### Opción 1: Deployment Automático desde GitHub (Recomendado)

#### Paso 1: Commit y Push

```powershell
cd "c:\Users\USER\Desktop\ING SISTEMAS\7\ING SOFTWARE 2\usuario-service"

# Ver cambios
git status

# Agregar todos los archivos
git add .

# Commit
git commit -m "feat: Configuración Docker optimizada para Railway

- Agregado Dockerfile multi-stage con Alpine Linux
- Agregado .dockerignore para optimizar build
- Actualizado railway.json para usar DOCKERFILE builder
- Agregado Spring Boot Actuator para health checks
- Actualizado SecurityConfig para permitir /actuator/health
- Configurado docker-compose.yml para desarrollo local
- Optimizaciones de memoria y seguridad (usuario no-root)
- Health check configurado en Railway
"

# Push
git push origin dev
```

#### Paso 2: Railway Detectará Automáticamente

Railway verá el `Dockerfile` y `railway.json` y:

1. ✅ Usará el Dockerfile para construir la imagen
2. ✅ Construirá en un contenedor aislado
3. ✅ Ejecutará health checks en `/actuator/health`
4. ✅ Re-intentará hasta 3 veces si falla

#### Paso 3: Monitorear el Build

```
Railway Dashboard → Tu servicio → Deployments → View Logs
```

Deberías ver:
```
🐳 Building Docker image...
📦 [Stage 1/2] Building with Maven...
📦 [Stage 2/2] Creating runtime image...
✅ Image built successfully
🚀 Starting container...
✅ Health check passed: /actuator/health
✅ Deployment successful
```

---

## 🔍 Verificación del Deployment

### 1. Health Check Endpoint

```bash
curl https://tu-servicio.up.railway.app/actuator/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "groups": ["liveness", "readiness"]
}
```

### 2. Liveness y Readiness Probes

```bash
# Liveness - ¿Está vivo el contenedor?
curl https://tu-servicio.up.railway.app/actuator/health/liveness

# Readiness - ¿Está listo para recibir tráfico?
curl https://tu-servicio.up.railway.app/actuator/health/readiness
```

### 3. Métricas (opcional)

```bash
curl https://tu-servicio.up.railway.app/actuator/metrics
```

### 4. Info (opcional)

```bash
curl https://tu-servicio.up.railway.app/actuator/info
```

---

## 📊 Ventajas de Docker vs Nixpacks

| Característica | Docker | Nixpacks (anterior) |
|----------------|--------|---------------------|
| **Build reproducible** | ✅ Siempre igual | ⚠️ Puede variar |
| **Caché de layers** | ✅ Más rápido | ⚠️ Limitado |
| **Control total** | ✅ Total | ⚠️ Limitado |
| **Multi-stage build** | ✅ Sí | ❌ No |
| **Imagen optimizada** | ✅ Alpine (ligera) | ⚠️ Más pesada |
| **Usuario no-root** | ✅ Sí | ⚠️ Root |
| **Health checks** | ✅ Integrados | ⚠️ Externos |
| **Desarrollo local** | ✅ docker-compose | ❌ No |

---

## 🏠 Desarrollo Local con Docker

### Iniciar servicios (PostgreSQL + Spring Boot):

```powershell
# Iniciar Docker Desktop primero

# Construir y levantar servicios
docker-compose up --build

# O en modo detached (background)
docker-compose up -d --build
```

### Ver logs:

```powershell
# Todos los servicios
docker-compose logs -f

# Solo Spring Boot
docker-compose logs -f app

# Solo PostgreSQL
docker-compose logs -f postgres
```

### Detener servicios:

```powershell
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: elimina datos)
docker-compose down -v
```

### Probar la aplicación local:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Registrar usuario
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "correo": "test@example.com",
    "password": "test123"
  }'
```

---

## 🔧 Variables de Entorno en Railway

Railway inyecta automáticamente estas variables:

### Automáticas (desde PostgreSQL service):
```bash
✅ DATABASE_URL
✅ PGUSER
✅ PGPASSWORD
✅ PGHOST
✅ PGPORT
✅ PGDATABASE
✅ PORT (puerto asignado por Railway)
```

### Recomendadas para Producción:

```bash
# JWT Secret (IMPORTANTE para seguridad)
JWT_SECRET=[genera uno único de 64+ caracteres]

# Expiration (opcional, tiene default)
JWT_EXPIRATION=86400000

# Spring Profile (opcional, ya está en Dockerfile)
SPRING_PROFILES_ACTIVE=railway

# DDL Auto (opcional, ya está en application-railway.properties)
DDL_AUTO=update
```

### Generar JWT_SECRET seguro:

```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

---

## 🐳 Características del Dockerfile

### Stage 1: Build
```dockerfile
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder
```
- Maven 3.9.5
- JDK 17
- Alpine Linux (ligero)
- Caché de dependencias Maven

### Stage 2: Runtime
```dockerfile
FROM eclipse-temurin:17-jre-alpine
```
- Solo JRE (no JDK completo) → más ligero
- Alpine Linux → imagen mínima
- Usuario no-root → más seguro
- Health check integrado

### Optimizaciones:
- ✅ Multi-stage build → Imagen final más pequeña
- ✅ Caché de layers → Builds más rápidos
- ✅ Usuario no-root → Seguridad
- ✅ JVM optimizado para containers
- ✅ Memory limits: 256MB-512MB
- ✅ Timezone: America/Bogota

---

## 📈 Monitoreo y Troubleshooting

### Ver logs en Railway:

```
Railway Dashboard → Deployments → Latest → View Logs
```

### Errores comunes:

#### 1. Health check failing
```
❌ Health check timeout
```
**Solución:**
- Verifica que `/actuator/health` esté accesible
- Aumenta `healthcheckTimeout` en railway.json
- Revisa logs de inicio de la aplicación

#### 2. Out of memory
```
❌ Container killed (OOMKilled)
```
**Solución:**
- Ajusta `JAVA_OPTS` en Dockerfile
- Reduce `-Xmx` si es necesario
- Considera upgrade de plan en Railway

#### 3. Database connection error
```
❌ Connection refused to postgres
```
**Solución:**
- Verifica que PostgreSQL esté en el mismo proyecto
- Revisa las variables de entorno DATABASE_URL
- Asegúrate de que la BD esté "Active"

#### 4. Build failing
```
❌ Failed to build Docker image
```
**Solución:**
- Revisa que el Dockerfile esté en la raíz
- Verifica que pom.xml sea válido
- Revisa logs de build en Railway

---

## 🎯 Checklist Pre-Deployment

### ✅ Archivos verificados:
- [x] `Dockerfile` - Multi-stage optimizado
- [x] `.dockerignore` - Excluye archivos innecesarios
- [x] `railway.json` - Builder DOCKERFILE
- [x] `docker-compose.yml` - Desarrollo local
- [x] `pom.xml` - Actuator agregado
- [x] `application-railway.properties` - Actuator configurado
- [x] `SecurityConfig.java` - /actuator/health permitido

### ✅ Compilación:
- [x] BUILD SUCCESS
- [x] JAR generado: 50.4 MB
- [x] No errores de compilación
- [x] Actuator incluido

### ✅ Listo para push:
```powershell
git add .
git commit -m "feat: Configuración Docker optimizada para Railway"
git push origin dev
```

---

## 🎉 Próximos Pasos

1. **Hacer push a GitHub** (instrucciones arriba)
2. **Esperar deployment automático** en Railway (2-5 minutos)
3. **Verificar health check** en Railway Dashboard
4. **Probar endpoints** con la URL de Railway
5. **Configurar JWT_SECRET** en variables de entorno (opcional pero recomendado)

---

## 📝 Notas Importantes

1. **DeepFace Service no incluido**
   - Los endpoints de reconocimiento facial no funcionarán
   - Endpoints de usuarios, auth, ingresos, gastos, metas funcionan perfectamente
   - Si necesitas facial recognition, deploya el servicio Python por separado

2. **Base de datos PostgreSQL**
   - Ya desplegada en Railway
   - Railway la conectará automáticamente
   - Schema se creará/actualizará automáticamente (DDL_AUTO=update)

3. **Health Checks**
   - Railway usará `/actuator/health` automáticamente
   - Timeout: 300 segundos (5 minutos)
   - 3 reintentos si falla

4. **Seguridad**
   - Usuario no-root en container
   - Actuator endpoints públicos solo health/info
   - JWT con secret configurable

---

**Última actualización:** 11 de noviembre de 2025  
**Versión:** 1.0.0 - Docker optimizado  
**Status:** ✅ LISTO PARA DEPLOYMENT
