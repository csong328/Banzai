package quickfix.examples.banzai;

import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

public class TypeMapping {
	static private TwoWayMap<OrderSide, Side> sideMap = new TwoWayMap<OrderSide, Side>();
	static private TwoWayMap<OrderType, OrdType> typeMap = new TwoWayMap<OrderType, OrdType>();
	static private TwoWayMap<OrderTIF, TimeInForce> tifMap = new TwoWayMap<OrderTIF, TimeInForce>();

	public static Side sideToFIXSide(OrderSide side) {
		return sideMap.getFirst(side);
	}

	public static OrderSide FIXSideToSide(Side side) {
		return sideMap.getSecond(side);
	}

	public static OrdType typeToFIXType(OrderType type) {
		return typeMap.getFirst(type);
	}

	public static OrderType FIXTypeToType(OrdType type) {
		return typeMap.getSecond(type);
	}

	public static TimeInForce tifToFIXTif(OrderTIF tif) {
		return tifMap.getFirst(tif);
	}

	public static OrderTIF FIXTifToTif(TimeInForce tif) {
		return tifMap.getSecond(tif);
	}

	static {
		sideMap.put(OrderSide.BUY, new Side(Side.BUY));
		sideMap.put(OrderSide.SELL, new Side(Side.SELL));
		sideMap.put(OrderSide.SHORT_SELL, new Side(Side.SELL_SHORT));
		sideMap.put(OrderSide.SHORT_SELL_EXEMPT, new Side(
				Side.SELL_SHORT_EXEMPT));
		sideMap.put(OrderSide.CROSS, new Side(Side.CROSS));
		sideMap.put(OrderSide.CROSS_SHORT, new Side(Side.CROSS_SHORT));

		typeMap.put(OrderType.MARKET, new OrdType(OrdType.MARKET));
		typeMap.put(OrderType.LIMIT, new OrdType(OrdType.LIMIT));
		typeMap.put(OrderType.STOP, new OrdType(OrdType.STOP));
		typeMap.put(OrderType.STOP_LIMIT, new OrdType(OrdType.STOP_LIMIT));

		tifMap.put(OrderTIF.DAY, new TimeInForce(TimeInForce.DAY));
		tifMap.put(OrderTIF.IOC, new TimeInForce(
				TimeInForce.IMMEDIATE_OR_CANCEL));
		tifMap.put(OrderTIF.OPG, new TimeInForce(TimeInForce.AT_THE_OPENING));
		tifMap.put(OrderTIF.GTC, new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
		tifMap.put(OrderTIF.GTX,
				new TimeInForce(TimeInForce.GOOD_TILL_CROSSING));
	}
}
