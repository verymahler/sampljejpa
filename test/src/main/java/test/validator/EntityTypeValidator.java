package test.validator;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class EntityTypeValidator {
  private static final String ID_FIELD_NAME = "id";
  private static final String GET_ID_METHOD_NAME = "getId";

  private List<RuleViolation> ruleViolations = Lists.newArrayListWithExpectedSize(10);

  public List<RuleViolation> validate(Class<?> entityClass) {
    validateClass(entityClass);
    validateConstructor(entityClass);
    validateIdField(entityClass);
    validateIdGetter(entityClass);

    return ruleViolations;
  }

  private void validateClass(Class<?> entityClass) {
    Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
    if (entityAnnotation == null) {
      ruleViolations.add(new RuleViolation("annotation [%s] should be applied to class declaration",
          Entity.class.getName()));
    }
  }

  private void validateConstructor(Class<?> entityClass) {
    if (hasPublicOrProtectedNoArgConstructor(entityClass) == false) {
      ruleViolations.add(
          new RuleViolation("entity should have a public or protected zero-argument constructor"));
    }
  }

  private boolean hasPublicOrProtectedNoArgConstructor(Class<?> entityClass) {
    Constructor<?>[] constructors = entityClass.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      if (isPublicOrProtectedNoArgConstructor(constructor)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isPublicOrProtectedNoArgConstructor(Constructor<?> constructor) {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    if (parameterTypes.length == 0) {
      if (isPublic(constructor) || isProtected(constructor)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isPublic(Constructor<?> constructor) {
    return Invokable.from(constructor).isPublic();
  }

  private static boolean isProtected(Constructor<?> constructor) {
    return Invokable.from(constructor).isProtected();
  }

  private void validateIdField(Class<?> entityClass) {
    Field idField = getFieldByName(entityClass, ID_FIELD_NAME);
    if (idField != null) {
      validateIdType(idField);
      validateIdAnnotations(idField);
    } else {
      ruleViolations
          .add(new RuleViolation("entity should have a field named [%s].", ID_FIELD_NAME));
    }
  }

  private static Field getFieldByName(Class<?> entityClass, String name) {
    Class<?> searchType = entityClass;
    while ((Object.class != searchType) && (searchType != null)) {
      Field[] fields = searchType.getDeclaredFields();
      for (Field field : fields) {
        if (name.equals(field.getName())) {
          return field;
        }
      }
      searchType = searchType.getSuperclass();
    }
    return null;
  }

  private void validateIdType(Field idField) {
    if (idField.getType().equals(Long.class) == false) {
      ruleViolations.add(new RuleViolation("field id should have type [%s]", Long.class.getName()));
    }
  }

  private void validateIdAnnotations(Field idField) {
    Annotation[] annotations = idField.getDeclaredAnnotations();
    boolean idAnnotation = false;
    boolean generatedValueAnnotation = false;
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().equals(Id.class)) {
        idAnnotation = true;
      }
      if (annotation.annotationType().equals(GeneratedValue.class)) {
        generatedValueAnnotation = true;
      }
    }
    if (idAnnotation == false) {
      ruleViolations.add(new RuleViolation(
          "annotation [%s] should be applied to the id field declaration", Id.class.getName()));
    }
    if (generatedValueAnnotation == false) {
      ruleViolations
          .add(new RuleViolation("annotation [%s] should be applied to the id field declaration",
              GeneratedValue.class.getName()));
    }
  }

  private void validateIdGetter(Class<?> entityClass) {
    try {
      entityClass.getMethod(GET_ID_METHOD_NAME);
    } catch (@SuppressWarnings("unused") NoSuchMethodException e) {
      ruleViolations.add(new RuleViolation("entity should always have a public method named [%s].",
          GET_ID_METHOD_NAME));
    }
  }
}
