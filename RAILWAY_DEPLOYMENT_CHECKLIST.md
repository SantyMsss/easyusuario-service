# ✅ Checklist de Despliegue en Railway - Spring Boot

## 📦 Archivos de Configuración

### ✅ Archivos Principales
- [x] `pom.xml` - Configurado con `<finalName>` explícito
- [x] `railway.json` - Configuración de build y deploy
- [x] `Procfile` - Comando de inicio alternativo
- [x] `.gitignore` - Archivos a excluir del repositorio
- [x] `application.properties` - Configuración con variables de entorno
- [x] `application-railway.properties` - Configuración específica para Railway

### ✅ Compilación Exitosa
```
✅ BUILD SUCCESS
✅ JAR generado: celular-service-0.0.1-SNAPSHOT.jar (50.4 MB)
✅ 36 archivos Java compilados correctamente
```

## 🔧 Configuración Revisada

### 1. `pom.xml`
```xml
✅ Spring Boot: 3.3.3
✅ Java: 17
✅ finalName: celular-service-0.0.1-SNAPSHOT
✅ spring-boot-maven-plugin configurado
✅ Dependencias: JPA, Web, Security, JWT, PostgreSQL
```

### 2. `railway.json`
```json
✅ Builder: NIXPACKS
✅ Build command: mvn clean package -DskipTests
✅ Start command: java -Dspring.profiles.active=railway -jar target/celular-service-0.0.1-SNAPSHOT.jar
✅ Restart policy: ON_FAILURE (3 reintentos)
```

### 3. `application.properties`
```properties
✅ server.port=${PORT:8080}
✅ spring.datasource.url=${DATABASE_URL:...}
✅ spring.datasource.username=${PGUSER:postgres}
✅ spring.datasource.password=${PGPASSWORD:...}
✅ spring.jpa.hibernate.ddl-auto=${DDL_AUTO:create-drop}
✅ app.jwt.secret=${JWT_SECRET:...}
✅ deepface.service.url=${DEEPFACE_SERVICE_URL:...}
```

### 4. `application-railway.properties`
```properties
✅ spring.jpa.hibernate.ddl-auto=update (para producción)
✅ spring.jpa.show-sql=false (optimizado)
✅ HikariCP pool configurado (5 max, 2 min)
✅ Logging nivel INFO
✅ H2 console deshabilitada
✅ Sin valores hardcodeados
```

### 5. `.gitignore`
```
✅ target/ excluido
✅ .env y variables de entorno excluidas
✅ Archivos de IDE excluidos
✅ Logs excluidos
✅ Python venv/ excluido
```

## 🚀 Variables de Entorno Requeridas en Railway

### Obligatorias:
```bash
✅ PORT                    # Railway lo asigna automáticamente
✅ DATABASE_URL            # jdbc:postgresql://postgres.railway.internal:5432/railway
✅ PGUSER                  # postgres
✅ PGPASSWORD              # lGlWZraCZSxrVpXRxzPvrOFDEmksFqcS
✅ SPRING_PROFILES_ACTIVE  # railway
```

### Recomendadas:
```bash
✅ DDL_AUTO                # update (para no perder datos)
✅ JWT_SECRET              # [Generar uno seguro de 64+ caracteres]
✅ JWT_EXPIRATION          # 86400000 (24 horas)
✅ DEEPFACE_SERVICE_URL    # https://deepface-service.up.railway.app
```

## 📝 Próximos Pasos para el Push

### 1. Revisar cambios pendientes
```powershell
git status
```

### 2. Agregar archivos al staging
```powershell
git add .
```

### 3. Crear commit
```powershell
git commit -m "feat: Configuración completa para despliegue en Railway

- Añadida configuración railway.json
- Actualizado pom.xml con finalName explícito
- Creado application-railway.properties para producción
- Actualizado .gitignore con exclusiones de ambiente
- Añadido Procfile para Railway
- Variables de entorno configuradas con fallbacks
- Pool de conexiones Hikari optimizado
- Compilación exitosa: JAR 50.4MB
"
```

### 4. Push a GitHub
```powershell
git push origin dev
```

## 🎯 Despliegue en Railway

### Opción A: Desde GitHub (Recomendado)
1. Ve a https://railway.app/dashboard
2. Click en "New Project"
3. Selecciona "Deploy from GitHub repo"
4. Elige el repositorio `easyusuario-service`
5. Railway detectará automáticamente el proyecto Java
6. Configura las variables de entorno
7. Deploy automático

### Opción B: Railway CLI
```powershell
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link al proyecto
railway link

# Deploy
railway up
```

## 🔍 Verificaciones Post-Despliegue

### 1. Revisar logs
```
Railway Dashboard → Tu servicio → Deployments → View Logs
```

Buscar:
```
✅ Started CelularServiceApplication in X.XXX seconds
✅ Tomcat started on port(s): XXXX (http)
✅ HikariPool-1 - Start completed
```

### 2. Probar endpoints
```bash
# Health check (si tienes actuator)
curl https://tu-servicio.up.railway.app/actuator/health

# Listar usuarios
curl https://tu-servicio.up.railway.app/api/v1/usuarios/listar

# Health de DeepFace
curl https://tu-servicio.up.railway.app/api/v1/auth/deepface-health
```

### 3. Verificar base de datos
```sql
-- Conectarse desde Railway Dashboard
-- Database → Connect → PSQL

-- Verificar tablas
\dt

-- Debería mostrar:
-- usuario
-- face_encoding
-- ingreso
-- gasto
-- meta_ahorro
-- cuota_ahorro
```

## ⚠️ Puntos Importantes

1. **DDL_AUTO en Producción**: Usar `update` nunca `create-drop`
2. **JWT_SECRET**: Generar uno seguro y único para producción
3. **DeepFace Service**: Debe desplegarse PRIMERO antes del Spring Boot
4. **Logs**: Mantener en INFO en producción para no llenar disco
5. **Pool de conexiones**: Ajustar según el plan de Railway
6. **CORS**: Configurar origins específicos para tu frontend

## 📊 Recursos del Proyecto

- **Repositorio**: github.com/SantyMsss/easyusuario-service
- **Branch actual**: dev
- **Java Version**: 17
- **Spring Boot**: 3.3.3
- **Base de datos**: PostgreSQL en Railway
- **Tamaño JAR**: 50.4 MB

---

## ✅ Estado Actual: LISTO PARA PUSH

**Última verificación**: 11 de noviembre de 2025
**Compilación**: ✅ EXITOSA
**JAR generado**: ✅ VERIFICADO
**Configuración**: ✅ COMPLETA

🚀 **Puedes proceder con el `git push` con confianza**
