package quickfix.examples.banzai.ui.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.OrderTableController;
import quickfix.examples.banzai.ui.OrderTableModel;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventType;
import quickfix.examples.banzai.ui.event.SimpleOrderEventSource;

@Component("orderTableController")
public class OrderTableControllerImpl extends SimpleOrderEventSource implements OrderTableController, Initializable {
  @FXML
  private TableView<Order> orderTable;

  @FXML
  private TableColumn<Order, String> targetColumn;

  @Autowired
  private OrderTableModel orderTableModel;

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

  public void clear() {
    this.orderTableModel.clear();
  }

  @Override
  public void onOrderSelected(MouseEvent mouseEvent) {
    Order order = this.orderTable.getSelectionModel().getSelectedItem();
    notify(new OrderEvent(order, OrderEventType.OrderSelected));
  }

  private <S, T> void initializeColumn(TableColumn<S, T> column,
                                       Function<S, ObservableValue<T>> prop) {
    column.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
  }
}
