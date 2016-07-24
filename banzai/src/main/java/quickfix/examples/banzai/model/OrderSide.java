package quickfix.examples.banzai.model;

public enum OrderSide {
  BUY("Buy", '1'), SELL("Sell", '2'), SHORT_SELL("Short Sell", '5'), SHORT_SELL_EXEMPT(
          "Short Sell Exempt", '6'), CROSS("Cross", '8'), CROSS_SHORT("Cross Short", '9');

  private final String name;
  private final char value;

  OrderSide(final String name, final char value) {
    this.name = name;
    this.value = value;
  }

  public char getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
