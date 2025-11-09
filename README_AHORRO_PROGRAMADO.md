# 💰 Módulo de Ahorro Programado - EasySave

## Descripción General

El módulo de **Metas de Ahorro** permite a los usuarios crear planes de ahorro basados en su balance disponible (ingresos - gastos), programando cuotas automáticas con fechas específicas para fomentar una cultura de ahorro.

---

## 🎯 Características Principales

### 1. **Creación de Metas de Ahorro**
- Definir un nombre y monto objetivo para la meta
- Establecer número de cuotas
- Seleccionar frecuencia (Semanal, Quincenal, Mensual)
- Calcular automáticamente el valor de cada cuota
- Generar calendario de pagos con fechas futuras

### 2. **Sugerencias Inteligentes**
- Calcular automáticamente cuánto ahorrar basado en un porcentaje del balance
- Sugerir plan de ahorro personalizado según disponibilidad
- Proyectar fechas de cumplimiento de la meta

### 3. **Seguimiento de Progreso**
- Ver porcentaje de avance de la meta
- Registrar pago de cuotas
- Monitorear cuotas pendientes y vencidas
- Estado de la meta (Activa, Completada, Cancelada)

---

## 📊 Modelo de Datos

### Entidad: MetaAhorro
```java
- id: Long (PK)
- nombreMeta: String
- montoObjetivo: Double
- montoAhorrado: Double
- numeroCuotas: Integer
- valorCuota: Double
- frecuenciaCuota: String (SEMANAL, QUINCENAL, MENSUAL)
- fechaInicio: LocalDate
- fechaFinEstimada: LocalDate
- estado: String (ACTIVA, COMPLETADA, CANCELADA)
- porcentajeBalance: Double
- usuario: Usuario (ManyToOne)
- cuotas: List<CuotaAhorro> (OneToMany)
```

### Entidad: CuotaAhorro
```java
- id: Long (PK)
- numeroCuota: Integer
- montoCuota: Double
- fechaProgramada: LocalDate
- fechaPago: LocalDate
- estado: String (PENDIENTE, PAGADA, VENCIDA)
- metaAhorro: MetaAhorro (ManyToOne)
```

---

## 🚀 Endpoints API

### Base URL
```
http://localhost:8080/api/v1/usuario-service
```

---

### 1. 📝 Crear Meta de Ahorro
**Endpoint:** `POST /usuarios/{usuarioId}/metas-ahorro`

**Descripción:** Crea una nueva meta de ahorro para un usuario.

**Request Body:**
```json
{
  "nombreMeta": "Vacaciones 2026",
  "montoObjetivo": 2000000,
  "numeroCuotas": 10,
  "frecuenciaCuota": "MENSUAL",
  "porcentajeBalance": 25
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nombreMeta": "Vacaciones 2026",
  "montoObjetivo": 2000000.0,
  "montoAhorrado": 0.0,
  "montoFaltante": 2000000.0,
  "progresoPorcentaje": 0.0,
  "numeroCuotas": 10,
  "valorCuota": 200000.0,
  "frecuenciaCuota": "MENSUAL",
  "fechaInicio": "2025-11-05",
  "fechaFinEstimada": "2026-08-05",
  "estado": "ACTIVA",
  "cuotasPagadas": 0,
  "cuotasPendientes": 10,
  "proximasCuotas": [
    {
      "id": 1,
      "numeroCuota": 1,
      "montoCuota": 200000.0,
      "fechaProgramada": "2025-11-05",
      "estado": "PENDIENTE"
    },
    {
      "id": 2,
      "numeroCuota": 2,
      "montoCuota": 200000.0,
      "fechaProgramada": "2025-12-05",
      "estado": "PENDIENTE"
    }
  ]
}
```

---

### 2. 📋 Listar Metas de Ahorro
**Endpoint:** `GET /usuarios/{usuarioId}/metas-ahorro`

**Descripción:** Obtiene todas las metas de ahorro de un usuario.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombreMeta": "Vacaciones 2026",
    "montoObjetivo": 2000000.0,
    "montoAhorrado": 400000.0,
    "valorCuota": 200000.0,
    "estado": "ACTIVA"
  }
]
```

---

### 3. ✅ Listar Metas Activas
**Endpoint:** `GET /usuarios/{usuarioId}/metas-ahorro/activas`

**Descripción:** Obtiene solo las metas activas del usuario.

---

### 4. 🔍 Ver Detalles de Meta
**Endpoint:** `GET /metas-ahorro/{metaId}`

**Descripción:** Obtiene información detallada de una meta específica, incluyendo las próximas cuotas.

---

### 5. 💳 Pagar Cuota
**Endpoint:** `POST /metas-ahorro/{metaId}/cuotas/{cuotaId}/pagar`

**Descripción:** Registra el pago de una cuota específica.

**Response (200 OK):**
```json
{
  "message": "Cuota pagada exitosamente",
  "meta": {
    "montoAhorrado": 200000.0,
    "progresoPorcentaje": 10.0,
    "cuotasPagadas": 1,
    "cuotasPendientes": 9
  }
}
```

---

### 6. 🧮 Calcular Sugerencia de Ahorro
**Endpoint:** `GET /usuarios/{usuarioId}/sugerencia-ahorro`

**Descripción:** Calcula una sugerencia de ahorro basada en el balance del usuario.

**Query Parameters:**
- `porcentajeBalance` (opcional, default: 20): % del balance a ahorrar
- `numeroCuotas` (opcional, default: 12): Cantidad de cuotas
- `frecuencia` (opcional, default: MENSUAL): SEMANAL, QUINCENAL, MENSUAL

**Ejemplo:**
```
GET /usuarios/2/sugerencia-ahorro?porcentajeBalance=30&numeroCuotas=6&frecuencia=MENSUAL
```

**Response (200 OK):**
```json
{
  "mensaje": "Sugerencia de ahorro calculada exitosamente",
  "sugerencia": {
    "montoObjetivo": 675000.0,
    "numeroCuotas": 6,
    "valorCuota": 112500.0,
    "frecuenciaCuota": "MENSUAL",
    "fechaInicio": "2025-11-05",
    "fechaFinEstimada": "2026-04-05",
    "proximasCuotas": [...]
  },
  "nota": "Esta es una sugerencia. Puedes crear la meta con estos valores o modificarlos."
}
```

**Ejemplo de cálculo:**
```
Balance del usuario = 2,250,000 (Ingresos: 3,700,000 - Gastos: 1,450,000)
Porcentaje a ahorrar = 30%
Monto sugerido = 2,250,000 × 0.30 = 675,000
Cuotas = 6
Valor por cuota = 675,000 / 6 = 112,500
```

---

### 7. ❌ Cancelar Meta
**Endpoint:** `DELETE /metas-ahorro/{metaId}`

**Descripción:** Cancela una meta de ahorro.

---

### 8. 🔄 Actualizar Cuotas Vencidas
**Endpoint:** `POST /metas-ahorro/actualizar-vencidas`

**Descripción:** Marca como vencidas las cuotas pendientes cuya fecha programada ya pasó.

---

## 💡 Casos de Uso

### Caso 1: Usuario quiere ahorrar para vacaciones

1. El usuario consulta su balance actual:
   ```
   GET /usuarios/2/resumen-financiero
   Balance: $2,250,000
   ```

2. Solicita una sugerencia de ahorro (30% del balance en 10 meses):
   ```
   GET /usuarios/2/sugerencia-ahorro?porcentajeBalance=30&numeroCuotas=10&frecuencia=MENSUAL
   ```

3. Crea la meta de ahorro:
   ```
   POST /usuarios/2/metas-ahorro
   {
     "nombreMeta": "Vacaciones Caribe",
     "montoObjetivo": 675000,
     "numeroCuotas": 10,
     "frecuenciaCuota": "MENSUAL"
   }
   ```

4. El sistema genera automáticamente:
   - 10 cuotas de $67,500 cada una
   - Fechas programadas mensuales
   - Fecha estimada de cumplimiento: Agosto 2026

### Caso 2: Pago de cuotas

1. Cuando llega la fecha de pago, el usuario paga la cuota:
   ```
   POST /metas-ahorro/1/cuotas/1/pagar
   ```

2. El sistema actualiza:
   - Monto ahorrado: $67,500
   - Progreso: 10%
   - Estado de la cuota: PAGADA
   - Fecha de pago: 2025-11-05

---

## 📈 Beneficios

✅ **Educación Financiera:** Fomenta el hábito del ahorro  
✅ **Planificación:** Proyección clara de metas alcanzables  
✅ **Motivación:** Seguimiento visual del progreso  
✅ **Flexibilidad:** Diferentes frecuencias de pago  
✅ **Realismo:** Basado en el balance real del usuario  

---

## 🔧 Configuración Técnica

### Dependencias Necesarias
- Spring Boot 3.3.3
- Spring Data JPA
- PostgreSQL / H2 Database
- Lombok

### Configuración de Base de Datos
Las tablas se crean automáticamente con `spring.jpa.hibernate.ddl-auto=create-drop`

---

## 📝 Notas Técnicas

- Las cuotas se generan automáticamente al crear la meta
- El cálculo de fechas considera la frecuencia seleccionada
- El estado de la meta cambia automáticamente a "COMPLETADA" cuando se paga la última cuota
- Las cuotas vencidas se pueden actualizar con el endpoint dedicado
- La relación Usuario-MetaAhorro-CuotaAhorro usa CASCADE para mantener integridad

---

## 🎓 Ejemplo Completo de Flujo

```bash
# 1. Ver balance actual
GET /usuarios/2/resumen-financiero

# 2. Calcular sugerencia
GET /usuarios/2/sugerencia-ahorro?porcentajeBalance=25&numeroCuotas=12&frecuencia=MENSUAL

# 3. Crear meta
POST /usuarios/2/metas-ahorro
{
  "nombreMeta": "Fondo de Emergencia",
  "montoObjetivo": 562500,
  "numeroCuotas": 12,
  "frecuenciaCuota": "MENSUAL"
}

# 4. Ver detalles de la meta
GET /metas-ahorro/1

# 5. Pagar primera cuota
POST /metas-ahorro/1/cuotas/1/pagar

# 6. Ver progreso
GET /metas-ahorro/1
```

---

## 🚀 Próximas Mejoras

- [ ] Notificaciones de cuotas próximas a vencer
- [ ] Gráficas de progreso
- [ ] Múltiples metas simultáneas con prioridades
- [ ] Ajuste dinámico de cuotas según cambios en el balance
- [ ] Reportes de ahorro mensuales/anuales

---

**Desarrollado con ❤️ para promover la cultura del ahorro financiero**
