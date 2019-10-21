# samplejpa

Isolated Maven project to demonstrate issue related to `@NotNull byte[]` in an `@Entity` object using JPA

Libraries used are:

- EclipseLink for source generation
- Jersey for Bean Validation

*This works with*

- EclipseLink 2.7.4
- Jersey 2.27

*It doesn't work with*

- EclipseLink 2.7.4
- Jersey 2.29.1
