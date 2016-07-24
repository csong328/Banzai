package quickfix.examples.banzai.ui;

import javafx.scene.input.MouseEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventSource;

public interface OrderTableController extends OrderEventSource {

  void addOrder(Order order);

  void replaceOrder(Order order);

  void clear();

  void onOrderSelected(MouseEvent mouseEvent);
}