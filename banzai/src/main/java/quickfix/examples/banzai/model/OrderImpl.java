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

package quickfix.examples.banzai.model;

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

public class OrderImpl implements Order {
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

  public OrderImpl() {
  }

  public void copy(final Order other) {
    this.setOriginalID(other.getID());
    this.setSessionID(other.getSessionID());
    this.setSymbol(other.getSymbol());
    this.setQuantity(other.getQuantity());
    this.setOpen(other.getOpen());
    this.setExecuted(other.getExecuted());
    this.setSide(other.getSide());
    this.setType(other.getType());
    this.setTIF(other.getTIF());
    this.setLimit(other.getLimit());
    this.setStop(other.getStop());
    this.setAvgPx(other.getAvgPx());
    this.setRejected(other.getRejected());
    this.setCanceled(other.getCanceled());
    this.setMessage(other.getMessage());
    this.setOrderID(other.getOrderID());
  }

  public ObjectProperty<SessionID> sessionIDProperty() {
    return this.sessionID;
  }

  @Override
  public SessionID getSessionID() {
    return sessionIDProperty().get();
  }

  @Override
  public void setSessionID(final SessionID sessionID) {
    sessionIDProperty().set(sessionID);
  }

  public StringProperty symbolProperty() {
    return this.symbol;
  }

  @Override
  public String getSymbol() {
    return symbolProperty().get();
  }

  @Override
  public void setSymbol(final String symbol) {
    symbolProperty().set(symbol);
  }

  public IntegerProperty quantityProperty() {
    return this.quantity;
  }

  @Override
  public int getQuantity() {
    return quantityProperty().get();
  }

  @Override
  public void setQuantity(final int quantity) {
    quantityProperty().set(quantity);
  }

  public IntegerProperty openProperty() {
    return this.open;
  }

  @Override
  public int getOpen() {
    return openProperty().get();
  }

  @Override
  public void setOpen(final int open) {
    openProperty().set(open);
  }

  public IntegerProperty executedProperty() {
    return this.executed;
  }

  @Override
  public int getExecuted() {
    return executedProperty().get();
  }

  @Override
  public void setExecuted(final int executed) {
    executedProperty().set(executed);
  }

  public ObjectProperty<OrderSide> sideProperty() {
    return this.side;
  }

  @Override
  public OrderSide getSide() {
    return sideProperty().get();
  }

  @Override
  public void setSide(final OrderSide side) {
    sideProperty().set(side);
  }

  public ObjectProperty<OrderType> typeProperty() {
    return this.type;
  }

  @Override
  public OrderType getType() {
    return typeProperty().get();
  }

  @Override
  public void setType(final OrderType type) {
    typeProperty().set(type);
  }

  public ObjectProperty<OrderTIF> tifProperty() {
    return this.tif;
  }

  @Override
  public OrderTIF getTIF() {
    return tifProperty().get();
  }

  @Override
  public void setTIF(final OrderTIF tif) {
    tifProperty().set(tif);
  }

  public ObjectProperty<Double> limitProperty() {
    return this.limit;
  }

  @Override
  public Double getLimit() {
    return limitProperty().get();
  }

  @Override
  public void setLimit(final Double limit) {
    limitProperty().set(limit);
  }

  @Override
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

  @Override
  public Double getStop() {
    return stopProperty().get();
  }

  @Override
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

  @Override
  public void setAvgPx(final double avgPx) {
    avgPxProperty().set(avgPx);
  }

  @Override
  public double getAvgPx() {
    return avgPxProperty().get();
  }

  public BooleanProperty rejectedProperty() {
    return this.rejected;
  }

  @Override
  public void setRejected(final boolean rejected) {
    rejectedProperty().set(rejected);
  }

  @Override
  public boolean getRejected() {
    return rejectedProperty().get();
  }

  public BooleanProperty canceledProperty() {
    return this.canceled;
  }

  @Override
  public void setCanceled(final boolean canceled) {
    canceledProperty().set(canceled);
  }

  @Override
  public boolean getCanceled() {
    return canceledProperty().get();
  }

  public BooleanProperty isNewProperty() {
    return this.isNew;
  }

  @Override
  public void setNew(final boolean isNew) {
    isNewProperty().set(isNew);
  }

  @Override
  public boolean isNew() {
    return isNewProperty().get();
  }

  public StringProperty messageProperty() {
    return this.message;
  }

  @Override
  public void setMessage(final String message) {
    messageProperty().set(message);
  }

  @Override
  public String getMessage() {
    return messageProperty().get();
  }

  public StringProperty idProperty() {
    return this.ID;
  }

  @Override
  public String getOrderID() {
    return this.orderID.get();
  }

  public StringProperty orderIDProperty() {
    return this.orderID;
  }

  @Override
  public void setOrderID(final String orderID) {
    this.orderID.set(orderID);
  }

  @Override
  public void setID(final String ID) {
    idProperty().set(ID);
  }

  @Override
  public String getID() {
    return idProperty().get();
  }

  public StringProperty originalIDProperty() {
    return this.originalID;
  }

  @Override
  public void setOriginalID(final String originalID) {
    originalIDProperty().set(originalID);
  }

  @Override
  public String getOriginalID() {
    return this.originalID.get();
  }

}
