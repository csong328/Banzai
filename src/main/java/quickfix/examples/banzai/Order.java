/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 * <p>
 * This file is part of the QuickFIX FIX Engine
 * <p>
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 * <p>
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 * <p>
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 * <p>
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai;

import javafx.beans.property.*;
import quickfix.SessionID;
import quickfix.field.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Order implements Cloneable {
    private ObjectProperty<SessionID> sessionID = new SimpleObjectProperty<>();
    private StringProperty symbol = new SimpleStringProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty(0);
    private IntegerProperty open = new SimpleIntegerProperty(0);
    private IntegerProperty executed = new SimpleIntegerProperty(0);
    private ObjectProperty<OrderSide> side = new SimpleObjectProperty<>(OrderSide.BUY);
    private ObjectProperty<OrderType> type = new SimpleObjectProperty<>(OrderType.MARKET);
    private ObjectProperty<OrderTIF> tif = new SimpleObjectProperty<>(OrderTIF.DAY);
    private ObjectProperty<Double> limit = new SimpleObjectProperty<>();
    private ObjectProperty<Double> stop = new SimpleObjectProperty<>();
    private DoubleProperty avgPx = new SimpleDoubleProperty(0.0);
    private BooleanProperty rejected = new SimpleBooleanProperty(false);
    private BooleanProperty canceled = new SimpleBooleanProperty(false);
    private BooleanProperty isNew = new SimpleBooleanProperty(true);
    private StringProperty message = new SimpleStringProperty();
    private StringProperty ID = new SimpleStringProperty();
    private StringProperty originalID = new SimpleStringProperty();
    private static int nextID = 1;

    public Order() {
        ID.set(generateID());
    }

    public Order(String ID) {
        this.ID.set(ID);
    }

    public Object clone() {
        try {
            Order order = (Order) super.clone();
            order.setOriginalID(getID());
            order.setID(order.generateID());
            return order;
        } catch (CloneNotSupportedException e) {
            // ignore
        }
        return null;
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

    public void setSessionID(SessionID sessionID) {
        sessionIDProperty().set(sessionID);
    }

    public StringProperty symbolProperty() {
        return this.symbol;
    }

    public String getSymbol() {
        return symbolProperty().get();
    }

    public void setSymbol(String symbol) {
        symbolProperty().set(symbol);
    }

    public IntegerProperty quantityProperty() {
        return this.quantity;
    }

    public int getQuantity() {
        return quantityProperty().get();
    }

    public void setQuantity(int quantity) {
        quantityProperty().set(quantity);
    }

    public IntegerProperty openProperty() {
        return this.open;
    }

    public int getOpen() {
        return openProperty().get();
    }

    public void setOpen(int open) {
        openProperty().set(open);
    }

    public IntegerProperty executedProperty() {
        return this.executed;
    }

    public int getExecuted() {
        return executedProperty().get();
    }

    public void setExecuted(int executed) {
        executedProperty().set(executed);
    }

    public ObjectProperty<OrderSide> sideProperty() {
        return this.side;
    }

    public OrderSide getSide() {
        return sideProperty().get();
    }

    public void setSide(OrderSide side) {
        sideProperty().set(side);
    }

    public ObjectProperty<OrderType> typeProperty() {
        return this.type;
    }

    public OrderType getType() {
        return typeProperty().get();
    }

    public void setType(OrderType type) {
        typeProperty().set(type);
    }

    public ObjectProperty<OrderTIF> tifProperty() {
        return this.tif;
    }

    public OrderTIF getTIF() {
        return tifProperty().get();
    }

    public void setTIF(OrderTIF tif) {
        tifProperty().set(tif);
    }

    public ObjectProperty<Double> limitProperty() {
        return this.limit;
    }

    public Double getLimit() {
        return limitProperty().get();
    }

    public void setLimit(Double limit) {
        limitProperty().set(limit);
    }

    public void setLimit(String limit) {
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

    public void setStop(Double stop) {
        stopProperty().set(stop);
    }

    public void setStop(String stop) {
        if (stop == null || stop.equals("")) {
            stopProperty().set(null);
        } else {
            setStop(Double.parseDouble(stop));
        }
    }

    public DoubleProperty avgPxProperty() {
        return this.avgPx;
    }

    public void setAvgPx(double avgPx) {
        avgPxProperty().set(avgPx);
    }

    public double getAvgPx() {
        return avgPxProperty().get();
    }

    public BooleanProperty rejectedProperty() {
        return this.rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejectedProperty().set(rejected);
    }

    public boolean getRejected() {
        return rejectedProperty().get();
    }

    public BooleanProperty canceledProperty() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceledProperty().set(canceled);
    }

    public boolean getCanceled() {
        return canceledProperty().get();
    }

    public BooleanProperty isNewProperty() {
        return this.isNew;
    }

    public void setNew(boolean isNew) {
        this.isNewProperty().set(isNew);
    }

    public boolean isNew() {
        return isNewProperty().get();
    }

    public StringProperty messageProperty() {
        return this.message;
    }

    public void setMessage(String message) {
        this.messageProperty().set(message);
    }

    public String getMessage() {
        return messageProperty().get();
    }

    public StringProperty idProperty() {
        return this.ID;
    }

    public void setID(String ID) {
        this.idProperty().set(ID);
    }

    public String getID() {
        return idProperty().get();
    }

    public StringProperty originalIDProperty() {
        return this.originalID;
    }

    public void setOriginalID(String originalID) {
        this.originalIDProperty().set(originalID);
    }

    public String getOriginalID() {
        return originalID.get();
    }

    public List<TagValue> tagValuePairs() {
        List<TagValue> list = new ArrayList<>();
        if (!isEmpty(getID())) {
            list.add(TagValue.of(ClOrdID.FIELD, getID()));
        }
        if (!isEmpty(getOriginalID())) {
            list.add(TagValue.of(OrigClOrdID.FIELD, getOriginalID()));
        }
        list.add(TagValue.of(Symbol.FIELD, getSymbol()));
        list.add(TagValue.of(Side.FIELD, getSide().getValue()));
        list.add(TagValue.of(Quantity.FIELD, getQuantity()));
        list.add(TagValue.of(OrdType.FIELD, getType().getValue()));
        if (getLimit() != null) {
            list.add(TagValue.of(Price.FIELD, getLimit()));
        }
        if (getStop() != null) {
            list.add(TagValue.of(StopPx.FIELD, getStop()));
        }
        list.add(TagValue.of(LeavesQty.FIELD, getOpen()));
        list.add(TagValue.of(CumQty.FIELD, getExecuted()));
        list.add(TagValue.of(AvgPx.FIELD, getAvgPx()));
        if (tif != null) {
            list.add(TagValue.of(TimeInForce.FIELD, getTIF().getValue()));
        }
        if (!isEmpty(getMessage())) {
            list.add(TagValue.of(Text.FIELD, getMessage()));
        }
        return list;
    }
}
