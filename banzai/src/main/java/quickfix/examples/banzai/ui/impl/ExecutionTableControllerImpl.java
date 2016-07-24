package quickfix.examples.banzai.ui.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.model.Execution;
import quickfix.examples.banzai.ui.ExecutionTableController;
import quickfix.examples.banzai.ui.ExecutionTableModel;

@Component("executionTableController")
public class ExecutionTableControllerImpl implements ExecutionTableController, Initializable {
  @FXML
  private TableView<Execution> executionTable;

  @Autowired
  private ExecutionTableModel executionTableModel;

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.executionTable.setItems(this.executionTableModel.getExecutionList());
  }

  public void addExecution(final Execution execution) {
    this.executionTableModel.addExecution(execution);
  }

  public void clear() {
    this.executionTableModel.clear();
  }

  @FXML
  public void onExecutionSelected(final Event event) {
  }
}