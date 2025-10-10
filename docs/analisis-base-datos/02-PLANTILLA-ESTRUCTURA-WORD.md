# PLANTILLA ESTRUCTURA PARA DOCUMENTOS WORD - PROYECTO VIVEMEDELLIN

## GUÍA DE FORMATO PROFESIONAL PARA ENTREGABLES

---

### CONFIGURACIÓN INICIAL DEL DOCUMENTO

#### Márgenes y Página
- **Márgenes:** Superior 2.5cm, Inferior 2.5cm, Izquierdo 3cm, Derecho 2cm
- **Tamaño:** A4 (21 x 29.7 cm)
- **Orientación:** Vertical
- **Fuente principal:** Calibri 11pt para texto, Arial 12pt para títulos

#### Numeración y Encabezados
- **Encabezado:** Logo + "ViveMedellin - Análisis de Base de Datos"
- **Pie de página:** Página X de Y + Fecha + "Confidencial - Uso Interno"
- **Numeración:** Iniciar desde página 2 (después de portada)

---

## ESTRUCTURA DETALLADA DEL DOCUMENTO

### 1. PORTADA (Página 1)
```
[LOGO ALCALDÍA DE MEDELLÍN]

PROYECTO VIVEMEDELLIN
PLATAFORMA DE GESTIÓN DE EVENTOS CULTURALES

ANÁLISIS DE BASE DE DATOS
Entregables Técnicos - Módulo 2

Versión 1.0
Septiembre 2025

Elaborado por: [Nombre del Arquitecto]
Revisado por: [Nombre del Product Owner]
Aprobado por: [Nombre del Director Técnico]

Secretaría de Cultura Ciudadana
Alcaldía de Medellín
```

### 2. CONTROL DE VERSIONES (Página 2)

| Versión | Fecha | Autor | Descripción de Cambios |
|---------|-------|-------|------------------------|
| 1.0 | Sep 2025 | Carlos Zuluaga | Versión inicial - Análisis completo BD |
| | | | |

### 3. TABLA DE CONTENIDO (Página 3)
```
1. RESUMEN EJECUTIVO ................................. 4
2. DEFINICIÓN DE ENTIDADES ........................... 5
   2.1 Contexto del Negocio .......................... 5
   2.2 Reglas de Negocio ............................. 6
   2.3 Entidades Identificadas ....................... 8
3. PRINCIPALES CONSULTAS IDENTIFICADAS ............... 10
   3.1 Consultas de Negocio Críticas ................. 10
   3.2 Consultas Operacionales ....................... 12
   3.3 Consultas de Análisis y BI .................... 14
4. MODELO LÓGICO (MER) ............................... 16
   4.1 Diagrama Entidad-Relación ..................... 16
   4.2 Descripción de Relaciones ..................... 18
   4.3 Reglas de Integridad .......................... 19
5. MODELO FÍSICO ..................................... 20
   5.1 Tecnología Seleccionada ....................... 20
   5.2 Definición de Tablas .......................... 21
   5.3 Índices y Performance ......................... 28
6. ANEXOS Y REFERENCIAS .............................. 30
```

---

## ESTILOS RECOMENDADOS PARA WORD

### Títulos y Subtítulos
- **Título 1 (H1):** Arial 16pt, Negrita, Color Azul Medellín (#0066CC)
- **Título 2 (H2):** Arial 14pt, Negrita, Color Gris Oscuro (#333333)
- **Título 3 (H3):** Arial 12pt, Negrita, Color Negro
- **Título 4 (H4):** Calibri 11pt, Negrita, Color Negro

### Texto y Párrafos
- **Texto normal:** Calibri 11pt, Interlineado 1.15, Justificado
- **Texto código:** Courier New 10pt, Fondo gris claro
- **Énfasis:** Calibri 11pt, Cursiva
- **Importante:** Calibri 11pt, Negrita

### Tablas
- **Encabezado:** Fondo azul medellín (#0066CC), texto blanco, negrita
- **Filas alternas:** Fondo gris claro (#F5F5F5)
- **Bordes:** Línea sólida 1pt, color gris (#CCCCCC)

### Colores Institucionales
- **Azul Medellín:** #0066CC (Principal)
- **Verde Medellín:** #009639 (Secundario)
- **Naranja Medellín:** #FF6600 (Acento)
- **Gris Corporativo:** #666666 (Texto secundario)

---

## SECCIONES ESPECÍFICAS - FORMATO DETALLADO

### SECCIÓN 1: DEFINICIÓN DE ENTIDADES

#### Formato de Reglas de Negocio
```
RN-XX: [Título de la Regla]
📋 Descripción: [Descripción clara y concisa]
🎯 Impacto: [Alto/Medio/Bajo]
📊 Entidades Afectadas: [Lista de entidades]
✅ Validaciones: [Controles necesarios]
```

#### Tabla de Entidades (Formato Word)
| **Entidad** | **Propósito** | **Cardinalidad** | **Observaciones** |
|-------------|---------------|------------------|-------------------|
| Evento | Actividad cultural principal | 1:N con Valoraciones | Entidad central del sistema |

### SECCIÓN 2: CONSULTAS PRINCIPALES

#### Formato de Consultas
```
CN-XX: [Nombre de la Consulta]

Propósito: [Para qué sirve esta consulta]
Frecuencia: [Diaria/Semanal/Bajo demanda]
Complejidad: [Baja/Media/Alta]
Performance: [< Xms respuesta esperada]

SQL:
[Código SQL con formato y comentarios]

Índices requeridos:
- [Lista de índices necesarios]

Consideraciones:
- [Puntos importantes sobre la consulta]
```

### SECCIÓN 3: MODELO LÓGICO (MER)

#### Inserción de Diagramas
- **Formato:** PNG o SVG de alta resolución (mínimo 300 DPI)
- **Tamaño:** Máximo ancho de página (18cm)
- **Ubicación:** Centrado con título descriptivo
- **Leyenda:** Incluir explicación de símbolos y colores

#### Descripción de Relaciones (Tabla)
| **Entidad Origen** | **Entidad Destino** | **Tipo** | **Cardinalidad** | **Descripción** |
|-------------------|-------------------|----------|------------------|-----------------|
| Organizador | Evento | Identifica | 1:N | Un organizador puede crear múltiples eventos |

### SECCIÓN 4: MODELO FÍSICO

#### Definición de Tablas (Formato)
```
TABLA: nombre_tabla
Propósito: [Descripción de para qué sirve la tabla]

Columnas:
┌─────────────────┬──────────┬──────┬─────────┬──────────────────┐
│ Campo           │ Tipo     │ Tam. │ Nulos   │ Descripción      │
├─────────────────┼──────────┼──────┼─────────┼──────────────────┤
│ id              │ UUID     │ 36   │ NO      │ Clave primaria   │
│ nombre          │ VARCHAR  │ 200  │ NO      │ Nombre completo  │
└─────────────────┴──────────┴──────┴─────────┴──────────────────┘

Restricciones:
- PK: id
- FK: [Referencias foráneas]
- UK: [Claves únicas]
- CK: [Restricciones de validación]

Índices:
- idx_tabla_campo: [Descripción del propósito]
```

---

## ELEMENTOS GRÁFICOS Y VISUALES

### Iconografía Sugerida
- 🏛️ Entidades principales
- 🔗 Relaciones
- 📊 Consultas de análisis
- ⚡ Consultas de performance
- 🛡️ Restricciones de seguridad
- 📈 Métricas y KPIs

### Diagramas y Esquemas
1. **Diagrama ER:** Usar herramientas como Lucidchart o Draw.io
2. **Diagramas de flujo:** Para procesos complejos de consultas
3. **Esquemas de arquitectura:** Para mostrar capas de datos

### Cajas de Información
```
💡 BUENA PRÁCTICA
[Recomendación técnica importante]

⚠️ ADVERTENCIA
[Consideración crítica o limitación]

🎯 OBJETIVO DE NEGOCIO
[Alineación con necesidades del negocio]

📋 REQUERIMIENTO TÉCNICO
[Especificación técnica obligatoria]
```

---

## ANEXOS Y DOCUMENTOS COMPLEMENTARIOS

### Anexo A: Diccionario de Datos Completo
- Tabla exhaustiva de todos los campos
- Dominios de valores permitidos
- Reglas de validación específicas

### Anexo B: Scripts SQL de Creación
- Scripts completos de DDL
- Scripts de datos de prueba
- Scripts de índices y optimización

### Anexo C: Casos de Prueba
- Casos de prueba para validar integridad
- Escenarios de carga de datos
- Pruebas de performance

### Anexo D: Documentación Técnica
- Configuración de PostgreSQL
- Procedimientos de backup
- Monitoreo y mantenimiento

---

## CHECKLIST FINAL ANTES DE ENTREGA

### Revisión de Contenido
- [ ] Todos los entregables están completos
- [ ] Las consultas SQL están probadas
- [ ] Los diagramas son legibles y precisos
- [ ] La documentación está actualizada

### Revisión de Formato
- [ ] Estilos consistentes aplicados
- [ ] Numeración de páginas correcta
- [ ] Tabla de contenido actualizada
- [ ] Referencias cruzadas funcionando

### Revisión Técnica
- [ ] Nomenclatura estándar aplicada
- [ ] Restricciones de integridad definidas
- [ ] Performance considerada
- [ ] Seguridad evaluada

### Revisión de Negocio
- [ ] Reglas de negocio cubiertas
- [ ] Casos de uso contemplados
- [ ] Escalabilidad considerada
- [ ] Mantenibilidad evaluada

---

**Plantilla creada para:** Proyecto ViveMedellin  
**Fecha:** Septiembre 2025  
**Uso:** Documentos técnicos oficiales  
**Versión:** 1.0