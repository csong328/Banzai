package quickfix.examples.banzai.ui;

import javafx.event.Event;
import quickfix.examples.banzai.Execution;

public interface ExecutionTableController<T extends Execution> {

  void addExecution(T execution);

  void clear();

  void onExecutionSelected(Event event);
}