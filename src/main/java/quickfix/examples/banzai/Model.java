package quickfix.examples.banzai;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public interface Model {
    ObservableList<Order> getOrderList();

    void addOrder(Order order);

    Order getOrder(String ID);

    ObservableList<Execution> getExecutionList();

    void addExecution(Execution execution);

    Execution getExchangeExecution(String exchangeID);

    ObjectProperty<Order> selectedOrderProperty();

    Order getSelectedOrder();

    void setSelectedOrder(Order order);

    ObservableList<OrderSide> getSideList();
    ObservableList<OrderType> getTypeList();
    ObservableList<OrderTIF> getTIFList();
}
