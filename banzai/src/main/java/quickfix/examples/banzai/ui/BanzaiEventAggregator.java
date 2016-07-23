package quickfix.examples.banzai.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.application.IBanzaiService;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;

import static com.google.common.base.Preconditions.checkNotNull;

@Component("eventAggregator")
public class BanzaiEventAggregator implements OrderEventListener {

  @Autowired
  private OrderEntryController orderEntryController;
  @Autowired
  private OrderTableController orderTableController;
  @Autowired
  private ExecutionTableController executionTableController;

  @Autowired
  private IBanzaiService service;

  @PostConstruct
  public void init() {
    this.service.addOrderEventListener(this);
    this.orderEntryController.addOrderEventListener(this);
    this.orderTableController.addOrderEventListener(this);
  }

  @Override
  public void handle(OrderEvent event) {
    checkNotNull(event.getEventType(), "Event type not set");

    switch (event.getEventType()) {
      case OrderSelected:
        onOrderSelected(event);
        break;

      case New:
        onNewOrder(event);
        break;

      case Cancel:
        onCancelOrder(event);
        break;

      case Replace:
        onReplaceOrder(event);
        break;

      case OrderReplaced:
        onOrderReplaced(event);
        break;

      case Fill:
        onFill(event);
        break;

      case ClearAll:
        onClearAll();
        break;
    }
  }

  private void onOrderSelected(OrderEvent event) {
    Order order = event.getOrder();
    orderEntryController.setSelectedOrder(order);
  }

  private void onNewOrder(OrderEvent event) {
    Order order = event.getOrder();
    orderTableController.addOrder(order);
    service.send(order);
  }

  private void onCancelOrder(OrderEvent event) {
    Order order = event.getOrder();
    service.cancel(order);
  }

  private void onReplaceOrder(OrderEvent event) {
    Order order = event.getOrder();
    Order origOrder = (Order) event.getArg();
    service.replace(origOrder, order);
  }

  private void onOrderReplaced(OrderEvent event) {
    Order order = event.getOrder();
    orderTableController.replaceOrder(order);
  }

  private void onFill(OrderEvent event) {
    Execution execution = (Execution) event.getArg();
    executionTableController.addExecution(execution);
  }

  private void onClearAll() {
    orderTableController.clear();
    executionTableController.clear();
  }

}
