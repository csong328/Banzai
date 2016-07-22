package quickfix.examples.fix.builder.execution;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecTransType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.OrdStatus;
import quickfix.field.Text;

public class FIX40ExecutionReportBuilder extends AbstractExecutioReportBuilder {

	public Message orderAcked(Message message, String orderID, String execID)
			throws FieldNotFound {
		Message exec = createExecutionReport(message, orderID, execID);

		exec.setField(new ExecTransType(ExecTransType.NEW));
		exec.setField(new OrdStatus(OrdStatus.NEW));

		exec.setField(new LastShares(0));
		exec.setField(new LastPx(0));
		
		exec.setField(new CumQty(0));
		exec.setField(new AvgPx(0));

		return exec;
	}

	public Message orderRejected(Message message, String orderID,
                                 String execID, String text) throws FieldNotFound {
		Message exec = createExecutionReport(message, orderID, execID);
		
		exec.setField(new ExecTransType(ExecTransType.NEW));
		exec.setField(new OrdStatus(OrdStatus.REJECTED));

		exec.setField(new LastShares(0));
		exec.setField(new LastPx(0));
		
		exec.setField(new CumQty(0));
		exec.setField(new AvgPx(0));
		
		exec.setField(new Text(text));
		return exec;
	}

	public Message fillOrder(Message message, String orderID, String execID,
                             char ordStatus, double cumQty, double avgPx, double lastShares,
                             double lastPx) throws FieldNotFound {
		Message exec = createExecutionReport(message, orderID, execID);

		exec.setField(new ExecTransType(ExecTransType.NEW));
		exec.setField(new OrdStatus(ordStatus));

		exec.setField(new LastShares(lastShares));
		exec.setField(new LastPx(lastPx));
		
		exec.setField(new CumQty(cumQty));
		exec.setField(new AvgPx(avgPx));

		return exec;
	}

	public Message orderCanceled(Message message, String orderID,
                                 String execID, double cumQty, double avgPx) throws FieldNotFound {
		Message exec = createExecutionReport(message, orderID, execID);
		
		exec.setField(new ExecTransType(ExecTransType.NEW));
		exec.setField(new OrdStatus(OrdStatus.CANCELED));

		exec.setField(new LastShares(0));
		exec.setField(new LastPx(0));
		
		exec.setField(new CumQty(cumQty));
		exec.setField(new AvgPx(avgPx));
		return exec;
	}

	@Override
	public Message orderReplaced(Message message, String orderID,
                                 String execID, double cumQty, double avgPx) throws FieldNotFound {
		Message exec = createExecutionReport(message, orderID, execID);
		
		exec.setField(new ExecTransType(ExecTransType.NEW));
		exec.setField(new OrdStatus(OrdStatus.REPLACED));

		exec.setField(new LastShares(0));
		exec.setField(new LastPx(0));
		
		exec.setField(new CumQty(cumQty));
		exec.setField(new AvgPx(avgPx));
		return exec;
	}
}
