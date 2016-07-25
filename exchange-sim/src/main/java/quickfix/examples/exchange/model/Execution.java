package quickfix.examples.exchange.model;

public class Execution {
  private String execID;
  private String orderID;
  private double lastShares;
  private double lastPx;

  public String getExecID() {
    return this.execID;
  }

  public void setExecID(final String execID) {
    this.execID = execID;
  }

  public String getOrderID() {
    return this.orderID;
  }

  public void setOrderID(final String orderID) {
    this.orderID = orderID;
  }

  public double getLastShares() {
    return this.lastShares;
  }

  public void setLastShares(final double lastShares) {
    this.lastShares = lastShares;
  }

  public double getLastPx() {
    return this.lastPx;
  }

  public void setLastPx(final double lastPx) {
    this.lastPx = lastPx;
  }
}
