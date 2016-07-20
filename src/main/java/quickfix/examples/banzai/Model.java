package quickfix.examples.banzai;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;

public interface Model {
    ObjectProperty<Order> selectedOrderProperty();

    Order getSelectedOrder();

    void setSelectedOrder(Order order);

    ObservableList<OrderSide> getSideList();

    ObservableList<OrderType> getTypeList();

    ObservableList<OrderTIF> getTIFList();

    ObservableList<SessionID> getSessionList();

    ObservableList<Order> getOrderList();

    void addOrder(Order order);

    Order getOrder(String ID);

    ObservableList<Execution> getExecutionList();

    void addExecution(Execution execution);

    Execution getExchangeExecution(String exchangeID);
}
