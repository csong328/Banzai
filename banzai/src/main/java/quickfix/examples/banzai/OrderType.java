package quickfix.examples.banzai;

public enum OrderType {
  MARKET("Market", '1'), LIMIT("Limit", '2'), STOP("Stop", '3'), STOP_LIMIT("Stop Limit", '4');

  private final String name;
  private final char value;

  OrderType(final String name, final char value) {
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
