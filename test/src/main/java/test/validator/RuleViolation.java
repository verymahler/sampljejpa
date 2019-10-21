package test.validator;

public class RuleViolation {
  private String messageTemplate;
  private Object[] messageArgs;

  public RuleViolation(String messageTemplate, Object... messageArgs) {
    this.messageTemplate = messageTemplate;
    this.messageArgs = messageArgs;
  }

  public String getMessage() {
    return String.format(messageTemplate, messageArgs);
  }
}
