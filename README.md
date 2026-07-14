# 🛡️ VigiControlPro

Sistema de gestión de seguridad para tiendas, desarrollado a partir de un caso real (Leroy Merlin).

## 🚀 Stack tecnológico

**Backend:**
- Java 23 · Spring Boot 3.5 · Spring Security + JWT
- Spring Data JPA · Hibernate · PostgreSQL
- Arquitectura hexagonal (módulo Incident)
- Swagger / OpenAPI
- JUnit 5 + Mockito (47 tests unitarios)

**Frontend:**
- React 18 · Vite · React Router DOM
- Axios (interceptor JWT automático)
- Recharts (gráficas del dashboard)

## 📦 Módulos

| Módulo | Descripción |
|---|---|
| **Usuarios** | Roles: ADMIN, SUPERVISOR, VIGILANTE |
| **Incidencias** | Tipo, prioridad, zona de tienda y foto de evidencia |
| **Turnos** | Planificación por zona y estado |
| **Intervenciones** | Asociadas a una incidencia concreta |

## 🏗️ Arquitectura

- Organización **por dominio** (feature-based): cada módulo agrupa entity, dto, repository, service, controller y mapper
- **Arquitectura hexagonal** en el módulo Incident: `IncidentRepositoryPort` (puerto de dominio) + `JpaIncidentRepositoryAdapter` (adaptador de infraestructura)
- Autenticación **stateless** con JWT: `JwtFilter` + `SecurityContextHolder`
- Control de acceso granular con `@PreAuthorize` por método
- **Notificaciones** automáticas a ADMIN/SUPERVISOR ante incidencias CRITICA
- **Subida de fotos** de evidencia con almacenamiento en filesystem

## 🖥️ Cómo arrancar

### Requisitos
- Java 17+
- PostgreSQL
- Node.js 18+

### Backend
```bash
# Configurar application.properties con tus credenciales de PostgreSQL
mvn clean package -DskipTests
java -jar target/vigicontrol-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
npm install
npm run dev
```

La API estará disponible en `http://localhost:8082`  
El frontend en `http://localhost:5173`  
Swagger UI en `http://localhost:8082/swagger-ui/index.html`

## 📊 Dashboard

Panel de control con contadores en tiempo real e historial de incidencias visualizado con gráficas de barras (por zona) y circular (por tipo).

## 🧪 Tests

```bash
mvn test
```

47 tests unitarios con JUnit 5 + Mockito cubriendo los 4 módulos principales (User, Incident, Shift, Intervention), incluyendo casos de éxito, excepciones y verificación de notificaciones.

## 👤 Autor

**Manuel Tortosa Capilla**  
Backend Developer · Java · Spring Boot  
[GitHub](https://github.com/manuconan)
