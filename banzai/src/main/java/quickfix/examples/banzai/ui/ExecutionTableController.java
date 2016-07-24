package quickfix.examples.banzai.ui;

import javafx.event.Event;
import quickfix.examples.banzai.Execution;

public interface ExecutionTableController {

  void addExecution(Execution execution);

  void clear();

  void onExecutionSelected(Event event);
}