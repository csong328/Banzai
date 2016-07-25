package quickfix.examples.exchange.model;

import quickfix.Message;
import quickfix.SessionID;

public class Order {
  private String ID;

  private double quantity;
  private double open;
  private double executed;
  private char ordStatus;
  private double avgPx;

  private SessionID sessionID;
  private Message pendingMessage;
  private Message processedMessage;

  public String getID() {
    return this.ID;
  }

  public void setID(final String ID) {
    this.ID = ID;
  }

  public double getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final double quantity) {
    this.quantity = quantity;
  }

  public double getOpen() {
    return this.open;
  }

  public void setOpen(final double open) {
    this.open = open;
  }

  public double getExecuted() {
    return this.executed;
  }

  public void setExecuted(final double executed) {
    this.executed = executed;
  }

  public char getOrdStatus() {
    return this.ordStatus;
  }

  public void setOrdStatus(final char ordStatus) {
    this.ordStatus = ordStatus;
  }

  public double getAvgPx() {
    return this.avgPx;
  }

  public void setAvgPx(final double avgPx) {
    this.avgPx = avgPx;
  }

  public SessionID getSessionID() {
    return this.sessionID;
  }

  public void setSessionID(final SessionID sessionID) {
    this.sessionID = sessionID;
  }

  public Message getPendingMessage() {
    return this.pendingMessage;
  }

  public void setPendingMessage(final Message pendingMessage) {
    this.pendingMessage = pendingMessage;
  }

  public Message getProcessedMessage() {
    return this.processedMessage;
  }

  public void setProcessedMessage(final Message processedMessage) {
    this.processedMessage = processedMessage;
  }
}
