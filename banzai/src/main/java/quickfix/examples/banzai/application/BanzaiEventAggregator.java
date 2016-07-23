package quickfix.examples.banzai.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.ExecutionTableController;
import quickfix.examples.banzai.ui.ExecutionTableModel;
import quickfix.examples.banzai.ui.OrderEntryController;
import quickfix.examples.banzai.ui.OrderEntryModel;
import quickfix.examples.banzai.ui.OrderTableController;
import quickfix.examples.banzai.ui.OrderTableModel;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;

import static com.google.common.base.Preconditions.checkNotNull;

@Component("eventAggregator")
public class BanzaiEventAggregator implements OrderEventListener {
  @Autowired
  private OrderEntryModel orderEntryModel;
  @Autowired
  private ExecutionTableModel executionTableModel;
  @Autowired
  private OrderTableModel orderTableModel;

  @Autowired
  private OrderEntryController orderEntryController;
  @Autowired
  private OrderTableController orderTableController;
  @Autowired
  private ExecutionTableController executionTableController;

  @Autowired
  private BanzaiApplication service;

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

      case ClearAll:
        onClearAll();
        break;

      case OrderReplaced:
        onOrderReplaced(event);
        break;

      case Fill:
        onFill(event);
        break;
    }
  }

  private void onOrderSelected(OrderEvent event) {
    Order order = event.getOrder();
    orderEntryModel.setSelectedOrder(order);
  }

  private void onNewOrder(OrderEvent event) {
    Order order = event.getOrder();
    orderTableModel.addOrder(order);
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
    executionTableModel.addExecution(execution);
  }

  private void onClearAll() {
    orderTableModel.clear();
    executionTableModel.clear();
  }

}
