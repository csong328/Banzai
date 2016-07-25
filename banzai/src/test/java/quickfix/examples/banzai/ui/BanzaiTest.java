package quickfix.examples.banzai.ui;

import com.google.common.base.Predicate;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import quickfix.FixVersions;
import quickfix.SessionID;
import quickfix.examples.banzai.application.IMarketConnectivity;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, OMSConfig.class}, loader = AnnotationConfigContextLoader.class)
public class BanzaiTest extends ApplicationTest {
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private IMarketConnectivity marketConnectivity;

  private SessionID sessionID;

  @After
  public void tearDown() throws Exception {
    clickOn("#clearButton").push(KeyCode.ENTER);
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = SpringFXMLLoader.create().applicationContext(this.applicationContext)
            .location(getClass().getResource("banzai.fxml")).load();
    final Scene scene = new Scene(root, 800, 400);
    stage.setScene(scene);
    stage.show();

    this.sessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, "Banzai", "EXEC");
    this.marketConnectivity.onLogon(this.sessionID);
  }

  @Test
  public void testMSFTNewReplaceCancel() {
    clickOn("#symbolTextField").write("MSFT").clickOn("#quantityTextField").write("100");
    verifyThat("#newButton", node -> !node.isDisabled());
    clickOn("#newButton").push(KeyCode.ENTER);
    verifyThat("#orderTable", hasItems(1));
    verifyThat("#executionTable", hasItems(1));

    Order order = lookupRow("#orderTable", 0);
    assertThat(order.getQuantity(), is(100));
    assertThat(order.getOpen(), is(90));
    assertThat(order.getExecuted(), is(10));
    assertThat(order.getAvgPx(), is(5.0));

    final Predicate<TableRow> firstRow = t -> t.getIndex() == 0;
    clickOn("#orderTable");
    clickOn(firstRow);

    clickOn("#quantityTextField").eraseText(3).write("120");
    clickOn("#replaceButton").push(KeyCode.ENTER);

    order = lookupRow("#orderTable", 0);
    assertThat(order.getQuantity(), is(120));
    assertThat(order.getOpen(), is(110));
    assertThat(order.getExecuted(), is(10));
    assertThat(order.getAvgPx(), is(5.0));

    clickOn(firstRow);
    clickOn("#cancelButton").push(KeyCode.ENTER);

    order = lookupRow("#orderTable", 0);
    assertThat(order.getQuantity(), is(120));
    assertThat(order.getOpen(), is(0));
    assertThat(order.getExecuted(), is(10));
    assertThat(order.getAvgPx(), is(5.0));
  }

  private <T> T lookupRow(final String queryString, final int index) {
    final TableView table = lookup(queryString).queryFirst();
    return (T) table.getItems().get(index);
  }
}
