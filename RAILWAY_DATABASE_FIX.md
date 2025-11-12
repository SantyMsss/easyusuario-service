# 🚂 Fix: Configuración de Base de Datos en Railway

## ❌ Problema Identificado

Tu aplicación está intentando conectarse a `localhost:5432` en lugar de usar la base de datos PostgreSQL de Railway.

**Error en el log:**
```
Connection to localhost:5432 refused
jdbcUrl.........................jdbc:postgresql://localhost:5432/railway
```

## ✅ Solución Implementada

### 1. Archivo `application-railway.properties` Corregido

El archivo ha sido actualizado para usar las variables de entorno correctas de Railway:

```properties
spring.datasource.url=jdbc:postgresql://${PGHOST:localhost}:${PGPORT:5432}/${PGDATABASE:railway}
spring.datasource.username=${PGUSER:postgres}
spring.datasource.password=${PGPASSWORD:postgres}
```

## 🔧 Pasos para Configurar Railway

### Paso 1: Verificar que tienes un Servicio PostgreSQL

1. Ve a tu proyecto en Railway
2. Asegúrate de tener un servicio PostgreSQL agregado
3. Si no lo tienes, añádelo:
   - Click en **"+ New"**
   - Selecciona **"Database"**
   - Selecciona **"Add PostgreSQL"**

### Paso 2: Verificar Variables de Entorno

Railway PostgreSQL proporciona automáticamente estas variables:

```bash
PGHOST=postgres.railway.internal     # Host de la base de datos
PGPORT=5432                          # Puerto
PGDATABASE=railway                   # Nombre de la base de datos
PGUSER=postgres                      # Usuario
PGPASSWORD=<contraseña-generada>     # Contraseña
DATABASE_URL=postgresql://postgres:password@host:5432/railway
```

### Paso 3: Verificar Perfil Activo de Spring

Asegúrate de que tu aplicación esté usando el perfil `railway`:

1. Ve a tu servicio de Spring Boot en Railway
2. Ve a **"Variables"**
3. Verifica o agrega:
   ```
   SPRING_PROFILES_ACTIVE=railway
   ```

### Paso 4: Variables Adicionales Necesarias

Agrega estas variables de entorno en Railway:

```bash
# JWT Configuration
JWT_SECRET=TuClaveSecretaSuperSeguraParaProduccionQueDebeSerMuyLarga256Bits

# JWT Expiration (24 horas en milisegundos)
JWT_EXPIRATION=86400000

# Puerto (Railway lo asigna automáticamente)
PORT=${PORT}
```

### Paso 5: Redeploy

1. Guarda los cambios en tu código
2. Haz commit y push a tu repositorio:
   ```bash
   git add .
   git commit -m "fix: corregir configuración de base de datos para Railway"
   git push origin main
   ```
3. Railway detectará los cambios y hará redeploy automáticamente

## 🔍 Verificar Conexión

### Opción 1: Ver Logs en Railway

1. Ve a tu servicio en Railway
2. Click en **"Deployments"**
3. Click en el último deployment
4. Revisa los logs y busca:
   ```
   jdbcUrl.........................jdbc:postgresql://postgres.railway.internal:5432/railway
   HikariPool-1 - Starting...
   HikariPool-1 - Added connection
   Started CelularServiceApplication
   ```

### Opción 2: Probar Health Check

Una vez desplegado, prueba:
```
https://tu-app.up.railway.app/actuator/health
```

Deberías ver:
```json
{
  "status": "UP"
}
```

## 📝 Configuración Completa de Variables de Entorno en Railway

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=railway

# Database (Railway las proporciona automáticamente desde PostgreSQL)
PGHOST=<proporcionado por Railway>
PGPORT=<proporcionado por Railway>
PGDATABASE=<proporcionado por Railway>
PGUSER=<proporcionado por Railway>
PGPASSWORD=<proporcionado por Railway>

# Application
PORT=${PORT}

# JWT
JWT_SECRET=TuClaveSecretaSuperSeguraParaProduccionQueDebeSerMuyLarga256Bits
JWT_EXPIRATION=86400000

# DDL (opcional - usar 'update' para no perder datos)
DDL_AUTO=update
```

## ⚠️ Notas Importantes

1. **NO uses `create-drop` en producción**: Cambia `DDL_AUTO` a `update` o `validate`
   
2. **Vincula los servicios**: Asegúrate de que tu aplicación Spring Boot y PostgreSQL estén en el mismo proyecto de Railway

3. **Verifica la red interna**: Railway usa `postgres.railway.internal` como host interno

4. **Primer despliegue**: La primera vez que despliegues, las tablas se crearán automáticamente si usas `update`

## 🐛 Solución de Problemas

### Si sigue sin conectarse:

1. **Verifica las variables de entorno**:
   - Ve a tu servicio → "Variables"
   - Asegúrate de que `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` estén presentes

2. **Verifica el perfil activo**:
   - Busca en los logs: `The following 1 profile is active: "railway"`

3. **Verifica la URL construida**:
   - Busca en los logs: `jdbcUrl.........................`
   - NO debe decir `localhost`

4. **Railway Reference Variables**:
   Si las variables no están disponibles, puedes referenciarlas manualmente:
   - Ve a PostgreSQL service → "Variables" → "Copy"
   - Ve a Spring Boot service → "Variables" → "Paste Variables from Another Service"
   - Selecciona el servicio PostgreSQL

## ✅ Resultado Esperado

Después de aplicar estos cambios, deberías ver en los logs:

```
jdbcUrl.........................jdbc:postgresql://postgres.railway.internal:5432/railway
username........................"postgres"
HikariPool-1 - Starting...
HikariPool-1 - Added connection
Initialized JPA EntityManagerFactory for persistence unit 'default'
Started CelularServiceApplication in X seconds
```

## 📞 Soporte

Si después de seguir estos pasos el problema persiste:

1. Comparte los logs completos del deployment
2. Comparte un screenshot de las variables de entorno (oculta las contraseñas)
3. Verifica que ambos servicios (Spring Boot y PostgreSQL) estén en el mismo proyecto
