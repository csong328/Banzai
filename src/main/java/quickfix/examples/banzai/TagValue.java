package quickfix.examples.banzai;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TagValue {
    private IntegerProperty tag = new SimpleIntegerProperty();
    private StringProperty value = new SimpleStringProperty();


    public static TagValue of(int tag, Integer value) {
        return of(tag, Integer.toString(value));
    }

    public static TagValue of(int tag, Double value) {
        return of(tag, Double.toString(value));
    }

    public static TagValue of(int tag, char value) {
        return of(tag, Character.toString(value));
    }

    public static TagValue of(int tag, String value) {
        TagValue pair = new TagValue();
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

    public void setTag(int tag) {
        this.tagProperty().set(tag);
    }

    public StringProperty valueProperty() {
        return this.value;
    }

    public String getValue() {
        return valueProperty().get();
    }

    public void setValue(String value) {
        this.valueProperty().set(value);
    }
}
