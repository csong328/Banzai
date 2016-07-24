package quickfix.examples.banzai;

public enum OrderTIF {
  DAY("Day", '0'), IOC("IOC", '3'), OPG("OPG", '2'), GTC("GTC", '1'), GTX("GTX", '5');

  private final String name;
  private final char value;

  OrderTIF(final String name, final char value) {
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
