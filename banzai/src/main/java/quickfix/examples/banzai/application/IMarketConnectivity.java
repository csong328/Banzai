package quickfix.examples.banzai.application;

import java.util.Observer;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.ui.event.OrderEventSource;

public interface IMarketConnectivity extends OrderEventSource {
  void onLogon(final SessionID sessionID);

  void onLogout(final SessionID sessionID);

  void addLogonObserver(final Observer observer);

  void deleteLogonObserver(final Observer observer);

  void send(Order order);

  void cancel(Order order);

  void replace(Order order, Order newOrder);

  void executionReport(final Message message, final SessionID sessionID) throws FieldNotFound;

  void cancelReject(final Message message, final SessionID sessionID) throws FieldNotFound;
}
