package quickfix.examples.banzai.ui;

import javafx.scene.input.MouseEvent;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.ui.event.OrderEventSource;

public interface OrderTableController<T extends Order> extends OrderEventSource {

  void addOrder(T order);

  void replaceOrder(T order);

  void clear();

  void onOrderSelected(MouseEvent mouseEvent);
}