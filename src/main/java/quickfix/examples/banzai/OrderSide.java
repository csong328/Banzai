package quickfix.examples.banzai;

public enum OrderSide {
    BUY("Buy"), SELL("Sell"), SHORT_SELL("Short Sell"), SHORT_SELL_EXEMPT("Short Sell Exempt"),
    CROSS("Cross"), CROSS_SHORT("Cross Short"), CROSS_SHORT_EXEMPT("Cross Short Exempt");

    private String name;

    OrderSide(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
