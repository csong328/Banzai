package quickfix.examples.banzai.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.Order;

@Component("orderTableController")
public class OrderTableController implements Initializable {
  @FXML
  private TableView<Order> orderTable;

  @FXML
  private TableColumn<Order, String> targetColumn;

  @Autowired
  private OrderTableModel orderTableModel;
  @Autowired
  private OrderEntryModel orderEntryModel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.orderTable.setItems(orderTableModel.getOrderList());

    initializeColumn(targetColumn,
        order -> new ReadOnlyObjectWrapper<>(order.getSessionID().getTargetCompID()));
  }

  public void replaceOrder(Order newOrder) {
    this.orderTableModel.replaceOrder(newOrder);
  }

  public void onOrderSelected(Event event) {
    Order order = this.orderTable.getSelectionModel().getSelectedItem();
    orderEntryModel.setSelectedOrder(order);
  }

  private <S, T> void initializeColumn(TableColumn<S, T> column,
      Function<S, ObservableValue<T>> prop) {
    column.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
  }
}
