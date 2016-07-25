package quickfix.examples.exchange.simulator;

import quickfix.Message;
import quickfix.examples.exchange.OMS;

public class SimpleScriptFactory implements ScriptFactory {
  private final OMS script;

  public SimpleScriptFactory(final OMS script) {
    this.script = script;
  }

  @Override
  public OMS getHandlingInstructions(final Message message, final ActionableOMS actionableOMS) {
    return this.script;
  }
}
