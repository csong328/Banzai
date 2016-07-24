package quickfix.examples.banzai.ui.impl;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.ExecutionImpl;
import quickfix.examples.banzai.ui.ExecutionTableModel;

import static javafx.collections.FXCollections.observableArrayList;

@Component("executionTableModel")
public class ExecutionTableModelImpl implements ExecutionTableModel<ExecutionImpl> {
  private final ObservableList<ExecutionImpl> executionList;
  private final Map<String, ExecutionImpl> exchangeIdToExecution = new HashMap<>();

  public ExecutionTableModelImpl() {
    this.executionList = observableArrayList();
  }

  @Override
  public ObservableList<ExecutionImpl> getExecutionList() {
    return this.executionList;
  }

  @Override
  public void addExecution(final ExecutionImpl execution) {
    if (this.exchangeIdToExecution.containsKey(execution.getExchangeID())) {
      return;
    }
    this.exchangeIdToExecution.put(execution.getExchangeID(), execution);
    this.executionList.add(execution);
  }

  @Override
  public ExecutionImpl getExchangeExecution(final String exchangeID) {
    return this.exchangeIdToExecution.get(exchangeID);
  }

  @Override
  public void clear() {
    this.executionList.clear();
  }

}