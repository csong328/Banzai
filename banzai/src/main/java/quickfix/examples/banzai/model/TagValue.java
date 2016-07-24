package quickfix.examples.banzai.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TagValue {
  private final IntegerProperty tag = new SimpleIntegerProperty();
  private final StringProperty value = new SimpleStringProperty();

  public static TagValue of(final int tag, final Integer value) {
    return of(tag, Integer.toString(value));
  }

  public static TagValue of(final int tag, final Double value) {
    return of(tag, Double.toString(value));
  }

  public static TagValue of(final int tag, final char value) {
    return of(tag, Character.toString(value));
  }

  public static TagValue of(final int tag, final String value) {
    final TagValue pair = new TagValue();
    pair.setTag(tag);
    pair.setValue(value);
    return pair;
  }

  public IntegerProperty tagProperty() {
    return this.tag;
  }

  public int getTag() {
    return tagProperty().get();
  }

  public void setTag(final int tag) {
    this.tagProperty().set(tag);
  }

  public StringProperty valueProperty() {
    return this.value;
  }

  public String getValue() {
    return valueProperty().get();
  }

  public void setValue(final String value) {
    this.valueProperty().set(value);
  }

  @Override
  public String toString() {
    return String.format("%d=%s", getTag(), getValue());
  }
}
