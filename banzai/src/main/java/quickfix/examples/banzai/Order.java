/*******************************************************************************
 * Copyright (c) quickfixengine.org All rights reserved.
 * <p>
 * This file is part of the QuickFIX FIX Engine
 * <p>
 * This file may be distributed under the terms of the quickfixengine.org license as defined by
 * quickfixengine.org and appearing in the file LICENSE included in the packaging of this file.
 * <p>
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE WARRANTY OF DESIGN,
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * <p>
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 * <p>
 * Contact ask@quickfixengine.org if any conditions of this licensing are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import quickfix.SessionID;

public class Order implements Cloneable {
  private final ObjectProperty<SessionID> sessionID = new SimpleObjectProperty<>();
  private final StringProperty symbol = new SimpleStringProperty();
  private final IntegerProperty quantity = new SimpleIntegerProperty(0);
  private final IntegerProperty open = new SimpleIntegerProperty(0);
  private final IntegerProperty executed = new SimpleIntegerProperty(0);
  private final ObjectProperty<OrderSide> side = new SimpleObjectProperty<>(OrderSide.BUY);
  private final ObjectProperty<OrderType> type = new SimpleObjectProperty<>(OrderType.MARKET);
  private final ObjectProperty<OrderTIF> tif = new SimpleObjectProperty<>(OrderTIF.DAY);
  private final ObjectProperty<Double> limit = new SimpleObjectProperty<>();
  private final ObjectProperty<Double> stop = new SimpleObjectProperty<>();
  private final DoubleProperty avgPx = new SimpleDoubleProperty(0.0);
  private final BooleanProperty rejected = new SimpleBooleanProperty(false);
  private final BooleanProperty canceled = new SimpleBooleanProperty(false);
  private final BooleanProperty isNew = new SimpleBooleanProperty(true);
  private final StringProperty message = new SimpleStringProperty();
  private final StringProperty orderID = new SimpleStringProperty();
  private final StringProperty ID = new SimpleStringProperty();
  private final StringProperty originalID = new SimpleStringProperty();
  private static int nextID = 1;

  public Order() {
    this.ID.set(generateID());
  }

  public Order(final String ID) {
    this.ID.set(ID);
  }

  public Object clone() {
    final Order order = new Order();
    order.setOriginalID(getID());
    order.setSessionID(getSessionID());
    order.setSymbol(getSymbol());
    order.setQuantity(getQuantity());
    order.setOpen(getOpen());
    order.setExecuted(getExecuted());
    order.setSide(getSide());
    order.setType(getType());
    order.setTIF(getTIF());
    order.setLimit(getLimit());
    order.setStop(getStop());
    order.setAvgPx(getAvgPx());
    order.setRejected(getRejected());
    order.setCanceled(getCanceled());
    order.setMessage(getMessage());
    order.setOrderID(getOrderID());
    return order;
  }

  public String generateID() {
    return Long.toString(System.currentTimeMillis() + (nextID++));
  }

  public ObjectProperty<SessionID> sessionIDProperty() {
    return this.sessionID;
  }

  public SessionID getSessionID() {
    return sessionIDProperty().get();
  }

  public void setSessionID(final SessionID sessionID) {
    sessionIDProperty().set(sessionID);
  }

  public StringProperty symbolProperty() {
    return this.symbol;
  }

  public String getSymbol() {
    return symbolProperty().get();
  }

  public void setSymbol(final String symbol) {
    symbolProperty().set(symbol);
  }

  public IntegerProperty quantityProperty() {
    return this.quantity;
  }

  public int getQuantity() {
    return quantityProperty().get();
  }

  public void setQuantity(final int quantity) {
    quantityProperty().set(quantity);
  }

  public IntegerProperty openProperty() {
    return this.open;
  }

  public int getOpen() {
    return openProperty().get();
  }

  public void setOpen(final int open) {
    openProperty().set(open);
  }

  public IntegerProperty executedProperty() {
    return this.executed;
  }

  public int getExecuted() {
    return executedProperty().get();
  }

  public void setExecuted(final int executed) {
    executedProperty().set(executed);
  }

  public ObjectProperty<OrderSide> sideProperty() {
    return this.side;
  }

  public OrderSide getSide() {
    return sideProperty().get();
  }

  public void setSide(final OrderSide side) {
    sideProperty().set(side);
  }

  public ObjectProperty<OrderType> typeProperty() {
    return this.type;
  }

  public OrderType getType() {
    return typeProperty().get();
  }

  public void setType(final OrderType type) {
    typeProperty().set(type);
  }

  public ObjectProperty<OrderTIF> tifProperty() {
    return this.tif;
  }

  public OrderTIF getTIF() {
    return tifProperty().get();
  }

  public void setTIF(final OrderTIF tif) {
    tifProperty().set(tif);
  }

  public ObjectProperty<Double> limitProperty() {
    return this.limit;
  }

  public Double getLimit() {
    return limitProperty().get();
  }

  public void setLimit(final Double limit) {
    limitProperty().set(limit);
  }

  public void setLimit(final String limit) {
    if (limit == null || limit.equals("")) {
      limitProperty().set(null);
    } else {
      setLimit(Double.parseDouble(limit));
    }
  }

  public ObjectProperty<Double> stopProperty() {
    return this.stop;
  }

  public Double getStop() {
    return stopProperty().get();
  }

  public void setStop(final Double stop) {
    stopProperty().set(stop);
  }

  public void setStop(final String stop) {
    if (stop == null || stop.equals("")) {
      stopProperty().set(null);
    } else {
      setStop(Double.parseDouble(stop));
    }
  }

  public DoubleProperty avgPxProperty() {
    return this.avgPx;
  }

  public void setAvgPx(final double avgPx) {
    avgPxProperty().set(avgPx);
  }

  public double getAvgPx() {
    return avgPxProperty().get();
  }

  public BooleanProperty rejectedProperty() {
    return this.rejected;
  }

  public void setRejected(final boolean rejected) {
    rejectedProperty().set(rejected);
  }

  public boolean getRejected() {
    return rejectedProperty().get();
  }

  public BooleanProperty canceledProperty() {
    return this.canceled;
  }

  public void setCanceled(final boolean canceled) {
    canceledProperty().set(canceled);
  }

  public boolean getCanceled() {
    return canceledProperty().get();
  }

  public BooleanProperty isNewProperty() {
    return this.isNew;
  }

  public void setNew(final boolean isNew) {
    isNewProperty().set(isNew);
  }

  public boolean isNew() {
    return isNewProperty().get();
  }

  public StringProperty messageProperty() {
    return this.message;
  }

  public void setMessage(final String message) {
    messageProperty().set(message);
  }

  public String getMessage() {
    return messageProperty().get();
  }

  public StringProperty idProperty() {
    return this.ID;
  }

  public String getOrderID() {
    return this.orderID.get();
  }

  public StringProperty orderIDProperty() {
    return this.orderID;
  }

  public void setOrderID(final String orderID) {
    this.orderID.set(orderID);
  }

  public void setID(final String ID) {
    idProperty().set(ID);
  }

  public String getID() {
    return idProperty().get();
  }

  public StringProperty originalIDProperty() {
    return this.originalID;
  }

  public void setOriginalID(final String originalID) {
    originalIDProperty().set(originalID);
  }

  public String getOriginalID() {
    return this.originalID.get();
  }

}
