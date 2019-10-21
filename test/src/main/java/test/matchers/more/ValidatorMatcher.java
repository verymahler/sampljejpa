package test.matchers.more;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class ValidatorMatcher<T> extends BaseMatcher<T> {
  private static Validator validator;


  private boolean matchesIfValid;
  private String expectedMessage;
  private Set<ConstraintViolation<Object>> constraintViolations;



  // Static initializer
  static {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }



  private ValidatorMatcher(boolean matchesIfValid) {
    this.matchesIfValid = matchesIfValid;
  }

  private ValidatorMatcher(boolean matchesIfValid, String expectedMessage) {
    this.matchesIfValid = matchesIfValid;
    this.expectedMessage = expectedMessage;
  }



  public static <T> Matcher<T> valid() {
    return new ValidatorMatcher<>(true);
  }

  public static <T> Matcher<T> invalidAndMessageContains(String paramExpectedMessage) {
    checkNotNull(paramExpectedMessage);

    return new ValidatorMatcher<>(false, paramExpectedMessage);
  }



  @Override
  public boolean matches(Object paramObject) {
    constraintViolations = validator.validate(paramObject);

    if (matchesIfValid) {
      return hasNoConstraintViolations();
    }
    if (hasConstraintViolations()) {
      return containsExpectedMessage();
    }
    return false;
  }

  @Override
  public void describeTo(Description description) {
    if (matchesIfValid) {
      description.appendText("object that validates using the default Validator");
    } else {
      if (expectedMessage != null) {
        description
            .appendText(String.format(
                "object that validates not using the default Validator and the message"
                    + " of the single expected ConstraintViolation contains [%s]",
                expectedMessage));
      } else {
        description.appendText("object that validates not using the default Validator");
      }
    }
  }

  @Override
  public void describeMismatch(Object item, Description description) {
    description.appendText(describeMismatch());
  }



  private boolean hasNoConstraintViolations() {
    return constraintViolations.isEmpty();
  }

  private boolean hasConstraintViolations() {
    return hasNoConstraintViolations() == false;
  }

  private boolean containsExpectedMessage() {
    if (expectedMessage != null) {
      if (constraintViolations.size() == 1) {
        return toMessage(constraintViolations.iterator().next()).contains(expectedMessage);
      }
      return false;
    }
    return true;
  }

  private String describeMismatch() {
    StringBuilder stringBuilder = new StringBuilder();
    if (matchesIfValid) {
      describeMismatchIfNotValid(stringBuilder);
    } else {
      describeMismatchIfValidOrUnexpected(stringBuilder);
    }
    return stringBuilder.toString();
  }

  private void describeMismatchIfNotValid(StringBuilder stringBuilder) {
    stringBuilder.append("was object that violated the following constraints: ");
    appendConstraintViolations(stringBuilder);
  }

  private void describeMismatchIfValidOrUnexpected(StringBuilder stringBuilder) {
    if (constraintViolations.isEmpty()) {
      stringBuilder.append("was valid object.");
    } else {
      describeMismatchIfNotValid(stringBuilder);
    }
  }

  private void appendConstraintViolations(StringBuilder stringBuilder) {
    for (Iterator<ConstraintViolation<Object>> iterator = constraintViolations.iterator(); iterator
        .hasNext();) {
      ConstraintViolation<Object> constraintViolation = iterator.next();
      appendConstraintViolation(stringBuilder, constraintViolation, iterator.hasNext() == false);
    }
  }

  private String toMessage(ConstraintViolation<?> constraintViolation) {
    StringBuilder stringBuilder = new StringBuilder();
    appendConstraintViolation(stringBuilder, constraintViolation, true);
    return stringBuilder.toString();
  }

  private void appendConstraintViolation(StringBuilder stringBuilder,
      ConstraintViolation<?> constraintViolation, boolean isLast) {
    stringBuilder.append(constraintViolation.getPropertyPath());
    stringBuilder.append(" ");
    stringBuilder.append(constraintViolation.getMessage());
    if (isLast) {
      stringBuilder.append(".");
    } else {
      stringBuilder.append(", ");
    }
  }
}
