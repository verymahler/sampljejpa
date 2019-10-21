package test.matchers;

import test.matchers.more.EntityTypeMatcher;
import test.matchers.more.ValidatorMatcher;
import org.hamcrest.Matcher;

/**
 * This class provides Matchers which are able to check whether valid annotations have been applied
 * on any type of element. In addition, a Matcher can check whether other attributes (such as access
 * modifiers) have been properly declared according to basic rules accompanying a specific
 * annotation.
 */
public class MoreMatchers {

  /**
   * Returns a matcher that can be used to check whether an object validates after calling
   * {@link javax.validation.Validator#validate(Object)}. This can for example be useful for
   * checking whether a property has been declared mandatory by adding a @NotNull annotation.
   */
  public static <T> Matcher<T> valid() {
    return ValidatorMatcher.valid();
  }

  /**
   * Returns a matcher that can check whether a JPA Entity class is valid according to the standard
   * rules for the framework.
   */
  public static Matcher<Class<?>> validEntityType() {
    return EntityTypeMatcher.validEntityType();
  }
}
