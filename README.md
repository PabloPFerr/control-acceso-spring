# Sistema de Control de Acceso Horario

Este proyecto es un sistema de control de acceso horario desarrollado con Spring Boot que permite gestionar y monitorizar los accesos y horarios en una organización.

## Requisitos Previos

- Java 21 o superior
- Maven 3.6 o superior
- IDE compatible con Spring Boot (recomendado: IntelliJ IDEA, Eclipse, VS Code)

## Tecnologías Utilizadas

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Thymeleaf
- Base de datos H2 (en memoria)
- Lombok
- Maven

## Estructura del Proyecto

```
control-acceso/
├── src/
│   └── main/
│       ├── java/         # Código fuente Java
│       └── resources/    # Recursos y configuraciones
├── data/                 # Directorio de datos
├── .mvn/                 # Configuración de Maven Wrapper
├── pom.xml              # Configuración de dependencias Maven
└── README.md            # Este archivo
```

## Configuración y Ejecución

### Clonar el Repositorio

```bash
git clone [URL_DEL_REPOSITORIO]
cd control-acceso
```

### Compilar el Proyecto

```bash
./mvnw clean install
```

### Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Base de Datos

El proyecto utiliza H2 como base de datos en memoria. La consola de H2 está disponible en:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: (vacía)

## Configuración de Diferentes Bases de Datos

El proyecto está configurado por defecto para usar H2 como base de datos en memoria, pero puede ser fácilmente modificado para usar otras bases de datos como MySQL o PostgreSQL.

### MySQL

1. Añade la dependencia en el `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Modifica `application.properties`:
```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_base_datos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### PostgreSQL

1. Añade la dependencia en el `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Modifica `application.properties`:
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_base_datos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### Notas Importantes

- Asegúrate de tener la base de datos creada antes de ejecutar la aplicación
- Ajusta los valores de username, password y URL según tu configuración
- El valor de `ddl-auto` puede ser:
  - `update`: Actualiza el esquema automáticamente
  - `create`: Crea el esquema destruyendo datos previos
  - `validate`: Solo valida el esquema
  - `none`: No hace cambios en el esquema

## Características Principales

- Gestión de usuarios y roles
- Control de accesos
- Registro de horarios
- Interfaz web con Thymeleaf
- Seguridad integrada con Spring Security

## Desarrollo

Para contribuir al proyecto:

1. Crea un fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Realiza tus cambios y haz commit (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

[Especificar la licencia del proyecto]

## Contacto

[Tu nombre o el del equipo] - [email]

Link del proyecto: [URL_DEL_REPOSITORIO]
