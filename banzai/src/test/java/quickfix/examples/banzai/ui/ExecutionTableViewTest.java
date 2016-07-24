package quickfix.examples.banzai.ui;

import org.junit.After;
import org.junit.Before;
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
import javafx.stage.Stage;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.ExecutionImpl;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.application.UIControlConfig;
import quickfix.examples.banzai.ui.impl.ExecutionTableControllerImpl;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UIControlConfig.class, loader = AnnotationConfigContextLoader.class)
public class ExecutionTableViewTest extends ApplicationTest {
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private ExecutionTableControllerImpl executionTableController;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @After
  public void tearDown() throws Exception {
    this.executionTableController.clear();
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Parent root = SpringFXMLLoader.create().applicationContext(this.applicationContext)
            .location(getClass().getResource("ExecutionTableView.fxml")).load();
    final Scene scene = new Scene(root, 800, 400);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testClear() {
    final Execution execution = execution();

    this.executionTableController.addExecution(execution);

    verifyThat("#executionTable", hasItems(1));

    this.executionTableController.clear();
    verifyThat("#executionTable", hasItems(0));
  }

  private Execution execution() {
    final Execution execution = new ExecutionImpl();
    execution.setQuantity(100);
    execution.setExchangeID("NYSE");
    execution.setPrice(23.01);
    execution.setSymbol("MSFT");
    execution.setSide(OrderSide.BUY);
    return execution;
  }
}
