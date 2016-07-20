package quickfix.examples.banzai.ui;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.Execution;

public interface ExecutionTableModel {

    ObservableList<Execution> getExecutionList();

    void addExecution(Execution execution);

    Execution getExchangeExecution(String exchangeID);

}
