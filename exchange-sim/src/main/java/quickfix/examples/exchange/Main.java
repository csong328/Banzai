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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import quickfix.ConfigError;
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
import quickfix.field.OrdType;
import quickfix.mina.acceptor.DynamicAcceptorSessionProvider;
import quickfix.mina.acceptor.DynamicAcceptorSessionProvider.TemplateMapping;

import static com.google.common.base.Preconditions.checkArgument;
import static quickfix.Acceptor.SETTING_ACCEPTOR_TEMPLATE;
import static quickfix.Acceptor.SETTING_SOCKET_ACCEPT_ADDRESS;
import static quickfix.Acceptor.SETTING_SOCKET_ACCEPT_PORT;

public class Main {
  private final static Logger log = LoggerFactory.getLogger(Main.class);

  private static final String DEFAULT_MARKET_PRICE_KEY = "DefaultMarketPrice";
  private static final String ALWAYS_FILL_LIMIT_KEY = "AlwaysFillLimitOrders";
  private static final String VALID_ORDER_TYPES_KEY = "ValidOrderTypes";

  private final SocketAcceptor acceptor;
  private final Map<InetSocketAddress, List<TemplateMapping>> dynamicSessionMappings = new HashMap<>();

  private final JmxExporter jmxExporter;
  private final ObjectName connectorObjectName;

  private final AnnotationConfigApplicationContext applicationContext;

  public Main(final SessionSettings settings) throws ConfigError, FieldConvertError, JMException {
    this.applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

    final ExchangeOMSImpl exchangeOMS = this.applicationContext.getBean(ExchangeOMSImpl.class);

    if (settings.isSetting(ALWAYS_FILL_LIMIT_KEY)) {
      exchangeOMS.setAlwaysFillLimitOrders(settings
              .getBool(ALWAYS_FILL_LIMIT_KEY));
    } else {
      exchangeOMS.setAlwaysFillLimitOrders(false);
    }

    String validOrderTypesStr = null;
    if (settings.isSetting(VALID_ORDER_TYPES_KEY)) {
      validOrderTypesStr = settings.getString(VALID_ORDER_TYPES_KEY)
              .trim();
    }
    exchangeOMS.setValidOrderTypes(getValidOrderTypes(validOrderTypesStr));

    double defaultMarketPrice = 0.0;
    if (settings.isSetting(DEFAULT_MARKET_PRICE_KEY)) {
      defaultMarketPrice = settings.getDouble(DEFAULT_MARKET_PRICE_KEY);
    }
    exchangeOMS.setMarketDataProvider(getMarketDataProvider(defaultMarketPrice));

    final ExchangeApplication application = this.applicationContext.getBean(ExchangeApplication.class);
    application.setValidOrderTypes(getValidOrderTypes(validOrderTypesStr));

    final MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
    final LogFactory logFactory = new ScreenLogFactory(true, true, true);
    final MessageFactory messageFactory = this.applicationContext.getBean(MessageFactory.class);

    this.acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory,
            messageFactory);

    configureDynamicSessions(settings, application, messageStoreFactory, logFactory,
            messageFactory);

    this.jmxExporter = new JmxExporter();
    this.connectorObjectName = this.jmxExporter.register(this.acceptor);
    log.info("Acceptor registered with JMX, name=" + this.connectorObjectName);
  }

  private MarketDataProvider getMarketDataProvider(final double defaultMarketPrice)
          throws ConfigError, FieldConvertError {
    checkArgument(defaultMarketPrice > 0.0, "Invalid defaultMarketPrice %s", defaultMarketPrice);

    return new MarketDataProvider() {
      public double getAsk(final String symbol) {
        return defaultMarketPrice;
      }

      public double getBid(final String symbol) {
        return defaultMarketPrice;
      }
    };
  }

  private Set<String> getValidOrderTypes(final String validOrderTypesStr)
          throws ConfigError, FieldConvertError {
    if (validOrderTypesStr != null) {
      final List<String> orderTypes = Arrays.asList(validOrderTypesStr
              .split("\\s*,\\s*"));
      return new HashSet<>(orderTypes);
    } else {
      return Collections.singleton(Character.toString(OrdType.LIMIT));
    }
  }

  private void configureDynamicSessions(final SessionSettings settings, final ExchangeApplication application,
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
