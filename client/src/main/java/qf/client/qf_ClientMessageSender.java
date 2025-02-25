package qf.client;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

public class qf_ClientMessageSender {
    
    private static final Logger logger = Logger.getLogger(qf_ClientMessageSender.class.getName());

    private SessionID sessionId;
    private ClOrdID cl0rdId = null; // client order ID
    private Symbol symbol = null; // ticker symbol
    private Side side = null; // buy or sell
    private OrdType orderType = null; // order type: market, limit, stop, etc.
    private double price;
    private int quantity;
    
    // new Symbol(symbol), new Side(side), new OrderQty(quantity), new OrdType(orderType)
    public qf_ClientMessageSender(SessionID sessionId, String clordId, String symbol, char side, char orderType, double price, int quantity) {
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.cl0rdId = new ClOrdID(Objects.requireNonNull(clordId, "Order ID cannot be null"));
        this.symbol = new Symbol(Objects.requireNonNull(symbol, "Symbol cannot be null"));
        this.side = new Side(Objects.requireNonNull(side, "Side cannot be null"));
        this.orderType = new OrdType(Objects.requireNonNull(orderType, "Order type cannot be null"));
        this.price = price;
        this.quantity = quantity;
    }

    public qf_ClientMessageSender(SessionID sessionId, String clordId, String symbol, char side, char orderType, int quantity) {
        this(sessionId, clordId, symbol, side, orderType, 0.0, quantity);
    }

    public void sendNewOrderSingle() throws SessionNotFound{
        NewOrderSingle newOrderSingle = createNewOrderSingle();
        logger.info("Sending order single: " + newOrderSingle.toString());
        Session.sendToTarget(newOrderSingle, this.sessionId);
        logger.info("Order single sent successfully");
    }

    private NewOrderSingle createNewOrderSingle() {
        NewOrderSingle newOrderSingle = new NewOrderSingle(
            this.cl0rdId, 
            this.side, 
            new TransactTime(),
            this.orderType
        );

        newOrderSingle.set(this.symbol);
        newOrderSingle.set(new OrderQty(quantity));
        newOrderSingle.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC_BROKER_INTERVENTION_OK));

        // only set price or limit orders
        if (this.orderType.getValue() == OrdType.LIMIT) {
            newOrderSingle.set(new Price(this.price));
        }
        return newOrderSingle;
    }
}
