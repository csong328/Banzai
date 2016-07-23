package quickfix.examples.banzai.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;
import quickfix.examples.banzai.ui.event.OrderEventType;

@Component("orderTableController")
public class OrderTableController implements Initializable {
  @FXML
  private TableView<Order> orderTable;

  @FXML
  private TableColumn<Order, String> targetColumn;

  @Autowired
  private OrderTableModel orderTableModel;

  private List<OrderEventListener> eventListenerList = new ArrayList<>();

  public void addOrderEventListener(OrderEventListener listener) {
    eventListenerList.add(listener);
  }

  private void notify(OrderEvent event) {
    eventListenerList.forEach(l -> l.handle(event));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.orderTable.setItems(orderTableModel.getOrderList());

    initializeColumn(targetColumn,
            order -> new ReadOnlyObjectWrapper<>(order.getSessionID().getTargetCompID()));
  }

  public void addOrder(Order order) {
    this.orderTableModel.addOrder(order);
  }

  public void replaceOrder(Order newOrder) {
    this.orderTableModel.replaceOrder(newOrder);
  }

  @FXML
  public void onOrderSelected(Event event) {
    Order order = this.orderTable.getSelectionModel().getSelectedItem();
    notify(new OrderEvent(order, OrderEventType.OrderSelected));
  }

  private <S, T> void initializeColumn(TableColumn<S, T> column,
                                       Function<S, ObservableValue<T>> prop) {
    column.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
  }
}
