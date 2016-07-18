package quickfix.examples.banzai;

public enum OrderType {
    MARKET("Market"), LIMIT("Limit"), STOP("Stop"), STOP_LIMIT("Stop Limit");

    private String name;

    OrderType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
