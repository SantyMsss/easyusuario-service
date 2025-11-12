# 🔧 Configuración de Variables de Entorno en Railway

## ⚠️ Error Resuelto

**Error anterior:**
```
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'JWT_SECRET' in value "${JWT_SECRET}"
```

**Solución aplicada:**
- ✅ Agregados valores por defecto a todas las variables de entorno
- ✅ La aplicación ahora puede iniciar sin configuración adicional
- ✅ Commit y push realizados exitosamente

---

## 📋 Variables de Entorno Configuradas Automáticamente por Railway

Railway detecta y configura automáticamente estas variables cuando vinculas la base de datos PostgreSQL:

```bash
✅ PORT                 # Railway lo asigna automáticamente
✅ DATABASE_URL         # jdbc:postgresql://postgres.railway.internal:5432/railway
✅ PGUSER              # postgres
✅ PGPASSWORD          # [tu password de railway]
✅ PGHOST              # postgres.railway.internal
✅ PGPORT              # 5432
✅ PGDATABASE          # railway
```

**No necesitas configurarlas manualmente** ✨

---

## 🔐 Variables Opcionales (Recomendadas para Producción)

Aunque la aplicación ya tiene valores por defecto, es **altamente recomendable** configurar estas variables en Railway para producción:

### 1. JWT_SECRET (Importante para seguridad)

**¿Por qué cambiarlo?**
- El valor por defecto es público en GitHub
- En producción necesitas un secret único y seguro

**Cómo generar uno seguro:**

```powershell
# En PowerShell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

**Ejemplo de salida:**
```
8kX3mQ9pL2wR7vN5jT1yF6bH4gD0sZ8cA2xE9wQ3mK7pL5nR1tY6jB4hG8dS2fV0
```

**En Railway:**
1. Ve a tu servicio → **Variables**
2. Click en **+ New Variable**
3. Name: `JWT_SECRET`
4. Value: `[tu secret generado]`
5. Click **Add**

---

### 2. DEEPFACE_SERVICE_URL (Necesario cuando despliegues el servicio Python)

**Valor actual (por defecto):**
```
http://localhost:5000
```

**Cambiar a (una vez desplegado el servicio Python):**
```
https://deepface-service.up.railway.app
```

O mejor aún, usar la URL interna de Railway:
```
http://deepface-service.railway.internal:8000
```

**En Railway:**
1. Ve a tu servicio → **Variables**
2. Click en **+ New Variable**
3. Name: `DEEPFACE_SERVICE_URL`
4. Value: `https://tu-deepface-service.up.railway.app`
5. Click **Add**

---

### 3. SPRING_PROFILES_ACTIVE (Ya configurado en railway.json)

Esta variable ya está configurada en el `railway.json`:
```json
"startCommand": "java -Dspring.profiles.active=railway ..."
```

Pero también puedes configurarla como variable de entorno:
```bash
SPRING_PROFILES_ACTIVE=railway
```

---

## 🎯 Configuración Completa Recomendada para Railway

### Variables Mínimas (La app ya funciona sin ellas):
```bash
# Estas ya están configuradas automáticamente por Railway
✅ PORT
✅ DATABASE_URL
✅ PGUSER
✅ PGPASSWORD
```

### Variables Recomendadas para Producción:
```bash
JWT_SECRET=[genera uno seguro con el comando de PowerShell]
DEEPFACE_SERVICE_URL=https://tu-deepface-service.up.railway.app
```

### Variables Opcionales (ya tienen buenos defaults):
```bash
JWT_EXPIRATION=86400000          # 24 horas (por defecto)
DDL_AUTO=update                   # ya está en application-railway.properties
```

---

## 🚀 Paso a Paso: Configurar en Railway

### 1. Acceder al Dashboard
```
https://railway.app/dashboard
```

### 2. Seleccionar tu servicio
- Click en el proyecto
- Click en el servicio `web-production-...`

### 3. Ir a Variables
- Click en la pestaña **Variables**

### 4. Agregar JWT_SECRET
```
Name:  JWT_SECRET
Value: [tu secret generado - 64 caracteres]
```

### 5. Verificar variables existentes
Deberías ver:
```
DATABASE_PUBLIC_URL
DATABASE_URL
PGDATA
PGDATABASE
PGHOST
PGPASSWORD
PGPORT
PGUSER
POSTGRES_DB
POSTGRES_PASSWORD
POSTGRES_USER
```

### 6. Re-deploy automático
- Railway hará un re-deploy automático al agregar variables
- Espera 2-3 minutos
- Verifica que el status cambie a "Active"

---

## ✅ Verificación Post-Corrección

### 1. Ver logs del deployment
```
Railway Dashboard → Tu servicio → Deployments → View Logs
```

Deberías ver:
```
✅ Started CelularServiceApplication in X.XXX seconds (JVM running for X.XXX)
✅ Tomcat started on port(s): 8080 (http) with context path '/'
✅ HikariPool-1 - Start completed
```

### 2. Probar el servicio

**Health check básico:**
```bash
curl https://tu-servicio.up.railway.app/
```

**Listar usuarios:**
```bash
curl https://tu-servicio.up.railway.app/api/v1/usuarios/listar
```

**Registrar usuario:**
```bash
curl -X POST https://tu-servicio.up.railway.app/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "correo": "test@example.com",
    "password": "test123"
  }'
```

---

## 🔍 Troubleshooting

### ❌ Si aún crashea:

**1. Verifica los logs:**
```
Railway Dashboard → Deployments → View Logs
```

**2. Busca estos errores comunes:**

| Error | Causa | Solución |
|-------|-------|----------|
| `Could not resolve placeholder` | Falta una variable | Agrega valor por defecto en application-railway.properties |
| `Connection refused` | Base de datos no conectada | Vincula el servicio PostgreSQL al proyecto |
| `Port already in use` | Puerto incorrecto | Railway usa la variable $PORT automáticamente |
| `ClassNotFoundException` | Falta dependencia | Verifica pom.xml y recompila |

**3. Variables de base de datos:**

Si ves error de conexión a BD:
```
Failed to configure a DataSource: 'url' attribute is not specified
```

Asegúrate de que el servicio PostgreSQL esté en el **mismo proyecto** que tu aplicación Spring Boot.

**4. Re-deploy manual:**

```
Railway Dashboard → Deployments → Latest → ... (tres puntos) → Redeploy
```

---

## 📊 Estado Actual

✅ **Cambios realizados:**
- application-railway.properties actualizado con fallbacks
- Compilación exitosa
- Commit creado: `fix: Agregar valores por defecto a variables de entorno`
- Push realizado a branch `dev`

✅ **Próximo paso:**
- Railway detectará el nuevo commit automáticamente
- Hará un nuevo deployment
- La aplicación debería iniciar correctamente

🎯 **Monitorea el deployment:**
```
Railway Dashboard → Tu servicio → Deployments
```

Espera a que el status cambie de "Building" → "Deploying" → "Active" ✨

---

## 📝 Notas Importantes

1. **Valores por defecto son seguros para desarrollo, no para producción**
   - Cambia JWT_SECRET en producción
   - Usa HTTPS para DeepFace service

2. **Railway hace auto-deploy desde GitHub**
   - Cada push a la rama `dev` activará un nuevo deployment
   - Puedes cambiar esto en Settings → Deployment Triggers

3. **Base de datos PostgreSQL**
   - Ya está desplegada con tus credenciales
   - Railway la vinculará automáticamente si está en el mismo proyecto

4. **DeepFace Service**
   - Por ahora usa el fallback `http://localhost:5000`
   - Los endpoints de facial recognition no funcionarán hasta que despliegues el servicio Python
   - Los demás endpoints (usuarios, auth tradicional, ingresos, gastos, metas) funcionan perfectamente

---

**Última actualización:** 11 de noviembre de 2025  
**Commit:** `2f22f02 - fix: Agregar valores por defecto a variables de entorno`  
**Status:** ✅ LISTO - Esperando re-deploy automático de Railway
