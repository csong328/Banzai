package quickfix.examples.banzai;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component("model")
public class ModelImpl implements Model {

    private ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();
    private final ObservableList<Order> orderList;
    private final Map<String, Order> idToOrder = new HashMap<>();

    private final ObservableList<Execution> executionList;
    private final Map<String, Execution> exchangeIdToExecution = new HashMap<>();

    public ModelImpl() {
        orderList = FXCollections.observableArrayList(o ->
                new Observable[]{o.executedProperty(), o.openProperty(), o.avgPxProperty(),
                        o.messageProperty(), o.canceledProperty(),
                        o.isNewProperty(), o.rejectedProperty()});
        executionList = FXCollections.observableArrayList(e -> new Observable[]{});
    }

    @PostConstruct
    public void init() {
    }

    @Override
    public ObservableList<Order> getOrderList() {
        return this.orderList;
    }

    @Override
    public void addOrder(Order order) {
        idToOrder.put(order.getID(), order);
        orderList.add(order);
    }

    @Override
    public Order getOrder(String ID) {
        return idToOrder.get(ID);
    }

    @Override
    public ObservableList<Execution> getExecutionList() {
        return this.executionList;
    }

    @Override
    public void addExecution(Execution execution) {
        if (exchangeIdToExecution.containsKey(execution.getExchangeID())) {
            return;
        }
        exchangeIdToExecution.put(execution.getExchangeID(), execution);
        this.executionList.add(execution);
    }

    @Override
    public Execution getExchangeExecution(String exchangeID) {
        return exchangeIdToExecution.get(exchangeID);
    }

    @Override
    public ObjectProperty<Order> selectedOrderProperty() {
        return this.selectedOrder;
    }

    @Override
    public Order getSelectedOrder() {
        return selectedOrderProperty().get();
    }

    @Override
    public void setSelectedOrder(Order order) {
        selectedOrderProperty().set(order);
    }
}
