package quickfix.examples.exchange.simulator;

import quickfix.Message;
import quickfix.examples.exchange.OMS;

public interface ScriptFactory {
  OMS getHandlingInstructions(Message message, ActionableOMS actionableOMS);
}
