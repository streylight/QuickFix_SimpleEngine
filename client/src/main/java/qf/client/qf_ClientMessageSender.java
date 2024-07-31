package qf.client;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix40.NewOrderSingle;

public class qf_ClientMessageSender {
    
    private final SessionID sessionId;
    private ClOrdID cl0rdId = null; // client order ID
    private Symbol symbol = null; // ticker symbol
    private Side side = null; // buy or sell
    private OrdType orderType = null; // order type: market, limit, stop, etc.
    private double price = 0.0;
    private int quantity = 0;
    
    // new Symbol(symbol), new Side(side), new OrderQty(quantity), new OrdType(orderType)
    public qf_ClientMessageSender(SessionID sessionId, String clordId, String symbol, char side, char orderType, double price, int quantity) {
        this.sessionId = sessionId;
        this.cl0rdId = new ClOrdID(clordId);
        this.symbol = new Symbol(symbol);
        this.side = new Side(side);
        this.orderType = new OrdType(orderType);
        this.price = price;
        this.quantity = quantity;
    }

    public void sendNewOrderSingle() {
        // quickfix.field.ClOrdID clOrdID, quickfix.field.HandlInst handlInst, quickfix.field.Symbol symbol, quickfix.field.Side side, quickfix.field.OrderQty orderQty, quickfix.field.OrdType ordType
        NewOrderSingle newOrderSingle = new NewOrderSingle(
            this.cl0rdId, 
            new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC_BROKER_INTERVENTION_OK), 
            this.symbol, 
            this.side, 
            new OrderQty(quantity),
            this.orderType
        );
        newOrderSingle.set(new Price(price));
        //newOrderSingle.set(new OrderQty(quantity));

        try {
            Session.sendToTarget(newOrderSingle, sessionId);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }
}
