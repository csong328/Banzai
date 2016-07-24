package quickfix.examples.banzai.utils;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class FXUtils {
  private static final String INTEGER_PATTERN = "\\d*";

  private static final String DOUBLE_PATTERN = "\\d*(\\.\\d*)?";

  private static final ChangeListener<String> INTEGER_TEXT_FIELD_CHANGE_LISTENER =
          (observable, oldValue, newValue) -> {
            if (!newValue.matches(INTEGER_PATTERN)) {
              ((StringProperty) observable).set(oldValue);
            }
          };

  private static final ChangeListener<String> DOUBLE_TEXT_FIELD_CHANGE_LISTENER =
          (observable, oldValue, newValue) -> {
            if (!newValue.matches(DOUBLE_PATTERN)) {
              ((StringProperty) observable).set(oldValue);
            }
          };

  private FXUtils() {
  }

  public static ChangeListener<String> integerFieldChangeListener() {
    return INTEGER_TEXT_FIELD_CHANGE_LISTENER;
  }

  public static ChangeListener<String> doubleFieldChangeListener() {
    return DOUBLE_TEXT_FIELD_CHANGE_LISTENER;
  }
}
