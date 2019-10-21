package test.matchers.more;

import test.validator.EntityTypeValidator;
import test.validator.RuleViolation;
import test.validator.RuleViolationAppender;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;

public class EntityTypeMatcher extends BaseMatcher<Class<?>> {
  private EntityTypeValidator entityTypeValidator;
  private List<RuleViolation> ruleViolations;

  public EntityTypeMatcher() {
    entityTypeValidator = new EntityTypeValidator();
  }

  public static Matcher<Class<?>> validEntityType() {
    return new EntityTypeMatcher();
  }

  @Override
  public boolean matches(Object obj) {
    Class<?> clazz = (Class<?>) obj;
    ruleViolations = entityTypeValidator.validate(clazz);
    return ruleViolations.isEmpty();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(
        "class that matches standard rules for declaring a JPA entity type according to standards.");
  }

  @Override
  public void describeMismatch(Object item, Description description) {
    description.appendText(describeMismatch());
  }

  private String describeMismatch() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("was a class that violated the following rules for declaring a JPA entity type: ");

    RuleViolationAppender.append(stringBuilder, ruleViolations);

    return stringBuilder.toString();
  }
}
