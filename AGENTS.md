# AGENTS.md - Development Guidelines for Seatflow

This document contains essential guidelines for agentic coding agents working in this Spring Boot seat assignment application.

## Build Commands

### Core Maven Commands
- **Build project**: `mvn clean compile`
- **Run application**: `mvn spring-boot:run`
- **Package**: `mvn clean package`

### Development Commands
- **Enable devtools auto-reload**: Application automatically reloads on changes due to spring-boot-devtools
- **H2 console access**: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)
- **Actuator endpoints**: http://localhost:8080/actuator

## Project Structure

### Package Organization
```
com.xinnsuu.seatflow/
├── controller/          # REST API controllers and Web controllers
├── service/            # Business logic services and implementations  
├── repository/         # JPA repositories
├── model/              # JPA entities and enums
├── config/             # Configuration classes
└── converter/          # Custom converters
```

### Key Architecture Patterns
- **Controller Layer**: Separate REST controllers (`*Controller`) and Web controllers (`*WebController`)
- **Service Layer**: Interface + Implementation pattern (`*Service` + `*ServiceImpl`)
- **Repository Layer**: Spring Data JPA repositories extending `JpaRepository`
- **Model Layer**: JPA entities with Lombok annotations for getters/setters

## Code Style Guidelines

### Imports
- **Java imports**: Group standard imports, then third-party, then your own package
- **Order**: `java.*`, `jakarta.*`, `org.*`, `com.*`
- **No wildcard imports**: Always import specific classes

### Naming Conventions
- **Classes**: PascalCase (e.g., `StudentController`, `AcademicStructure`)
- **Methods**: camelCase with descriptive names (e.g., `getStudentsBySectionId`)
- **Variables**: camelCase, meaningful names
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase, dot-separated

### Entity Conventions
- **Annotations**: Use `@Entity`, `@Table`, `@Id`, `@GeneratedValue` appropriately
- **Lombok**: Always use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Validation**: Use `@NotNull`, `@NotBlank`, `@Size` from jakarta.validation
- **Relationships**: Use `@ManyToOne`, `@OneToMany`, `@JoinColumn` with proper mappings
- **Equals/HashCode**: Exclude bidirectional relationships in `@EqualsAndHashCode`

### Controller Patterns
- **REST Controllers**: Use `@RestController` with `@RequestMapping` base path
- **HTTP Methods**: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- **Path Variables**: Use `@PathVariable` for URL parameters
- **Request Body**: Use `@RequestBody` with `@Valid` for validation
- **Response Entities**: Always return `ResponseEntity<T>` with appropriate HTTP status
- **Error Handling**: Use try-catch blocks returning appropriate HTTP status codes

### Service Layer Patterns
- **Autowired Dependencies**: Use field injection with `@Autowired`
- **Exception Handling**: Throw `RuntimeException` with descriptive messages
- **Validation**: Validate input parameters and entity existence
- **Transactional**: Services are transactional by default with Spring

### Frontend (Thymeleaf/CSS)
- **CSS Framework**: Bulma CSS with custom overrides in `/css/common.css`
- **Color Scheme**: Primary color `#00BCD4` (cyan), maintain consistent theme
- **Responsive Design**: Mobile-first approach with bottom navigation
- **JavaScript**: Keep scripts in `/js/` with `defer` attribute
- **Thymeleaf**: Use fragments for reusable components in `/templates/fragments/`

## Error Handling Guidelines

### Controllers
- **Not Found**: Return `HttpStatus.NOT_FOUND` (404) when resource doesn't exist
- **Bad Request**: Return `HttpStatus.BAD_REQUEST` (400) for validation/processing errors
- **Success**: Return `HttpStatus.OK` (200), `HttpStatus.CREATED` (201), or `HttpStatus.NO_CONTENT` (204)

### Services
- **Runtime Exceptions**: Throw with clear, descriptive messages
- **Entity Not Found**: Check repository existence before operations
- **Validation**: Let Jakarta Bean Validation handle input validation



## Database Guidelines

### JPA/Hibernate
- **Database**: H2 in-memory database for development
- **Entity Relationships**: Define clear foreign key relationships
- **Cascade Operations**: Use `cascade = CascadeType.ALL` appropriately for parent-child relationships
- **Data Validation**: Combine JPA validation with Jakarta Bean Validation

## File Processing

### CSV Import/Export
- **Library**: Apache Commons CSV for parsing
- **Validation**: Validate CSV structure and data before processing
- **Error Handling**: Graceful handling of malformed CSV data

## Security Considerations

- **Input Validation**: Always validate user input with Jakarta Bean Validation
- **SQL Injection**: Use JPA parameterized queries, avoid raw SQL
- **File Upload**: Validate file types and sizes for multipart uploads
- **Data Exposure**: Use `@JsonIgnore` on sensitive entity relationships

## Development Workflow

1. **Make Changes**: Edit code files following style guidelines
2. **Build**: `mvn clean compile` to check for compilation errors
3. **Run Application**: `mvn spring-boot:run` to test manually
4. **Commit**: Commit after manual verification

## Common Patterns to Follow

### Repository Methods
- Follow Spring Data naming conventions (`findByX`, `existsByX`, `deleteByX`)
- Use `Optional<T>` for methods that may not return results

### Controller Response Patterns
```java
// GET all resources
public ResponseEntity<List<Entity>> getAll() {
    List<Entity> items = service.findAll();
    return new ResponseEntity<>(items, HttpStatus.OK);
}

// GET single resource
public ResponseEntity<Entity> getById(@PathVariable Long id) {
    return service.findById(id)
        .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

// POST create
public ResponseEntity<Entity> create(@Valid @RequestBody Entity entity) {
    try {
        Entity saved = service.create(entity);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
```

## Configuration

### Application Properties
- Use `application.properties` for configuration
- Use `application-dev.properties.template` as template for development
- Spring Profiles available for different environments

### Lombok Configuration
- Lombok is configured in maven-compiler-plugin
- Use annotation processors for code generation