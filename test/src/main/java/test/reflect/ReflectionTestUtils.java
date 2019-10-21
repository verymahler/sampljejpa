package test.reflect;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Note that using reflection is not always considered good practice, even in unit tests. So it is
 * advised to only make use of this class if no other options are available.
 */
public final class ReflectionTestUtils {

  private ReflectionTestUtils() {
    // Private constructor that should prevent this class from getting instantiated
  }

  public static <T> void setField(Object targetObject, Class<T> type, T value) {
    checkNotNull(targetObject);
    checkNotNull(type);

    Field field = findRequiredField(targetObject.getClass(), type);
    makeFieldAccessible(field);
    setFieldValue(targetObject, field, value);
  }

  private static void invokeAnnotatedMethod(Object targetObject,
      Class<? extends Annotation> annotation) {

    Method method = findRequiredMethod(targetObject.getClass(), annotation);
    makeMethodAccessible(method);
    invokeMethod(targetObject, method);
  }

  private static Method findRequiredMethod(Class<? extends Object> clazz,
      Class<? extends Annotation> annotationClazz) {
    Method method = findMethod(clazz, annotationClazz);
    if (method == null) {
      throw new IllegalArgumentException(
          String.format("Object with type [%s] does not have a method annotated with [@%s].",
              clazz.getName(), annotationClazz.getSimpleName()));
    }
    return method;
  }

  private static Method findMethod(Class<? extends Object> clazz,
      Class<? extends Annotation> annotation) {
    Class<?> searchType = clazz;
    Method matchingMethod = null;
    while ((Object.class != searchType) && (searchType != null)) {
      Method[] methods = searchType.getDeclaredMethods();
      for (Method method : methods) {
        matchingMethod = checkIfMethodIsAnnotatedWith(method, annotation, matchingMethod);
      }
      searchType = searchType.getSuperclass();
    }
    return matchingMethod;
  }

  private static Method checkIfMethodIsAnnotatedWith(Method method,
      Class<? extends Annotation> annotation, Method matchingMethod) {
    if (method.isAnnotationPresent(annotation)) {
      verifyThatMethodDidNotMatchBefore(matchingMethod, annotation);
      return method;
    }
    return matchingMethod;
  }

  private static void verifyThatMethodDidNotMatchBefore(Method matchingMethod,
      Class<? extends Annotation> annotation) {
    if (matchingMethod != null) {
      throw new IllegalStateException(String.format(
          "Could not find method that is annotated with [@%s]. Ambiguous matches detected.",
          annotation));
    }
  }

  private static void invokeMethod(Object targetObject, Method postConstructMethod) {
    try {
      postConstructMethod.invoke(targetObject);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      throw new IllegalStateException("Exception thrown while Invoking method.", e);
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof RuntimeException) {
        throw (RuntimeException) e.getTargetException();
      }
      throw new IllegalStateException("Exception thrown while Invoking method.", e);
    }
  }

  private static Field findRequiredField(Class<?> clazz, Class<?> type) {
    Field field = findField(clazz, type);
    if (field == null) {
      throw new IllegalArgumentException(
          String.format("Object with type [%s] does not have a field with type [%s]",
              clazz.getName(), type.getName()));
    }
    return field;
  }

  private static Field findField(Class<?> clazz, Class<?> type) {
    Class<?> searchType = clazz;
    Field matchingField = null;
    while ((Object.class != searchType) && (searchType != null)) {
      Field[] fields = searchType.getDeclaredFields();
      for (Field field : fields) {
        matchingField = checkIfFieldTypeMatches(type, field, matchingField);
      }
      searchType = searchType.getSuperclass();
    }
    return matchingField;
  }

  private static boolean fieldNameMatches(String name, Field field) {
    return name.equals(field.getName());
  }

  private static Field checkIfFieldTypeMatches(Class<?> type, Field field, Field matchingField) {
    if (fieldTypeMatches(type, field)) {
      verifyThatFieldDidNotMatchBefore(matchingField, type);
      return field;
    }
    return matchingField;
  }

  private static boolean fieldTypeMatches(Class<?> type, Field field) {
    return type.equals(field.getType());
  }

  private static void verifyThatFieldDidNotMatchBefore(Field matchingField, Class<?> type) {
    if (matchingField != null) {
      throw new IllegalStateException(String.format(
          "Could not find field with type [%s]. Ambiguous matches detected.", type.getName()));
    }
  }

  private static void makeMethodAccessible(Method method) {
    if (((Modifier.isPublic(method.getModifiers()))
        && (Modifier.isPublic(method.getDeclaringClass().getModifiers()))
        && (!(Modifier.isFinal(method.getModifiers())))) || (method.isAccessible()))
      return;
    method.setAccessible(true);
  }

  private static void makeFieldAccessible(Field field) {
    if (((Modifier.isPublic(field.getModifiers()))
        && (Modifier.isPublic(field.getDeclaringClass().getModifiers()))
        && (!(Modifier.isFinal(field.getModifiers())))) || (field.isAccessible()))
      return;
    field.setAccessible(true);
  }

  private static void setFieldValue(Object targetObject, Field field, Object value) {
    try {
      field.set(targetObject, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new IllegalStateException(String.format(
          "Exception thrown while setting value [%s] of field with name [%s] on Object with type [%s].",
          value, field.getName(), targetObject.getClass().getName()), e);
    }
  }

  private static Object getFieldValue(Object targetObject, Field field) {
    try {
      return field.get(targetObject);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new IllegalStateException(String.format(
          "Exception thrown while reading value from field with name [%s] on Object with type [%s].",
          field.getName(), targetObject.getClass().getName()), e);
    }
  }
}
