package quickfix.examples.banzai.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderImpl;
import quickfix.examples.banzai.ui.ExecutionTableController;
import quickfix.examples.banzai.ui.OrderEntryController;
import quickfix.examples.banzai.ui.OrderTableController;
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
  private IMarketConnectivity connectivity;

  @PostConstruct
  public void init() {
    this.connectivity.addOrderEventListener(this);
    this.connectivity.addLogonObserver(this.orderEntryController);
    this.orderEntryController.addOrderEventListener(this);
    this.orderTableController.addOrderEventListener(this);
  }

  @Override
  public void handle(final OrderEvent event) {
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
        onClearAll(event);
        break;
    }
  }

  private void onOrderSelected(final OrderEvent event) {
    final Order order = event.getOrder();
    this.orderEntryController.setSelectedOrder(order);
  }

  private void onNewOrder(final OrderEvent event) {
    final Order order = event.getOrder();
    this.orderTableController.addOrder(order);
    this.connectivity.send(order);
  }

  private void onCancelOrder(final OrderEvent event) {
    final Order order = event.getOrder();
    this.connectivity.cancel(order);
  }

  private void onReplaceOrder(final OrderEvent event) {
    final Order order = event.getOrder();
    final OrderImpl origOrder = (OrderImpl) event.getArg();
    this.connectivity.replace(origOrder, order);
  }

  private void onOrderReplaced(final OrderEvent event) {
    final Order order = event.getOrder();
    this.orderTableController.replaceOrder(order);
  }

  private void onFill(final OrderEvent event) {
    final Execution execution = (Execution) event.getArg();
    this.executionTableController.addExecution(execution);
  }

  private void onClearAll(final OrderEvent event) {
    this.orderTableController.clear();
    this.executionTableController.clear();
  }

}
