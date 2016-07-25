/*******************************************************************************
 * Copyright (c) quickfixengine.org All rights reserved.
 * <p>
 * This file is part of the QuickFIX FIX Engine
 * <p>
 * This file may be distributed under the terms of the quickfixengine.org license as defined by
 * quickfixengine.org and appearing in the file LICENSE included in the packaging of this file.
 * <p>
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE WARRANTY OF DESIGN,
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * <p>
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 * <p>
 * Contact ask@quickfixengine.org if any conditions of this licensing are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import quickfix.ApplicationAdapter;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.examples.banzai.fix.FixMessageBuilder;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.utility.MessageSender;
import quickfix.field.BeginString;
import quickfix.field.BusinessRejectReason;
import quickfix.field.DeliverToCompID;
import quickfix.field.MsgType;
import quickfix.field.SessionRejectReason;

@Component("banzaiApplication")
public class BanzaiApplication extends ApplicationAdapter {
  private static final Logger logger = LoggerFactory.getLogger(BanzaiApplication.class);

  @Autowired
  private IMarketConnectivity marketConnectivity;
  @Autowired
  private FixMessageBuilderFactory fixMessageBuilderFactory;
  @Autowired
  private MessageSender messageSender;

  private boolean isAvailable = true;
  private boolean isMissingField;

  @Override
  public void onLogon(final SessionID sessionID) {
    this.marketConnectivity.onLogon(sessionID);
  }

  @Override
  public void onLogout(final SessionID sessionID) {
    this.marketConnectivity.onLogout(sessionID);
  }

  @Override
  public void fromApp(final quickfix.Message message, final SessionID sessionID)
          throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    try {
      Platform.runLater(() -> process(message, sessionID));
    } catch (final Exception e) {
    }
  }

  private void process(final quickfix.Message message, final SessionID sessionID) {
    try {
      final MsgType msgType = new MsgType();
      if (this.isAvailable) {
        if (this.isMissingField) {
          // For OpenFIX certification testing
          sendBusinessReject(message, BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING,
                  "Conditionally required field missing");

        } else if (message.getHeader().isSetField(DeliverToCompID.FIELD)) {
          // This is here to support OpenFIX certification
          sendSessionReject(message, SessionRejectReason.COMPID_PROBLEM);

        } else if (message.getHeader().getField(msgType).valueEquals("8")) {
          this.marketConnectivity.executionReport(message, sessionID);

        } else if (message.getHeader().getField(msgType).valueEquals("9")) {
          this.marketConnectivity.cancelReject(message, sessionID);

        } else {
          sendBusinessReject(message, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                  "Unsupported Message Type");
        }
      } else {
        sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
                "Application not available");
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private void sendSessionReject(final Message message, final int rejectReason)
          throws FieldNotFound, SessionNotFound {
    final String beginString = message.getHeader().getString(BeginString.FIELD);
    final Message reply = getFixMessageBuilder(beginString).sessionReject(message, rejectReason);
    this.messageSender.sendMessage(reply, null);
    logger.error("Reject: {}", reply.toString());
  }

  private void sendBusinessReject(final Message message, final int rejectReason, final String rejectText)
          throws FieldNotFound, SessionNotFound {
    final String beginString = message.getHeader().getString(BeginString.FIELD);
    final Message reply = getFixMessageBuilder(beginString).businessReject(message, rejectReason,
            rejectText);
    this.messageSender.sendMessage(reply, null);
    logger.error("Reject: {}", reply.toString());
  }

  public boolean isMissingField() {
    return this.isMissingField;
  }

  public void setMissingField(final boolean isMissingField) {
    this.isMissingField = isMissingField;
  }

  public boolean isAvailable() {
    return this.isAvailable;
  }

  public void setAvailable(final boolean isAvailable) {
    this.isAvailable = isAvailable;
  }

  private FixMessageBuilder getFixMessageBuilder(final String beginString) {
    return this.fixMessageBuilderFactory.getFixMessageBuilder(beginString);
  }
}
