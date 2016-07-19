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
import quickfix.field.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Execution {
    private StringProperty symbol = new SimpleStringProperty();
    private IntegerProperty quantity = new SimpleIntegerProperty(0);
    private ObjectProperty<OrderSide> side = new SimpleObjectProperty<>(OrderSide.BUY);
    private DoubleProperty price = new SimpleDoubleProperty();
    private StringProperty ID = new SimpleStringProperty();
    private StringProperty exchangeID = new SimpleStringProperty();
    private static int nextID = 1;

    public Execution() {
        this(Integer.toString(nextID++));
    }

    public Execution(String ID) {
        this.ID.set(ID);
    }

    public StringProperty symbolProperty() {
        return this.symbol;
    }

    public String getSymbol() {
        return symbolProperty().get();
    }

    public void setSymbol(String symbol) {
        this.symbolProperty().set(symbol);
    }

    public IntegerProperty quantityProperty() {
        return this.quantity;
    }

    public int getQuantity() {
        return quantityProperty().get();
    }

    public void setQuantity(int quantity) {
        this.quantityProperty().set(quantity);
    }

    public ObjectProperty<OrderSide> sideProperty() {
        return this.side;
    }

    public OrderSide getSide() {
        return sideProperty().get();
    }

    public void setSide(OrderSide side) {
        this.sideProperty().set(side);
    }

    public DoubleProperty priceProperty() {
        return this.price;
    }

    public double getPrice() {
        return priceProperty().get();
    }

    public void setPrice(double price) {
        this.priceProperty().set(price);
    }

    public StringProperty idProperty() {
        return this.ID;
    }

    public String getID() {
        return idProperty().get();
    }

    public StringProperty exchangeIDProperty() {
        return this.exchangeID;
    }

    public void setExchangeID(String exchangeID) {
        this.exchangeIDProperty().set(exchangeID);
    }

    public String getExchangeID() {
        return exchangeIDProperty().get();
    }

    public List<TagValue> tagValuePairs() {
        List<TagValue> list = new ArrayList<>();
        if (!isEmpty(getExchangeID())) {
            list.add(TagValue.of(ExecID.FIELD, getExchangeID()));
        }
        if (!isEmpty(getSymbol())) {
            list.add(TagValue.of(Symbol.FIELD, getSymbol()));
        }
        list.add(TagValue.of(Side.FIELD, getSide().getValue()));
        list.add(TagValue.of(LastShares.FIELD, getQuantity()));
        list.add(TagValue.of(LastPx.FIELD, getPrice()));
        return list;
    }
}
