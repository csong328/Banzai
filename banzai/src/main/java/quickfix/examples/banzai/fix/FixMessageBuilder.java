package quickfix.examples.banzai.fix;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionNotFound;
import quickfix.examples.banzai.Order;

public interface FixMessageBuilder {
	Message newOrder(Order order);

	Message replace(Order order, Order newOrder);

	Message cancel(Order order);

	Message businessReject(Message message, int rejectReason, String rejectText)
			throws FieldNotFound;

	Message sessionReject(Message message, int rejectReason)
			throws FieldNotFound, SessionNotFound;
}
