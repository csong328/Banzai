package quickfix.examples.exchange.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.exchange.OMS;
import quickfix.examples.exchange.model.Order;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilder;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilderFactory;
import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.MessageSender;
import quickfix.field.ClOrdID;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Component("actionableOMS")
public class ActionableOMSImpl implements ActionableOMS {
  private final Map<String, Message> requestByClOrdID = new HashMap<>();
  private final Map<String, Order> orderByClOrdID = new HashMap<>();

  @Autowired
  private IdGenerator orderIdGenerator;
  @Autowired
  private IdGenerator execIdGenerator;
  @Autowired
  private ScriptFactory scriptFactory;

  @Autowired
  private ExecutionReportBuilderFactory builderFactory;
  @Autowired
  private MessageSender messageSender;

  public void setScriptFactory(final ScriptFactory scriptFactory) {
    this.scriptFactory = scriptFactory;
  }

  @Override
  public void onNewOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String clOrdID = request.getString(ClOrdID.FIELD);
    Order order = null;
    try {
      if (this.orderByClOrdID.containsKey(clOrdID)) {
        order = this.orderByClOrdID.get(clOrdID);
        throw new IllegalArgumentException(String.format("Duplidated order %s", clOrdID));
      } else {
        order = newOrder(request, sessionID);
      }

      this.orderByClOrdID.put(clOrdID, order);
      this.requestByClOrdID.put(request.getString(ClOrdID.FIELD), request);
    } catch (final Exception ex) {
      final ExecutionReportBuilder builder = this.builderFactory.getExecutionReportBuilder(sessionID.getBeginString());

      final Message reject = builder.orderRejected(request, order.getID(), nextExecId(), ex.getMessage());
      sendMessage(reject, sessionID);
    }

    final OMS instructions = resolveInstructions(request);
    if (instructions != null) {
      instructions.onNewOrder(request, sessionID);
    }
  }

  @Override
  public void onCanceleOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String origClOrdID = request.getString(OrigClOrdID.FIELD);
    final String clOrdID = request.getString(ClOrdID.FIELD);
    final Order order = this.orderByClOrdID.get(origClOrdID);

    try {
      checkNotNull(order, "Order %s not found", order);
      checkArgument(order.getPendingMessage() == null, "Order %s in pending state", order.getID());
      checkArgument(!this.requestByClOrdID.containsKey(clOrdID), "Duplicated request %s", clOrdID);

      order.setOrdStatus(OrdStatus.PENDING_CANCEL);
      order.setPendingMessage(request);
      this.orderByClOrdID.put(request.getString(ClOrdID.FIELD), order);
      this.requestByClOrdID.put(request.getString(ClOrdID.FIELD), request);

    } catch (final Exception ex) {
      final ExecutionReportBuilder builder = this.builderFactory.getExecutionReportBuilder(sessionID.getBeginString());
      final Message cancelReject;
      if (order != null) {
        cancelReject = builder.cancelRejected(request, nextExecId(), order.getOrdStatus(), order.getExecuted(), order.getAvgPx(), 2);
      } else {
        cancelReject = builder.cancelRejected(request, nextExecId(), OrdStatus.PENDING_CANCEL, 0.0, 0.0, 2);
      }
      sendMessage(cancelReject, sessionID);
    }

    final OMS instructions = resolveInstructions(request);
    if (instructions != null) {
      instructions.onCanceleOrder(request, sessionID);
    }
  }

  @Override
  public void onReplaceOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String origClOrdID = request.getString(OrigClOrdID.FIELD);
    final String clOrdID = request.getString(ClOrdID.FIELD);
    final Order order = this.orderByClOrdID.get(origClOrdID);

    try {
      checkNotNull(order, "Order %s not found", order);
      checkArgument(order.getPendingMessage() == null, "Order %s in pending state", order.getID());
      checkArgument(!this.requestByClOrdID.containsKey(clOrdID), "Duplicated request %s", clOrdID);

      order.setOrdStatus(OrdStatus.PENDING_REPLACE);
      order.setPendingMessage(request);
      this.orderByClOrdID.put(request.getString(ClOrdID.FIELD), order);
      this.requestByClOrdID.put(request.getString(ClOrdID.FIELD), request);

    } catch (final Exception ex) {
      final ExecutionReportBuilder builder = this.builderFactory.getExecutionReportBuilder(sessionID.getBeginString());
      final Message cancelReject;
      if (order != null) {
        cancelReject = builder.cancelRejected(request, nextExecId(), order.getOrdStatus(), order.getExecuted(), order.getAvgPx(), 2);
      } else {
        cancelReject = builder.cancelRejected(request, nextExecId(), OrdStatus.PENDING_REPLACE, 0.0, 0.0, 2);
      }
      sendMessage(cancelReject, sessionID);
    }

    final OMS instructions = resolveInstructions(request);
    if (instructions != null) {
      instructions.onReplaceOrder(request, sessionID);
    }
  }

  private Order newOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final Order order = new Order();
    order.setID(nextOrderId());
    order.setSessionID(sessionID);
    order.setQuantity(request.getDouble(OrderQty.FIELD));
    order.setAvgPx(0.0);
    order.setExecuted(0.0);
    order.setOpen(order.getQuantity());
    order.setOrdStatus(OrdStatus.PENDING_NEW);
    order.setPendingMessage(request);
    return order;
  }

  @Override
  public void pendingAck(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    final Message execution = builder.pendingAck(order.getPendingMessage(), order.getID(), nextExecId());

    sendMessage(execution, sessionID);
  }

  @Override
  public void ack(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    order.setOrdStatus(OrdStatus.NEW);
    order.setProcessedMessage(order.getPendingMessage());
    order.setPendingMessage(null);

    final Message execution = builder.orderAcked(order.getProcessedMessage(), order.getID(), nextExecId());

    sendMessage(execution, sessionID);
  }

  @Override
  public void fill(final String clOrdID, final double lastShares, final double lastPx) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    double execAmt = order.getExecuted() * order.getAvgPx();
    execAmt += lastShares * lastPx;
    final double executed = order.getExecuted() + lastShares;
    final double avgPx = execAmt / executed;
    order.setExecuted(executed);
    order.setAvgPx(avgPx);
    order.setOpen(order.getQuantity() - order.getExecuted());
    order.setOrdStatus(getOrderExecStatus(order));
    order.setOrdStatus(OrdStatus.NEW);

    final Message execution = builder.fillOrder(order.getProcessedMessage(), order.getID(), nextExecId(),
            order.getOrdStatus(), order.getExecuted(), order.getAvgPx(), lastShares, lastPx);

    sendMessage(execution, sessionID);
  }

  @Override
  public void reject(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    order.setOpen(0.0);
    order.setOrdStatus(OrdStatus.REJECTED);
    order.setProcessedMessage(order.getPendingMessage());
    order.setPendingMessage(null);

    final Message execution = builder.orderRejected(order.getProcessedMessage(), order.getID(), nextExecId(), "");

    sendMessage(execution, sessionID);
  }

  @Override
  public void pendingCancel(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    final Message execution = builder.pendingCancel(order.getPendingMessage(), order.getID(), nextExecId(),
            order.getQuantity(), order.getExecuted(), order.getAvgPx());

    sendMessage(execution, sessionID);
  }

  @Override
  public void canceled(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    order.setOpen(0.0);
    order.setOrdStatus(OrdStatus.CANCELED);
    order.setProcessedMessage(order.getPendingMessage());
    order.setPendingMessage(null);

    final Message execution = builder.orderCanceled(order.getProcessedMessage(), order.getID(), nextExecId(),
            order.getExecuted(), order.getAvgPx());

    sendMessage(execution, sessionID);
  }

  @Override
  public void cancelReject(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    order.setOrdStatus(getOrderExecStatus(order));
    final Message pendingMsg = order.getPendingMessage();
    order.setPendingMessage(null);

    final Message execution = builder.cancelRejected(pendingMsg, order.getID(), order.getOrdStatus(),
            order.getExecuted(), order.getAvgPx(), 2);

    sendMessage(execution, sessionID);
  }

  private char getOrderExecStatus(final Order order) {
    if (order.getExecuted() == 0.0) {
      return OrdStatus.NEW;
    } else if (order.getOpen() == 0.0) {
      return OrdStatus.FILLED;
    } else {
      return OrdStatus.PARTIALLY_FILLED;
    }
  }

  @Override
  public void pendingReplace(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    final Message execution = builder.pendingReplace(order.getPendingMessage(), order.getID(), nextExecId(),
            order.getQuantity(), order.getExecuted(), order.getAvgPx());

    sendMessage(execution, sessionID);
  }

  @Override
  public void replaced(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    final Message pendingMsg = order.getPendingMessage();
    order.setQuantity(pendingMsg.getDouble(OrderQty.FIELD));
    order.setOpen(order.getQuantity() - order.getExecuted());
    order.setOrdStatus(getOrderExecStatus(order));
    order.setProcessedMessage(order.getPendingMessage());
    order.setPendingMessage(null);

    final Message execution = builder.orderReplaced(order.getProcessedMessage(), order.getID(), nextExecId(),
            order.getExecuted(), order.getAvgPx());

    sendMessage(execution, sessionID);
  }

  @Override
  public void replaceReject(final String clOrdID) throws FieldNotFound {
    final Order order = this.orderByClOrdID.get(clOrdID);
    checkNotNull(order, "Order %s not found", clOrdID);

    final SessionID sessionID = order.getSessionID();
    checkNotNull(sessionID, "Order %s sessionID not found", clOrdID);

    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(order.getSessionID().getBeginString());
    checkNotNull(String.format("%s not supported", order.getSessionID().getBeginString()), builder);

    order.setOrdStatus(getOrderExecStatus(order));
    final Message pendingMsg = order.getPendingMessage();
    order.setPendingMessage(null);

    final Message execution = builder.cancelRejected(pendingMsg, order.getID(), order.getOrdStatus(),
            order.getExecuted(), order.getAvgPx(), 2);

    sendMessage(execution, sessionID);
  }

  @Override
  public void onCancelReject(final Message message, final SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void onExecutionReport(final Message message, final SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  private String nextOrderId() {
    return this.orderIdGenerator.nextID();
  }

  private String nextExecId() {
    return this.execIdGenerator.nextID();
  }

  private OMS resolveInstructions(final Message message) {
    return this.scriptFactory.getHandlingInstructions(message, this);
  }

  private void sendMessage(final Message reject, final SessionID sessionID) {
    this.messageSender.sendMessage(reject, sessionID);
  }
}
