# Capgemini Test — API de gestión de usuarios y salas

Solución de una prueba técnica desarrollada con **Java 17 y Spring Boot 3.2.0**.

La aplicación permite crear usuarios asociados a una sala y consultar sus datos. Durante la creación, valida el DNI mediante una API externa simulada y envía una notificación según el rol del usuario.

## 1. Tecnologías utilizadas

- **Java 17** y **Spring Boot 3.2.0**
- **Maven** para la gestión de dependencias y compilación.
- **Spring Web** para desarrollar los endpoints REST.
- **Spring Data JPA** para la persistencia.
- **PostgreSQL** como base de datos relacional.
- **Flyway** para gestionar las migraciones.
- **OpenFeign** para la comunicación con servicios externos.
- **MockServer** para simular las APIs externas.
- **Docker Compose** para levantar la infraestructura.
- **JUnit 5, Mockito y MockMvc** para las pruebas automatizadas.
- **Lombok** para reducir código repetitivo.

## 2. Requisitos previos

Para ejecutar el proyecto es necesario disponer de:

- JDK 17
- Maven
- Docker y Docker Compose
- Git

**Importante:** el proyecto está configurado para Java 17. Si tienes varios JDK instalados, comprueba que Maven utiliza Java 17 ejecutando:

```bash
java -version
mvn -version
```

## 3. Instalación y ejecución

### 3.1. Clonar el repositorio

```bash
git clone https://github.com/dalonso1/prueba-tecnica-daniel.git
cd prueba-tecnica-daniel
```

### 3.2. Levantar la infraestructura

Desde la raíz del proyecto:

```bash
cd docker
docker compose up -d
docker compose ps
cd ..
```

Docker Compose inicia los siguientes servicios:

| Servicio | Puerto | Descripción |
|---|---|---|
| PostgreSQL | 5432 | Base de datos de la aplicación |
| MockServer | 1080 | Simulación de APIs externas |

Los contenedores deben estar disponibles antes de arrancar la aplicación.

### 3.3. Ejecutar las pruebas

Desde la raíz del proyecto:

```bash
mvn clean test
```

Este comando compila el proyecto y ejecuta las pruebas automatizadas.

### 3.4. Arrancar Spring Boot

```bash
mvn spring-boot:run
```

Una vez iniciado, el servicio REST estará disponible en:

`http://localhost:8080`

### 3.5. Detener la infraestructura

Detén Spring Boot con `Ctrl+C` y, desde la carpeta `docker`, ejecuta:

```bash
docker compose down
```

## 4. Endpoints REST

### 4.1. Crear usuario

**Método:** `POST`

**Endpoint:** `/rooms/{roomId}/users`

Permite crear un usuario asociado a una sala existente.

Ejemplo de petición:

```bash
curl -i -X POST http://localhost:8080/rooms/1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro",
    "email": "pedro@email.com",
    "phone": "611223344",
    "rol": "admin",
    "dni": "87654321X"
  }'
```

**Respuesta correcta: HTTP 201 Created**

```json
{
  "id": "3"
}
```

El identificador generado puede variar según los datos existentes en la base de datos.

**Nota:** si se repite la petición, es necesario utilizar datos únicos para evitar conflictos con usuarios existentes.

### 4.2. Obtener usuario

**Método:** `GET`

**Endpoint:** `/rooms/{roomId}/users/{userId}`

Permite consultar un usuario perteneciente a una sala determinada.

Ejemplo:

```bash
curl -i http://localhost:8080/rooms/1/users/3
```

**Respuesta correcta: HTTP 200 OK**

```json
{
  "id": 3,
  "name": "Daniel",
  "email": "daniel@example.com",
  "phone": "677998899",
  "rol": "admin",
  "dni": "12345678A",
  "roomId": 1
}
```

### 4.3. Gestión de errores

La aplicación utiliza un manejador global de excepciones para devolver respuestas HTTP consistentes.

| Código HTTP | Descripción |
|---|---|
| 200 OK | Usuario recuperado correctamente |
| 201 Created | Usuario creado correctamente |
| 404 Not Found | Sala o usuario inexistente |
| 409 Conflict | Error de validación |

Ejemplo de respuesta de validación:

```json
{
  "code": 409,
  "message": "error validation email"
}
```

## 5. Validaciones de negocio

Durante la creación de usuarios se comprueban las siguientes reglas:

1. El nombre no puede estar vacío ni superar los 6 caracteres.
2. El email debe contener `@` y `.`.
3. El email no puede estar registrado previamente.
4. El rol debe ser `admin` o `superadmin`.
5. El DNI debe validarse mediante el servicio externo.

Si alguna validación falla, se lanza la excepción correspondiente y se devuelve una respuesta HTTP de error.

## 6. Integraciones externas

La aplicación utiliza **OpenFeign** para comunicarse con las APIs simuladas mediante MockServer.

### Validación del DNI

**Método:** `PATCH`

**Endpoint:** `/check-dni`

El servicio comprueba la validez del DNI proporcionado.

Según la configuración del MockServer:

- HTTP 200: DNI aceptado.
- HTTP 409: DNI rechazado (`99999999w`).

### Notificaciones

Después de guardar correctamente un usuario, se envía una notificación según su rol:

| Rol | Método | Endpoint |
|---|---|---|
| admin | POST | `/email` |
| superadmin | POST | `/sms` |

El mensaje enviado es:

`usuario guardado`

## 7. Arquitectura del proyecto

La aplicación sigue una arquitectura por capas que separa las responsabilidades.

```text
Petición HTTP
      |
      v
   Controller
      |
      v
    Service
      |
      +---- ValidationService
      |           |
      |           v
      |       MockServer
      |
      +---- Repository
      |           |
      |           v
      |       PostgreSQL
      |
      +---- NotificationService
                  |
                  v
              MockServer
```

### Responsabilidades principales

**Controller:** recibe las peticiones HTTP y devuelve las respuestas correspondientes.

**Service:** implementa la lógica de negocio y coordina las operaciones.

**Repository:** proporciona acceso a los datos mediante Spring Data JPA.

**DTO:** define las estructuras de entrada y salida de la API.

**GlobalExceptionHandler:** centraliza el tratamiento de excepciones.

**Feign Clients:** encapsulan la comunicación con los servicios externos.

**Flyway:** gestiona las migraciones y la inicialización de la base de datos.

## 8. Pruebas automatizadas

Se han implementado **35 pruebas automatizadas** utilizando JUnit 5, Mockito y MockMvc.

Las pruebas cubren:

- Validaciones de usuarios.
- Comprobación de usuarios existentes.
- Integración simulada con el servicio de validación de DNI.
- Notificaciones por email y SMS.
- Creación y recuperación de usuarios.
- Gestión de excepciones.
- Respuestas HTTP de los controladores.
- Arranque del contexto de Spring Boot.

Para ejecutarlas:

```bash
mvn clean test
```

**Resultado verificado durante el desarrollo:**

```text
Tests run: 35
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

También se han realizado pruebas manuales de los endpoints REST contra PostgreSQL y MockServer mediante peticiones HTTP reales.

## 9. Resumen de los requisitos de la prueba técnica

La prueba solicita implementar una aplicación que gestione salas y usuarios.

Cada sala debe disponer de un identificador único y puede contener múltiples usuarios. Cada usuario pertenece a una única sala.

La creación de usuarios debe incluir validaciones, comprobación del DNI mediante una API externa, persistencia en base de datos y notificación según el rol.

También se requiere recuperar los datos de un usuario dentro de una sala determinada.

Entre los aspectos valorados se encuentran la calidad de la implementación, la arquitectura, las pruebas automatizadas, el uso de Spring y los patrones de diseño.

## 10. Entrega

El proyecto debe entregarse mediante un repositorio público personal, con los commits realizados en la rama `main`.