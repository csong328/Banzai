package quickfix.examples.banzai.ui;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.Execution;

@Component("executionTableController")
public class ExecutionTableController implements Initializable {
  @FXML
  private TableView<Execution> executionTable;

  @Autowired
  private ExecutionTableModel executionTableModel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.executionTable.setItems(executionTableModel.getExecutionList());
  }

  public void onExecutionSelected(Event event) {}
}
