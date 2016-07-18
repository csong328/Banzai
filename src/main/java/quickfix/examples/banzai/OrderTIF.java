package quickfix.examples.banzai;

public enum OrderTIF {
    DAY("Day"), IOC("IOC"), OPG("OPG"), GTC("GTC"), GTX("GTX");

    private String name;

    OrderTIF(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
