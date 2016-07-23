package quickfix.examples.banzai.ui;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.Execution;

import static javafx.collections.FXCollections.observableArrayList;

@Component("executionTableModel")
public class ExecutionTableModelImpl implements ExecutionTableModel {
  private final ObservableList<Execution> executionList;
  private final Map<String, Execution> exchangeIdToExecution = new HashMap<>();

  public ExecutionTableModelImpl() {
    this.executionList = observableArrayList();
  }

  @Override
  public ObservableList<Execution> getExecutionList() {
    return this.executionList;
  }

  @Override
  public void addExecution(Execution execution) {
    if (exchangeIdToExecution.containsKey(execution.getExchangeID())) {
      return;
    }
    exchangeIdToExecution.put(execution.getExchangeID(), execution);
    this.executionList.add(execution);
  }

  @Override
  public Execution getExchangeExecution(String exchangeID) {
    return this.exchangeIdToExecution.get(exchangeID);
  }

  @Override
  public void clear() {
    this.executionList.clear();
  }

}
