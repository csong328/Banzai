package quickfix.examples.fix.builder.execution;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.examples.utility.MessageBuilder;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Text;

public class FIX43ExecutionReportBuilder extends AbstractExecutioReportBuilder {

  public Message orderAcked(Message message, String orderID, String execID)
          throws FieldNotFound {
    double orderQty = message.getDouble(OrderQty.FIELD);
    return MessageBuilder.newBuilder(createExecutionReport(message, orderID, execID))
            .setField(new ExecType(ExecType.NEW))
            .setField(new OrdStatus(OrdStatus.NEW))
            .setField(new LastQty(0))
            .setField(new LastPx(0))
            .setField(new LeavesQty(orderQty))
            .setField(new CumQty(0))
            .setField(new AvgPx(0))
            .build();
  }

  public Message orderRejected(Message message, String orderID,
                               String execID, String text) throws FieldNotFound {
    return MessageBuilder.newBuilder(createExecutionReport(message, orderID, execID))
            .setField(new ExecType(ExecType.REJECTED))
            .setField(new OrdStatus(OrdStatus.REJECTED))
            .setField(new LastQty(0))
            .setField(new LastPx(0))
            .setField(new LeavesQty(0))
            .setField(new CumQty(0))
            .setField(new AvgPx(0))
            .setField(new Text(text))
            .build();
  }

  public Message fillOrder(Message message, String orderID, String execID,
                           char ordStatus, double cumQty, double avgPx, double lastShares,
                           double lastPx) throws FieldNotFound {
    char execType = getFillType(message, cumQty);
    double orderQty = message.getDouble(OrderQty.FIELD);
    return MessageBuilder.newBuilder(createExecutionReport(message, orderID, execID))
            .setField(new ExecType(execType))
            .setField(new OrdStatus(ordStatus))
            .setField(new LastQty(lastShares))
            .setField(new LastPx(lastPx))
            .setField(new LeavesQty(orderQty - cumQty))
            .setField(new CumQty(cumQty))
            .setField(new AvgPx(avgPx))
            .build();
  }

  public Message orderCanceled(Message message, String orderID,
                               String execID, double cumQty, double avgPx) throws FieldNotFound {
    return MessageBuilder.newBuilder(createExecutionReport(message, orderID, execID))
            .setField(new ExecType(ExecType.CANCELED))
            .setField(new OrdStatus(OrdStatus.CANCELED))
            .setField(new LastQty(0))
            .setField(new LastPx(0))
            .setField(new LeavesQty(0))
            .setField(new CumQty(cumQty))
            .setField(new AvgPx(avgPx))
            .build();
  }

  @Override
  public Message orderReplaced(Message message, String orderID,
                               String execID, double cumQty, double avgPx) throws FieldNotFound {
    double orderQty = message.getDouble(OrderQty.FIELD);
    return MessageBuilder.newBuilder(createExecutionReport(message, orderID, execID))
            .setField(new ExecType(ExecType.REPLACE))
            .setField(new OrdStatus(OrdStatus.REPLACED))
            .setField(new LastQty(0))
            .setField(new LastPx(0))
            .setField(new LeavesQty(orderQty - cumQty))
            .setField(new CumQty(cumQty))
            .setField(new AvgPx(avgPx))
            .build();
  }
}
