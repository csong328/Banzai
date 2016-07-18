package quickfix.examples.banzai;

public enum OrderSide {
    BUY("Buy", '1'), SELL("Sell", '2'), SHORT_SELL("Short Sell", '5'), SHORT_SELL_EXEMPT("Short Sell Exempt", '6'),
    CROSS("Cross", '8'), CROSS_SHORT("Cross Short", '9');

    private String name;
    private char value;

    OrderSide(String name, char value) {
        this.name = name;
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
