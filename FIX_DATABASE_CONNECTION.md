# 🔧 Fix: Error de Conexión a Base de Datos en Railway

## ❌ Error Detectado

```
HHH000342: Could not obtain connection to query metadata
java.lang.NullPointerException: Cannot invoke "org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert..."
```

**Causa:** Hibernate no puede conectarse a PostgreSQL.

---

## ✅ Soluciones Aplicadas

### 1. **Dockerfile Actualizado**
- ✅ Agregada variable `PORT` al ENV
- ✅ Health check usa `${PORT:-8080}`
- ✅ ENTRYPOINT usa `-Dserver.port=${PORT}`

### 2. **Logging Mejorado**
- ✅ Agregado logging DEBUG para `org.springframework.jdbc.datasource`
- ✅ Agregado logging DEBUG para `com.zaxxer.hikari` (pool de conexiones)
- ✅ Corregidos nombres de propiedades de Actuator

---

## 🔍 Pasos para Verificar en Railway

### Paso 1: Verificar que la Base de Datos está Vinculada

1. Ve a **Railway Dashboard**
2. Abre tu **proyecto**
3. Verifica que veas **DOS servicios**:
   - 📦 **web-production-...** (Spring Boot)
   - 🐘 **postgres** (PostgreSQL)

**Si no ves el servicio PostgreSQL:**
- Click en **"New"** → **"Database"** → **"PostgreSQL"**
- Espera a que se despliegue

### Paso 2: Verificar Variables de Entorno

1. Click en el servicio **web-production-...**
2. Ve a la pestaña **"Variables"**
3. Verifica que existan estas variables:

#### ✅ Variables Automáticas (inyectadas por Railway):
```bash
DATABASE_PUBLIC_URL=postgresql://postgres:PASSWORD@HOST:PORT/railway
DATABASE_URL=postgresql://postgres:PASSWORD@postgres.railway.internal:5432/railway
PGDATA=/var/lib/postgresql/data/pgdata
PGDATABASE=railway
PGHOST=postgres.railway.internal
PGPASSWORD=lGlWZraCZSxrVpXRxzPvrOFDEmksFqcS
PGPORT=5432
PGUSER=postgres
PORT=(asignado automáticamente)
```

**⚠️ IMPORTANTE:** Si NO ves `DATABASE_URL`, significa que los servicios NO están vinculados.

### Paso 3: Vincular Servicios (si no están vinculados)

1. Click en el servicio **web-production-...**
2. Ve a **"Settings"**
3. Scroll hasta **"Service Variables"**
4. Busca la sección **"Reference Variables"**
5. Click en **"+ New Variable"**
6. Selecciona **"Reference"**
7. Selecciona el servicio **postgres**
8. Se crearán automáticamente todas las variables `DATABASE_URL`, `PGUSER`, etc.

### Paso 4: Verificar el Formato de DATABASE_URL

Railway debe inyectar `DATABASE_URL` en formato JDBC:

```bash
# ❌ MAL (formato PostgreSQL nativo)
DATABASE_URL=postgresql://postgres:password@host:5432/railway

# ✅ BIEN (formato JDBC para Spring Boot)
DATABASE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
```

**Si Railway usa formato nativo**, necesitamos convertirlo:

1. Ve a **Variables**
2. Click en **"+ New Variable"**
3. Name: `SPRING_DATASOURCE_URL`
4. Value: `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}`
5. Click **"Add"**

### Paso 5: Agregar Variables Manualmente (si es necesario)

Si las variables no se crean automáticamente:

```bash
# Variables de Base de Datos
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=lGlWZraCZSxrVpXRxzPvrOFDEmksFqcS

# O simplemente
DATABASE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
PGUSER=postgres
PGPASSWORD=lGlWZraCZSxrVpXRxzPvrOFDEmksFqcS
```

---

## 🚀 Re-Deploy después de Configurar Variables

### Opción 1: Push de Correcciones

```powershell
cd "c:\Users\USER\Desktop\ING SISTEMAS\7\ING SOFTWARE 2\usuario-service"

git add .
git commit -m "fix: Corregir configuración de puerto y logging para Railway

- Dockerfile ahora usa \$PORT correctamente en ENTRYPOINT
- Agregado logging DEBUG para datasource y HikariCP
- Corregidos nombres de propiedades de Actuator
- Health check usa puerto dinámico
"
git push origin dev
```

### Opción 2: Re-Deploy Manual (si las variables ya están)

1. Ve a **Railway Dashboard**
2. Click en tu servicio **web-production-...**
3. Ve a **"Deployments"**
4. Click en el último deployment
5. Click en **"..."** (tres puntos)
6. Click en **"Redeploy"**

---

## 📊 Ver Logs con Mejor Información

Después del re-deploy, los logs mostrarán:

```
✅ Logs esperados (CORRECTO):
2025-11-12 00:XX:XX - HikariPool-1 - Starting...
2025-11-12 00:XX:XX - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...
2025-11-12 00:XX:XX - HikariPool-1 - Start completed.
2025-11-12 00:XX:XX - Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-11-12 00:XX:XX - Started CelularServiceApplication in X.XXX seconds
2025-11-12 00:XX:XX - Tomcat started on port(s): XXXX (http)
```

```
❌ Logs de error (si persiste):
2025-11-12 00:XX:XX - HikariPool-1 - Starting...
2025-11-12 00:XX:XX - HikariPool-1 - Exception during pool initialization
2025-11-12 00:XX:XX - Failed to obtain JDBC Connection

^ Esto indicaría que DATABASE_URL es incorrecta
```

---

## 🔧 Verificación de Conectividad

### Desde Railway CLI (opcional):

```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link al proyecto
railway link

# Ver variables
railway variables

# Conectarse a la base de datos
railway run psql $DATABASE_URL

# Una vez conectado, verificar tablas:
\dt
```

---

## 🎯 Checklist de Solución

- [ ] 1. Verificar que PostgreSQL está desplegado en Railway
- [ ] 2. Verificar que los servicios están en el mismo proyecto
- [ ] 3. Verificar que existe variable `DATABASE_URL` en Variables
- [ ] 4. Verificar formato de `DATABASE_URL` (debe empezar con `jdbc:postgresql://`)
- [ ] 5. Verificar `PGUSER` y `PGPASSWORD` existen
- [ ] 6. Hacer commit y push de las correcciones del Dockerfile
- [ ] 7. Esperar nuevo deployment
- [ ] 8. Verificar logs para confirmar conexión exitosa

---

## 💡 Solución Rápida: Raw Variables

Si nada funciona, agrega estas variables manualmente:

```bash
# En Railway → Tu servicio → Variables

DATABASE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
PGUSER=postgres
PGPASSWORD=lGlWZraCZSxrVpXRxzPvrOFDEmksFqcS
PGHOST=postgres.railway.internal
PGPORT=5432
PGDATABASE=railway
DDL_AUTO=update
JWT_SECRET=MySecretKeyForJWTTokenGenerationThatNeedsToBeAtLeast256BitsLongForHS256Algorithm
```

---

## 📝 Cambios Realizados en el Código

### Dockerfile:
```dockerfile
# ANTES
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# DESPUÉS
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
```

### application-railway.properties:
```properties
# Agregado logging para diagnosticar conexión
logging.level.org.springframework.jdbc.datasource=DEBUG
logging.level.com.zaxxer.hikari=DEBUG

# Corregido (state en minúsculas)
management.health.livenessstate.enabled=true
management.health.readinessstate.enabled=true
```

---

**Siguiente paso:** Hacer push y verificar variables en Railway 🚀
