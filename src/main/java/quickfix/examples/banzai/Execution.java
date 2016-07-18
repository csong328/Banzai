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

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Execution {
    private String symbol = null;
    private int quantity = 0;
    private OrderSide side = OrderSide.BUY;
    private double price;
    private String ID = null;
    private String exchangeID = null;
    private static int nextID = 1;

    public Execution() {
        ID = Integer.toString(nextID++);
    }

    public Execution(String ID) {
        this.ID = ID;
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

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getID() {
        return ID;
    }

    public void setExchangeID(String exchangeID) {
        this.exchangeID = exchangeID;
    }

    public String getExchangeID() {
        return exchangeID;
    }

    public List<TagValue> tagValuePairs() {
        List<TagValue> list = new ArrayList<>();
        if (!isEmpty(exchangeID)) {
            list.add(TagValue.of(17, exchangeID));
        }
        if (!isEmpty(symbol)) {
            list.add(TagValue.of(55, symbol));
        }
        list.add(TagValue.of(54, side.getValue()));
        list.add(TagValue.of(32, quantity));
        list.add(TagValue.of(31, price));
        return list;
    }
}
