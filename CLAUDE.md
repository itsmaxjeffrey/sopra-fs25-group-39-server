# CLAUDE.md - Guidelines for Coding Assistants

## Build & Test Commands
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Test all: `./gradlew test`
- Run single test: `./gradlew test --tests "ch.uzh.ifi.hase.soprafs24.controller.UserControllerTest"`
- Development mode: 
  - Terminal 1: `./gradlew build --continuous -xtest`
  - Terminal 2: `./gradlew bootRun`

## Code Style
- Java 17, UTF-8, 4-space indentation, 120 char line limit
- Single class imports (no wildcards except for java.awt.*, javax.swing.*)
- Class naming: implementation classes suffix with "Impl", test classes with "Test"
- Braces: end-of-line style for all blocks
- Control statements: always use braces even for single statements
- Error handling: use Spring's ResponseStatusException, see GlobalExceptionAdvice
- Entity structure: use JPA annotations for DB mapping, clearly define relationships
- Testing: JUnit 5 with Spring Boot test framework (MockMvc, integration/unit tests)

## Docker
- Project supports containerization
- Images build automatically on main branch changes