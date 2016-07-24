package quickfix.examples.banzai.ui;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.Execution;

public interface ExecutionTableModel<T extends Execution> {

  ObservableList<T> getExecutionList();

  void addExecution(T execution);

  T getExchangeExecution(String exchangeID);

  void clear();
}
