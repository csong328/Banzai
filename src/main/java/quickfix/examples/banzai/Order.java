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

import quickfix.SessionID;
import quickfix.field.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Order implements Cloneable {
    private SessionID sessionID = null;
    private String symbol = null;
    private int quantity = 0;
    private int open = 0;
    private int executed = 0;
    private OrderSide side = OrderSide.BUY;
    private OrderType type = OrderType.MARKET;
    private OrderTIF tif = OrderTIF.DAY;
    private Double limit = null;
    private Double stop = null;
    private double avgPx = 0.0;
    private boolean rejected = false;
    private boolean canceled = false;
    private boolean isNew = true;
    private String message = null;
    private String ID = null;
    private String originalID = null;
    private static int nextID = 1;

    public Order() {
        ID = generateID();
    }

    public Order(String ID) {
        this.ID = ID;
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

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderTIF getTIF() {
        return tif;
    }

    public void setTIF(OrderTIF tif) {
        this.tif = tif;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public void setLimit(String limit) {
        if (limit == null || limit.equals("")) {
            this.limit = null;
        } else {
            this.limit = Double.parseDouble(limit);
        }
    }

    public Double getStop() {
        return stop;
    }

    public void setStop(Double stop) {
        this.stop = stop;
    }

    public void setStop(String stop) {
        if (stop == null || stop.equals("")) {
            this.stop = null;
        } else {
            this.stop = Double.parseDouble(stop);
        }
    }

    public void setAvgPx(double avgPx) {
        this.avgPx = avgPx;
    }

    public double getAvgPx() {
        return avgPx;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean getRejected() {
        return rejected;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean getCanceled() {
        return canceled;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setOriginalID(String originalID) {
        this.originalID = originalID;
    }

    public String getOriginalID() {
        return originalID;
    }

    public List<TagValue> tagValuePairs() {
        List<TagValue> list = new ArrayList<>();
        if (!isEmpty(ID)) {
            list.add(TagValue.of(ClOrdID.FIELD, ID));
        }
        if (!isEmpty(originalID)) {
            list.add(TagValue.of(OrigClOrdID.FIELD, originalID));
        }
        list.add(TagValue.of(Symbol.FIELD, this.symbol));
        list.add(TagValue.of(Side.FIELD, side.getValue()));
        list.add(TagValue.of(Quantity.FIELD, quantity));
        list.add(TagValue.of(OrdType.FIELD, type.getValue()));
        if (limit != null) {
            list.add(TagValue.of(Price.FIELD, limit));
        }
        if (stop != null) {
            list.add(TagValue.of(StopPx.FIELD, stop));
        }
        list.add(TagValue.of(LeavesQty.FIELD, open));
        list.add(TagValue.of(CumQty.FIELD, executed));
        list.add(TagValue.of(AvgPx.FIELD, avgPx));
        if (tif != null) {
            list.add(TagValue.of(TimeInForce.FIELD, tif.getValue()));
        }
        if (!isEmpty(message)) {
            list.add(TagValue.of(Text.FIELD, this.message));
        }
        return list;
    }
}