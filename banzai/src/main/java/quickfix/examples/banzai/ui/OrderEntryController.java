package quickfix.examples.banzai.ui;

import java.util.Observer;

import javafx.event.ActionEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventSource;

public interface OrderEntryController extends OrderEventSource, Observer {

  void setSelectedOrder(Order order);

  void onNewOrder(ActionEvent actionEvent);

  void onCancelOrder(ActionEvent actionEvent);

  void onReplaceOrder(ActionEvent actionEvent);

  void onClear(ActionEvent actionEvent);
}
