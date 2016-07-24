package quickfix.examples.banzai.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import quickfix.examples.utility.IdGenerator;

@Component("executionFactory")
public class ExecutionFactoryImpl implements ExecutionFactory {
  @Autowired
  @Qualifier("execIdGenerator")
  private IdGenerator execIdGenerator;

  @Override
  public Execution newExecution() {
    final Execution execution = new ExecutionImpl();
    execution.setID(this.execIdGenerator.nextID());
    return execution;
  }

}
