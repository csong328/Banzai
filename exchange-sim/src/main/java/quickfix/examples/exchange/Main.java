/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved. 
 *
 * This file is part of the QuickFIX FIX Engine 
 *
 * This file may be distributed under the terms of the quickfixengine.org 
 * license as defined by quickfixengine.org and appearing in the file 
 * LICENSE included in the packaging of this file. 
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING 
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information. 
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing 
 * are not clear to you.
 ******************************************************************************/

package quickfix.examples.exchange;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.ObjectName;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FieldConvertError;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RuntimeError;
import quickfix.ScreenLogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.mina.acceptor.DynamicAcceptorSessionProvider;
import quickfix.mina.acceptor.DynamicAcceptorSessionProvider.TemplateMapping;

import static quickfix.Acceptor.SETTING_ACCEPTOR_TEMPLATE;
import static quickfix.Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS;
import static quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT;

public class Main {
  private final static Logger log = LoggerFactory.getLogger(Main.class);
  private final SocketAcceptor acceptor;
  private final Map<InetSocketAddress, List<TemplateMapping>> dynamicSessionMappings = new HashMap<>();

  private final JmxExporter jmxExporter;
  private final ObjectName connectorObjectName;

  public Main(final SessionSettings settings) throws ConfigError, FieldConvertError, JMException {
    final Application application = new Application(settings);
    final MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
    final LogFactory logFactory = new ScreenLogFactory(true, true, true);
    final MessageFactory messageFactory = new DefaultMessageFactory();

    this.acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory,
            messageFactory);

    configureDynamicSessions(settings, application, messageStoreFactory, logFactory,
            messageFactory);

    this.jmxExporter = new JmxExporter();
    this.connectorObjectName = this.jmxExporter.register(this.acceptor);
    log.info("Acceptor registered with JMX, name=" + this.connectorObjectName);
  }

  private void configureDynamicSessions(final SessionSettings settings, final Application application,
                                        final MessageStoreFactory messageStoreFactory, final LogFactory logFactory,
                                        final MessageFactory messageFactory) throws ConfigError, FieldConvertError {
    //
    // If a session template is detected in the settings, then
    // set up a dynamic session provider.
    //

    final Iterator<SessionID> sectionIterator = settings.sectionIterator();
    while (sectionIterator.hasNext()) {
      final SessionID sessionID = sectionIterator.next();
      if (isSessionTemplate(settings, sessionID)) {
        final InetSocketAddress address = getAcceptorSocketAddress(settings, sessionID);
        getMappings(address).add(new TemplateMapping(sessionID, sessionID));
      }
    }

    for (final Map.Entry<InetSocketAddress, List<TemplateMapping>> entry : this.dynamicSessionMappings
            .entrySet()) {
      this.acceptor.setSessionProvider(entry.getKey(), new DynamicAcceptorSessionProvider(
              settings, entry.getValue(), application, messageStoreFactory, logFactory,
              messageFactory));
    }
  }

  private List<TemplateMapping> getMappings(final InetSocketAddress address) {
    List<TemplateMapping> mappings = this.dynamicSessionMappings.get(address);
    if (mappings == null) {
      mappings = new ArrayList<>();
      this.dynamicSessionMappings.put(address, mappings);
    }
    return mappings;
  }

  private InetSocketAddress getAcceptorSocketAddress(final SessionSettings settings, final SessionID sessionID)
          throws ConfigError, FieldConvertError {
    String acceptorHost = "0.0.0.0";
    if (settings.isSetting(sessionID, SETTING_SOCKET_ACCEPT_ADDRESS)) {
      acceptorHost = settings.getString(sessionID, SETTING_SOCKET_ACCEPT_ADDRESS);
    }
    final int acceptorPort = (int) settings.getLong(sessionID, SETTING_SOCKET_ACCEPT_PORT);

    final InetSocketAddress address = new InetSocketAddress(acceptorHost, acceptorPort);
    return address;
  }

  private boolean isSessionTemplate(final SessionSettings settings, final SessionID sessionID)
          throws ConfigError, FieldConvertError {
    return settings.isSetting(sessionID, SETTING_ACCEPTOR_TEMPLATE)
            && settings.getBool(sessionID, SETTING_ACCEPTOR_TEMPLATE);
  }

  private void start() throws RuntimeError, ConfigError {
    this.acceptor.start();
  }

  private void stop() {
    try {
      this.jmxExporter.getMBeanServer().unregisterMBean(this.connectorObjectName);
    } catch (final Exception e) {
      log.error("Failed to unregister acceptor from JMX", e);
    }
    this.acceptor.stop();
  }

  public static void main(final String[] args) throws Exception {
    try {
      final InputStream inputStream = getSettingsInputStream(args);
      final SessionSettings settings = new SessionSettings(inputStream);
      inputStream.close();

      final Main executor = new Main(settings);
      executor.start();

      System.out.println("press <enter> to quit");
      System.in.read();

      executor.stop();
    } catch (final Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static InputStream getSettingsInputStream(final String[] args) throws FileNotFoundException {
    InputStream inputStream = null;
    if (args.length == 0) {
      inputStream = Main.class.getResourceAsStream("executor.cfg");
    } else if (args.length == 1) {
      inputStream = new FileInputStream(args[0]);
    }
    if (inputStream == null) {
      System.out.println("usage: " + Main.class.getName() + " [configFile].");
      System.exit(1);
    }
    return inputStream;
  }
}
