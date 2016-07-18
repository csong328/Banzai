package quickfix.examples.banzai;

public class TagValue {
    private int tag;
    private String value;

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

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
