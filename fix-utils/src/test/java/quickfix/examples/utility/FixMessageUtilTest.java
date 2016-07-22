package quickfix.examples.utility;

import org.junit.Before;
import org.junit.Test;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.CumQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static quickfix.examples.utility.FixMessageUtil.parse;

public class FixMessageUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws InvalidMessage, FieldNotFound {
        String dMsg = "8=FIX.4.2|35=D|11=002|18=1|21=2|38=2000|40=1|54=2|55=CSCO";
        Message message = parse(dMsg, '|');
        assertThat(message.getHeader().getString(MsgType.FIELD), is("D"));
        assertThat(message.getInt(OrderQty.FIELD), is(2000));

        String ackMsg = "8=FIX.4.2|35=8|17=1|11=002|14=0|6=0|20=0|37=002|38=2000|39=0|54=2|55=CSCO|150=0|151=2000";
        message = parse(ackMsg, '|');
        assertThat(message.getHeader().getString(MsgType.FIELD), is("8"));
        assertThat(message.getInt(CumQty.FIELD), is(0));
    }

}
