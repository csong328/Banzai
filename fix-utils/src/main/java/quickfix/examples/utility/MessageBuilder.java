package quickfix.examples.utility;

import java.math.BigDecimal;
import java.util.Date;

import quickfix.BooleanField;
import quickfix.BytesField;
import quickfix.CharField;
import quickfix.DecimalField;
import quickfix.DefaultMessageFactory;
import quickfix.DoubleField;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.Group;
import quickfix.IntField;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.StringField;
import quickfix.UtcDateOnlyField;
import quickfix.UtcTimeOnlyField;
import quickfix.UtcTimeStampField;

public class MessageBuilder<T extends FieldMap> {
  private final T target;

  public static MessageBuilder<Message> newBuilder(String beginString, String msgType) {
    return newBuilder(new DefaultMessageFactory(), beginString, msgType);
  }

  public static MessageBuilder<Message> newBuilder(MessageFactory messageFactory, String beginString, String msgType) {
    return newBuilder(messageFactory.create(beginString, msgType));
  }

  public static <T extends FieldMap> MessageBuilder<T> newBuilder(T message) {
    return new MessageBuilder<>(message);
  }

  private MessageBuilder(T target) {
    this.target = target;
  }

  public MessageBuilder<T> clear() {
    this.target.clear();
    return this;
  }

  public MessageBuilder<T> setFields(FieldMap fieldMap) {
    this.target.setFields(fieldMap);
    return this;
  }

  public MessageBuilder<T> setGroups(FieldMap fieldMap) {
    this.target.setGroups(fieldMap);
    return this;
  }

  public MessageBuilder<T> setString(int field, String value) {
    this.target.setString(field, value);
    return this;
  }

  public MessageBuilder<T> setBytes(int field, byte[] value) {
    this.target.setBytes(field, value);
    return this;
  }

  public MessageBuilder<T> setBoolean(int field, boolean value) {
    this.target.setBoolean(field, value);
    return this;
  }

  public MessageBuilder<T> setChar(int field, char value) {
    this.target.setChar(field, value);
    return this;
  }

  public MessageBuilder<T> setInt(int field, int value) {
    this.target.setInt(field, value);
    return this;
  }

  public MessageBuilder<T> setDouble(int field, double value) {
    this.target.setDouble(field, value);
    return this;
  }

  public MessageBuilder<T> setDouble(int field, double value, int padding) {
    this.target.setDouble(field, value, padding);
    return this;
  }

  public MessageBuilder<T> setDecimal(int field, BigDecimal value) {
    this.target.setDecimal(field, value);
    return this;
  }

  public MessageBuilder<T> setDecimal(int field, BigDecimal value, int padding) {
    this.target.setDecimal(field, value, padding);
    return this;
  }

  public MessageBuilder<T> setUtcTimeStamp(int field, Date value) {
    this.target.setUtcTimeStamp(field, value);
    return this;
  }

  public MessageBuilder<T> setUtcTimeStamp(int field, Date value, boolean includeMilliseconds) {
    this.target.setUtcTimeStamp(field, value, includeMilliseconds);
    return this;
  }

  public MessageBuilder<T> setUtcTimeOnly(int field, Date value) {
    this.target.setUtcTimeOnly(field, value);
    return this;
  }

  public MessageBuilder<T> setUtcTimeOnly(int field, Date value, boolean includeMillseconds) {
    this.target.setUtcTimeOnly(field, value, includeMillseconds);
    return this;
  }

  public MessageBuilder<T> setUtcDateOnly(int field, Date value) {
    this.target.setUtcDateOnly(field, value);
    return this;
  }

  public MessageBuilder<T> setField(int key, Field<?> field) {
    this.target.setField(key, field);
    return this;
  }

  public MessageBuilder<T> setField(StringField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(BooleanField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(CharField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(IntField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(DoubleField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(DecimalField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(UtcTimeStampField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(UtcTimeOnlyField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(UtcDateOnlyField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> setField(BytesField field) {
    this.target.setField(field);
    return this;
  }

  public MessageBuilder<T> removeField(int field) {
    this.target.removeField(field);
    return this;
  }

  public MessageBuilder<T> addGroup(Group group) {
    this.target.addGroup(group);
    return this;
  }

  public MessageBuilder<T> addGroupRef(Group group) {
    this.target.addGroupRef(group);
    return this;
  }

  public MessageBuilder<T> replaceGroup(int num, Group group) {
    this.target.replaceGroup(num, group);
    return this;
  }

  public MessageBuilder<T> removeGroup(int field) {
    this.target.removeGroup(field);
    return this;
  }

  public MessageBuilder<T> removeGroup(int num, int field) {
    this.target.removeGroup(num, field);
    return this;
  }

  public MessageBuilder<T> removeGroup(int num, Group group) {
    this.target.removeGroup(num, group);
    return this;
  }

  public MessageBuilder<T> removeGroup(Group group) {
    this.target.removeGroup(group);
    return this;
  }

  public T build() {
    return this.target;
  }
}
