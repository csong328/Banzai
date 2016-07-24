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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import quickfix.FixVersions;
import quickfix.SessionID;
import quickfix.examples.banzai.LogonEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;
import quickfix.examples.banzai.ui.event.OrderEventType;
import quickfix.examples.banzai.ui.impl.OrderEntryControllerImpl;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OrderEntryViewTestConfig.class, loader = AnnotationConfigContextLoader.class)
public class OrderEntryViewTest extends ApplicationTest {
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private OrderEntryControllerImpl orderEntryController;

  @Mock
  private OrderEventListener listener;

  private SessionID sessionID;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    this.orderEntryController.setSelectedOrder(null);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = SpringFXMLLoader.create().applicationContext(this.applicationContext)
            .location(getClass().getResource("OrderEntryView.fxml")).load();
    final Scene scene = new Scene(root, 800, 400);
    stage.setScene(scene);
    stage.show();

    this.sessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, "Banzai", "EXEC");
    final LogonEvent logonEvent = new LogonEvent(this.sessionID, true);
    this.orderEntryController.update(null, logonEvent);
  }

  @Test
  public void checkDefaultValues() {
    verifyThat("#limitPriceTextField", Node::isDisabled);
    verifyThat("#stopPriceTextField", Node::isDisabled);
    verifyThat("#newButton", Node::isDisabled);
    verifyThat("#cancelButton", Node::isDisabled);
    verifyThat("#replaceButton", Node::isDisabled);
    verifyThat("#sideComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderSide.BUY);
    verifyThat("#typeComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderType.MARKET);
    verifyThat("#tifComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderTIF.DAY);
  }

  @Test
  public void testNewBuyMarketOrder() {
    clickOn("#symbolTextField").write("MSFT").clickOn("#quantityTextField").write("100");
    verifyThat("#newButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#newButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order order = event.getOrder();
    assertThat(order, is(notNullValue()));
    assertThat(order.getSymbol(), is("MSFT"));
    assertThat(order.getQuantity(), is(100));
    assertThat(order.getType(), is(OrderType.MARKET));
    assertThat(order.getTIF(), is(OrderTIF.DAY));
  }

  @Test
  public void testNewBuyLimitOrder() {
    clickOn("#symbolTextField").write("MSFT")
            .clickOn("#quantityTextField").write("100")
            .clickOn("#typeComboBox").push(KeyCode.DOWN)
            .clickOn("#limitPriceTextField").write("23.01");
    verifyThat("#newButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#newButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order order = event.getOrder();
    assertThat(order, is(notNullValue()));

    assertThat(order.getSymbol(), is("MSFT"));
    assertThat(order.getQuantity(), is(100));
    assertThat(order.getType(), is(OrderType.LIMIT));
    assertThat(order.getLimit(), is(23.01));
    assertThat(order.getTIF(), is(OrderTIF.DAY));
  }

  @Test
  public void testNewBuyStopOrder() {
    clickOn("#symbolTextField").write("MSFT")
            .clickOn("#quantityTextField").write("100")
            .clickOn("#typeComboBox").push(KeyCode.DOWN).push(KeyCode.DOWN)
            .clickOn("#stopPriceTextField").write("23.01");

    verifyThat("#newButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#newButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order order = event.getOrder();
    assertThat(order, is(notNullValue()));

    assertThat(order.getSymbol(), is("MSFT"));
    assertThat(order.getQuantity(), is(100));
    assertThat(order.getType(), is(OrderType.STOP));
    assertThat(order.getStop(), is(23.01));
    assertThat(order.getTIF(), is(OrderTIF.DAY));
  }

  @Test
  public void testNewBuyStopLimitOrder() {
    clickOn("#symbolTextField").write("MSFT")
            .clickOn("#quantityTextField").write("100")
            .clickOn("#typeComboBox").push(KeyCode.DOWN).push(KeyCode.DOWN).push(KeyCode.DOWN)
            .clickOn("#limitPriceTextField").write("23.01")
            .clickOn("#stopPriceTextField").write("23.05");

    verifyThat("#newButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#newButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order order = event.getOrder();
    assertThat(order, is(notNullValue()));

    assertThat(order.getSymbol(), is("MSFT"));
    assertThat(order.getQuantity(), is(100));
    assertThat(order.getType(), is(OrderType.STOP_LIMIT));
    assertThat(order.getLimit(), is(23.01));
    assertThat(order.getStop(), is(23.05));
    assertThat(order.getTIF(), is(OrderTIF.DAY));
  }

  @Test
  public void testReplaceMarketOrderIncreaseQty() {
    prepareMarketOrder();

    verifyThat("#symbolTextField", hasText("MSFT"));
    verifyThat("#quantityTextField", hasText("100"));
    verifyThat("#sideComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderSide.BUY);
    verifyThat("#typeComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderType.MARKET);
    verifyThat("#tifComboBox", node -> ((ComboBox) node).getSelectionModel().getSelectedItem() == OrderTIF.DAY);

    verifyThat("#newButton", node -> !node.isDisabled());
    verifyThat("#cancelButton", node -> !node.isDisabled());
    verifyThat("#replaceButton", Node::isDisabled);

    clickOn("#quantityTextField").eraseText(3).write("150");
    verifyThat("#replaceButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#replaceButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order replaceOrder = event.getOrder();
    assertThat(replaceOrder, is(notNullValue()));

    assertThat(replaceOrder.getQuantity(), is(150));
  }

  @Test
  public void testCancelMarketOrder() {
    prepareMarketOrder();

    verifyThat("#cancelButton", node -> !node.isDisabled());

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#cancelButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    final Order replaceOrder = event.getOrder();
    assertThat(replaceOrder, is(notNullValue()));
  }

  @Test
  public void testClear() {
    prepareMarketOrder();

    final ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
    this.orderEntryController.addOrderEventListener(this.listener);
    clickOn("#clearButton").push(KeyCode.ENTER);
    verify(this.listener, times(1)).handle(captor.capture());

    final OrderEvent event = captor.getValue();
    assertThat(event.getEventType(), is(OrderEventType.ClearAll));
  }

  @Test
  public void testLogOff() {
    final LogonEvent logonEvent = new LogonEvent(this.sessionID, false);
    this.orderEntryController.update(null, logonEvent);

    final ComboBox<SessionID> sessionComboBox = lookup("#sessionComboBox").queryFirst();
    assertThat(sessionComboBox.getSelectionModel().isEmpty(), is(true));
  }

  private void prepareMarketOrder() {
    final Order order = new Order();
    order.setSymbol("MSFT");
    order.setQuantity(100);
    order.setSide(OrderSide.BUY);
    order.setType(OrderType.MARKET);
    order.setTIF(OrderTIF.DAY);
    order.setExecuted(50);
    order.setOpen(50);
    order.setAvgPx(23.01);
    order.setSessionID(this.sessionID);

    this.orderEntryController.setSelectedOrder(order);
  }
}
