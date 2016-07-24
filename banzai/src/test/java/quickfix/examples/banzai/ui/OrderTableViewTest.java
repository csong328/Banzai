package quickfix.examples.banzai.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;
import quickfix.FixVersions;
import quickfix.SessionID;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;
import quickfix.examples.banzai.ui.impl.OrderTableControllerImpl;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OrderEntryViewTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class OrderTableViewTest extends ApplicationTest {
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private OrderTableControllerImpl orderTableController;
  @Mock
  private OrderEventListener listener;

  private SessionID sessionID;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    this.orderTableController.clear();
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = SpringFXMLLoader.create().applicationContext(this.applicationContext)
            .location(getClass().getResource("OrderTableView.fxml")).load();
    final Scene scene = new Scene(root, 800, 400);
    stage.setScene(scene);
    stage.show();
    this.sessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, "Banzai", "EXEC");
  }

  @Test
  public void testAddOrder() {
    final Order order = newOrder();

    this.orderTableController.addOrder(order);

    verifyThat("#orderTable", hasItems(1));
  }

  @Test
  public void testSelectOrder() {
    final Order order = newOrder();

    this.orderTableController.addOrder(order);

    clickOn("#orderTable");

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderTableController.addOrderEventListener(this.listener);
    clickOn(t -> (t instanceof TableRow) && ((TableRow) t).getIndex() == 0);
    verify(this.listener).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order selectedOrder = event.getOrder();
    assertThat(selectedOrder, is(order));
  }

  @Test
  public void testReplaceOrder() {
    final Order order = newOrder();
    this.orderTableController.addOrder(order);
    final Order newOrder = replaceOrder(order);
    this.orderTableController.replaceOrder(newOrder);

    verifyThat("#orderTable", hasItems(1));
    clickOn("#orderTable");

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderTableController.addOrderEventListener(this.listener);
    clickOn(t -> (t instanceof TableRow) && ((TableRow) t).getIndex() == 0);
    verify(this.listener).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order selectedOrder = event.getOrder();
    assertThat(selectedOrder, is(newOrder));
  }

  @Test
  public void testClear() {
    final Order order = newOrder();

    this.orderTableController.addOrder(order);

    verifyThat("#orderTable", hasItems(1));

    this.orderTableController.clear();
    verifyThat("#orderTable", hasItems(0));
  }

  private Order newOrder() {
    final Order order = new Order();
    order.setSymbol("MSFT");
    order.setQuantity(100);
    order.setSide(OrderSide.BUY);
    order.setType(OrderType.MARKET);
    order.setTIF(OrderTIF.DAY);
    order.setSessionID(this.sessionID);
    return order;
  }

  private Order replaceOrder(final Order order) {
    final Order newOrder = (Order) order.clone();
    newOrder.setQuantity(order.getQuantity() + 100);
    return newOrder;
  }

}
