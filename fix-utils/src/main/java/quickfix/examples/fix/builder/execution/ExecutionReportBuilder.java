package quickfix.examples.fix.builder.execution;

import quickfix.FieldNotFound;
import quickfix.Message;

public interface ExecutionReportBuilder {
	Message pendingAck(Message newOrderSingle, String orderID, String execID)
			throws FieldNotFound;

	Message orderAcked(Message newOrderSingle, String orderID, String execID)
			throws FieldNotFound;

	Message orderRejected(Message newOrderSingle, String orderID,
                          String execID, String text) throws FieldNotFound;

	Message fillOrder(Message newOrderSingle, String orderID, String execID,
                      char ordStatus, double cumQty, double avgPx, double lastShares,
                      double lastPx) throws FieldNotFound;

	Message pendingCancel(Message cancelRequest, String orderID, String execID,
                          double orderQty, double cumQty, double avgPx) throws FieldNotFound;

	Message orderCanceled(Message cancelRequest, String orderID, String execID,
                          double cumQty, double avgPx) throws FieldNotFound;

	Message pendingReplace(Message replaceRequest, String orderID,
                           String execID, double orderQty, double cumQty, double avgPx)
			throws FieldNotFound;

	Message orderReplaced(Message replaceRequest, String orderID,
                          String execID, double cumQty, double avgPx) throws FieldNotFound;

	Message cancelRejected(Message order, String orderID, char ordStatus,
                           double cumQty, double avgPx, int cxlRejReason) throws FieldNotFound;

	Message cancelRejectedForUnknownOrder(Message order) throws FieldNotFound;
}
