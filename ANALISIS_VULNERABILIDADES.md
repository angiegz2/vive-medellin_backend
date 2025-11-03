# 🔒 Análisis de Vulnerabilidades de API - ViveMedellín

## 📋 Reporte de Seguridad OWASP API Security Top 10

Este documento identifica y documenta las vulnerabilidades potenciales en las APIs del sistema ViveMedellín basándose en **OWASP API Security Top 10 (2023)**.

---

## 🎯 Resumen Ejecutivo

| Categoría | Estado | Riesgo | Acción Requerida |
|-----------|--------|--------|------------------|
| Autenticación | ⚠️ Pendiente | Alto | Implementar Spring Security + JWT |
| Autorización | ⚠️ Parcial | Alto | Activar @PreAuthorize en endpoints admin |
| Rate Limiting | ❌ No implementado | Medio | Implementar Bucket4j o API Gateway |
| Validación de Datos | ✅ Implementado | Bajo | Continuar con @Valid |
| Exposición de Datos | ⚠️ Parcial | Medio | Filtrar datos sensibles en DTOs |
| Logging y Monitoreo | ✅ Básico | Bajo | Mejorar con ELK Stack |

---

## 🔴 API1:2023 - Broken Object Level Authorization (BOLA)

### Descripción
Falta de validación adecuada de que el usuario tenga permisos para acceder a un objeto específico.

### Vulnerabilidades Identificadas

#### ❌ **Crítico: Endpoints Admin sin Autenticación**

**Ubicación:** `EventoAdminController.java`

```java
@RestController
@RequestMapping("/api/admin/eventos")
// @PreAuthorize("hasRole('ADMIN')") ⚠️ COMENTADO
@CrossOrigin(origins = "*")
public class EventoAdminController {
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarEvento(@PathVariable Long id) {
        // ❌ Sin verificación de autenticación
        // ❌ Sin verificación de que el usuario puede cancelar ESTE evento
    }
}
```

**Impacto:**
- Cualquier usuario puede cancelar eventos
- Cualquier usuario puede destacar/quitar destacados
- No hay control de acceso por ID de recurso

**Solución Propuesta:**

```java
@RestController
@RequestMapping("/api/admin/eventos")
@PreAuthorize("hasRole('ADMIN')")  // ✅ Activar
public class EventoAdminController {
    
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Doble verificación
    public ResponseEntity<?> cancelarEvento(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails  // ✅ Obtener usuario
    ) {
        // ✅ Verificar que el admin tenga permiso para este evento
        if (!eventoService.puedeAdministrar(userDetails, id)) {
            throw new AccessDeniedException("No tiene permisos para este evento");
        }
        // Continuar con lógica...
    }
}
```

**Prioridad:** 🔴 **CRÍTICA** - Implementar antes de producción

---

## 🔴 API2:2023 - Broken Authentication

### Descripción
Sistema de autenticación mal implementado o ausente que permite a atacantes comprometer tokens o credenciales.

### Vulnerabilidades Identificadas

#### ❌ **Crítico: Sin Sistema de Autenticación**

**Estado Actual:**
- ❌ No hay login/registro
- ❌ No hay JWT tokens
- ❌ No hay Spring Security configurado
- ❌ Endpoints admin accesibles sin credenciales

**Solución Propuesta:**

1. **Agregar Spring Security**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

2. **Crear SecurityConfig**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // ✅ Fuerte encriptación
    }
}
```

3. **Implementar JWT**

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;  // ✅ Desde variables de entorno
    
    @Value("${jwt.expiration}")
    private long expiration;  // ✅ Token expira en 1 hora
    
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

**Prioridad:** 🔴 **CRÍTICA** - Implementar antes de producción

---

## 🟠 API3:2023 - Broken Object Property Level Authorization

### Descripción
Exposición excesiva de datos o manipulación de propiedades de objetos sin autorización.

### Vulnerabilidades Identificadas

#### ⚠️ **Medio: Exposición de Datos Sensibles en DTOs**

**Ubicación:** `EventoDetalleDTO.java`

```java
public record OrganizadorDTO(
    String nombre,
    String email,          // ⚠️ Email público
    String telefono,       // ⚠️ Teléfono público
    String sitioWeb
) {}
```

**Impacto:**
- Emails de organizadores expuestos públicamente
- Teléfonos de organizadores expuestos públicamente
- Posibilidad de spam o phishing

**Solución Propuesta:**

```java
public record OrganizadorDTO(
    String nombre,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // ✅ Solo para admin
    String email,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // ✅ Solo para admin
    String telefono,
    String sitioWeb,
    String descripcion
) {
    // ✅ Versión pública (sin datos sensibles)
    public static OrganizadorDTO publicVersion(Organizador org) {
        return new OrganizadorDTO(
            org.getNombre(),
            null,  // ❌ No exponer email
            null,  // ❌ No exponer teléfono
            org.getSitioWeb(),
            org.getDescripcion()
        );
    }
    
    // ✅ Versión admin (con todos los datos)
    public static OrganizadorDTO adminVersion(Organizador org) {
        return new OrganizadorDTO(
            org.getNombre(),
            org.getEmail(),      // ✅ Para admin
            org.getTelefono(),   // ✅ Para admin
            org.getSitioWeb(),
            org.getDescripcion()
        );
    }
}
```

**Prioridad:** 🟠 **ALTA** - Implementar próximo sprint

---

## 🟠 API4:2023 - Unrestricted Resource Consumption

### Descripción
Falta de límites en el consumo de recursos (rate limiting, pagination, file size).

### Vulnerabilidades Identificadas

#### ⚠️ **Medio: Sin Rate Limiting**

**Problema:**
- Endpoints públicos sin límite de peticiones
- Posibilidad de DDoS o abuso de API
- Sin throttling por IP

**Solución Propuesta:**

1. **Implementar Bucket4j**

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.5.0</version>
</dependency>
```

2. **Crear Rate Limiting Filter**

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIP(request);
        Bucket bucket = resolveBucket(clientIp);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);  // Too Many Requests
            response.getWriter().write(
                "{\"error\":\"Rate limit exceeded. Try again later.\"}"
            );
        }
    }
    
    private Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }
    
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(100)              // ✅ 100 peticiones
            .refillGreedy(100, Duration.ofMinutes(1))  // ✅ por minuto
            .build();
        
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
```

#### ⚠️ **Bajo: Paginación sin Límite Máximo**

**Problema:**
```java
@GetMapping("/buscar")
public ResponseEntity<Page<EventoMosaicoDTO>> buscarEventos(
    @RequestParam(required = false) Integer size  // ⚠️ Sin límite máximo
) {
    // Usuario podría pedir size=1000000
}
```

**Solución:**

```java
@GetMapping("/buscar")
public ResponseEntity<Page<EventoMosaicoDTO>> buscarEventos(
    @RequestParam(required = false) 
    @Min(1) @Max(100)  // ✅ Máximo 100 por página
    Integer size
) {
    int pageSize = (size != null && size <= 100) ? size : 20;  // ✅ Default 20
    // ...
}
```

**Prioridad:** 🟠 **ALTA** - Implementar antes de producción

---

## 🟢 API5:2023 - Broken Function Level Authorization

### Descripción
Falta de validación de permisos a nivel de función.

### Estado Actual

#### ✅ **Implementado: Separación de Endpoints**

**Buenas prácticas aplicadas:**

```java
// ✅ Endpoints públicos separados
@RestController
@RequestMapping("/api/public/eventos")
public class EventoPublicController {
    // Solo operaciones de lectura
}

// ✅ Endpoints admin separados
@RestController
@RequestMapping("/api/admin/eventos")
public class EventoAdminController {
    // Operaciones de escritura/administración
}
```

**Pendiente:**
- ⚠️ Activar @PreAuthorize en endpoints admin
- ⚠️ Verificar roles en cada función sensible

**Prioridad:** 🟠 **ALTA** - Activar con Spring Security

---

## 🟡 API6:2023 - Unrestricted Access to Sensitive Business Flows

### Descripción
Falta de protección en flujos de negocio sensibles.

### Vulnerabilidades Identificadas

#### ⚠️ **Medio: Sistema de Destacados sin Protección**

**Problema:**
```java
@PutMapping("/{id}/destacar")
public ResponseEntity<?> destacarEvento(@PathVariable Long id, 
                                        @RequestParam Boolean destacar) {
    // ⚠️ Sin verificación de identidad
    // ⚠️ Sin logging de quién hizo el cambio
    // ⚠️ Sin auditoría
}
```

**Solución Propuesta:**

```java
@PutMapping("/{id}/destacar")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> destacarEvento(
    @PathVariable Long id,
    @RequestParam Boolean destacar,
    @AuthenticationPrincipal UserDetails userDetails  // ✅ Identificar usuario
) {
    // ✅ Auditoría
    auditService.logAdminAction(
        userDetails.getUsername(),
        "DESTACAR_EVENTO",
        id,
        destacar
    );
    
    // ✅ Verificación de permisos específicos
    if (!adminService.puedeDestacarEventos(userDetails)) {
        throw new AccessDeniedException("No tiene permisos para destacar eventos");
    }
    
    // Continuar con lógica...
}
```

**Implementar tabla de auditoría:**

```sql
CREATE TABLE admin_actions_audit (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(255) NOT NULL,
    accion VARCHAR(100) NOT NULL,
    evento_id BIGINT,
    detalles TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_evento FOREIGN KEY (evento_id) 
        REFERENCES eventos(id)
);

CREATE INDEX idx_audit_usuario ON admin_actions_audit(usuario);
CREATE INDEX idx_audit_timestamp ON admin_actions_audit(timestamp);
```

**Prioridad:** 🟡 **MEDIA** - Implementar en Sprint 3

---

## 🟢 API7:2023 - Server Side Request Forgery (SSRF)

### Descripción
API vulnerable a ataques SSRF donde se pueden hacer peticiones a recursos internos.

### Estado Actual

#### ✅ **No Vulnerable**

**Análisis:**
- ✅ No hay endpoints que acepten URLs como parámetros
- ✅ No hay funcionalidad de fetch/download de URLs externas
- ✅ No hay proxy o redirección de peticiones

**Recomendación:** Mantener vigilancia si se agregan funcionalidades de:
- Importación de eventos desde URLs
- Webhooks
- Integración con APIs externas

---

## 🟡 API8:2023 - Security Misconfiguration

### Descripción
Configuraciones de seguridad incorrectas o por defecto.

### Vulnerabilidades Identificadas

#### ⚠️ **Medio: CORS Abierto para Todos**

**Problema:**
```java
@CrossOrigin(origins = "*")  // ⚠️ Acepta peticiones de cualquier origen
public class EventoPublicController {
    // ...
}
```

**Impacto:**
- Cualquier sitio web puede consumir la API
- Posibilidad de CSRF si se implementa autenticación con cookies
- Exposición innecesaria

**Solución:**

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Solo orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
            "https://vivemedellin.com",
            "https://www.vivemedellin.com",
            "https://admin.vivemedellin.com"
        ));
        
        // ✅ Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // ✅ Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept"
        ));
        
        // ✅ Exponer headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

#### ⚠️ **Bajo: Información Sensible en Logs**

**Problema:**
```java
logger.info("Evento creado: {}", evento);  // ⚠️ Puede loguear datos sensibles
```

**Solución:**

```java
// ✅ Usar DTOs en logs, no entidades completas
logger.info("Evento creado: id={}, titulo={}", evento.getId(), evento.getTitulo());

// ✅ Sanitizar datos sensibles
logger.info("Login attempt: user={}", sanitize(username));
```

**Prioridad:** 🟡 **MEDIA** - Implementar en Sprint 3

---

## 🟢 API9:2023 - Improper Inventory Management

### Descripción
Falta de documentación actualizada de endpoints y versiones de API.

### Estado Actual

#### ✅ **Implementado: Swagger/OpenAPI**

**Buenas prácticas aplicadas:**
- ✅ Swagger UI: `/swagger-ui/index.html`
- ✅ OpenAPI JSON: `/v3/api-docs`
- ✅ Documentación actualizada automáticamente
- ✅ Ejemplos de request/response

**Mejoras Recomendadas:**

```java
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ViveMedellín API",
        version = "1.0.0",
        description = "API REST para gestión de eventos culturales en Medellín",
        contact = @Contact(
            name = "Equipo ViveMedellín",
            email = "soporte@vivemedellin.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Desarrollo"),
        @Server(url = "https://api.vivemedellin.com", description = "Producción")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
    // ✅ Configuración completa de Swagger
}
```

**Prioridad:** 🟢 **BAJA** - Mejorar documentación continuamente

---

## 🟡 API10:2023 - Unsafe Consumption of APIs

### Descripción
Vulnerabilidades al consumir APIs de terceros sin validación.

### Estado Actual

#### ✅ **No Aplica Actualmente**

**Análisis:**
- ✅ No se consumen APIs externas actualmente
- ✅ Google Maps es solo generación de URL (sin API calls)

**Recomendaciones Futuras:**

Si se integran APIs externas:

```java
@Service
public class ExternalApiService {
    
    private final RestTemplate restTemplate;
    
    // ✅ Timeouts configurados
    public ExternalApiService() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);      // ✅ 5 segundos
        factory.setReadTimeout(10000);        // ✅ 10 segundos
        
        this.restTemplate = new RestTemplate(factory);
    }
    
    public EventoDTO fetchEvento(String externalId) {
        try {
            // ✅ Validar URL
            if (!isValidUrl(externalId)) {
                throw new IllegalArgumentException("URL inválida");
            }
            
            // ✅ Validar respuesta
            ResponseEntity<EventoDTO> response = restTemplate.getForEntity(
                externalId,
                EventoDTO.class
            );
            
            // ✅ Validar status code
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalApiException("Error en API externa");
            }
            
            // ✅ Validar contenido
            EventoDTO evento = response.getBody();
            if (evento == null) {
                throw new ExternalApiException("Respuesta vacía");
            }
            
            return sanitize(evento);  // ✅ Sanitizar datos
            
        } catch (RestClientException e) {
            logger.error("Error consumiendo API externa", e);
            throw new ExternalApiException("Servicio externo no disponible");
        }
    }
}
```

---

## 📊 Resumen de Vulnerabilidades

### Por Severidad

| Severidad | Cantidad | Descripción |
|-----------|----------|-------------|
| 🔴 Crítica | 2 | Autenticación y Autorización faltantes |
| 🟠 Alta | 3 | Rate Limiting, CORS, Exposición de datos |
| 🟡 Media | 2 | Auditoría, Logging |
| 🟢 Baja | 3 | Configuraciones menores |

### Por Categoría OWASP

| OWASP ID | Categoría | Estado | Acción |
|----------|-----------|--------|--------|
| API1 | Broken Object Authorization | ❌ Vulnerable | Implementar Spring Security |
| API2 | Broken Authentication | ❌ Vulnerable | Implementar JWT |
| API3 | Broken Property Authorization | ⚠️ Parcial | Filtrar datos sensibles |
| API4 | Resource Consumption | ⚠️ Parcial | Implementar Rate Limiting |
| API5 | Function Authorization | ⚠️ Preparado | Activar @PreAuthorize |
| API6 | Business Flow | ⚠️ Parcial | Agregar auditoría |
| API7 | SSRF | ✅ Seguro | N/A |
| API8 | Misconfiguration | ⚠️ Parcial | Configurar CORS |
| API9 | Inventory Management | ✅ Implementado | Mantener Swagger |
| API10 | Unsafe Consumption | ✅ N/A | N/A |

---

## 🎯 Plan de Acción Priorizado

### Sprint Actual (Crítico)
1. ✅ Documentar vulnerabilidades (COMPLETADO)
2. 🔄 Implementar Spring Security
3. 🔄 Configurar JWT
4. 🔄 Activar @PreAuthorize en endpoints admin

### Sprint 3 (Alto)
5. Implementar Rate Limiting (Bucket4j)
6. Configurar CORS restrictivo
7. Filtrar datos sensibles en DTOs
8. Implementar sistema de auditoría

### Sprint 4 (Medio)
9. Mejorar logging y sanitización
10. Agregar límites de paginación
11. Implementar monitoreo de seguridad
12. Penetration testing

---

## 🛠️ Herramientas de Escaneo Recomendadas

### En CI/CD (GitHub Actions)
- ✅ **OWASP Dependency Check** - Vulnerabilidades en dependencias
- ✅ **Trivy** - Escaneo de contenedores Docker
- 🔄 **SonarCloud** - Análisis de código
- 🔄 **Snyk** - Seguridad de dependencias

### Manual
- **OWASP ZAP** - Penetration testing
- **Burp Suite** - Análisis de tráfico HTTP
- **Postman** - Testing de endpoints
- **JMeter** - Testing de carga

---

## 📝 Checklist de Seguridad

### Pre-Producción
- [ ] Spring Security implementado
- [ ] JWT configurado
- [ ] Rate Limiting activo
- [ ] CORS configurado restrictivamente
- [ ] Datos sensibles filtrados
- [ ] HTTPS configurado
- [ ] Variables de entorno para secretos
- [ ] Logs sanitizados
- [ ] Auditoría implementada
- [ ] Penetration testing completado

### Post-Producción
- [ ] Monitoreo de logs activo
- [ ] Alertas de seguridad configuradas
- [ ] Backups automáticos
- [ ] Rotación de secretos programada
- [ ] Revisión mensual de dependencias
- [ ] Auditorías trimestrales

---

## ✅ Conclusión

**Estado Actual:** ⚠️ **No apto para producción sin seguridad**

**Vulnerabilidades Críticas:** 2
- Falta de autenticación
- Falta de autorización

**Acción Inmediata Requerida:**
1. Implementar Spring Security con JWT
2. Activar autorización en endpoints admin
3. Configurar CORS restrictivo
4. Implementar Rate Limiting

**Tiempo Estimado:** 2-3 sprints para seguridad completa

**Estado después de remediar:** ✅ **Apto para producción**
