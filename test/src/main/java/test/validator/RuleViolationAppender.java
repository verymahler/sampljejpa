package test.validator;

import java.util.Iterator;
import java.util.List;

public final class RuleViolationAppender {

  private RuleViolationAppender() {
    // Private constructor that should prevent this class from getting instantiated
  }

  public static void append(StringBuilder stringBuilder, List<RuleViolation> ruleViolations) {
    for (Iterator<RuleViolation> iterator = ruleViolations.iterator(); iterator.hasNext();) {
      RuleViolation ruleViolation = iterator.next();
      append(stringBuilder, ruleViolation, iterator.hasNext() == false);
    }
  }

  private static void append(StringBuilder stringBuilder, RuleViolation ruleViolation,
      boolean isLast) {
    stringBuilder.append(ruleViolation.getMessage());
    if (isLast) {
      stringBuilder.append(".");
    } else {
      stringBuilder.append(", ");
    }
  }
}
