package qf.engine;

import java.time.LocalDateTime;

import quickfix.*;
import quickfix.MessageCracker.Handler;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.Heartbeat;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;
import quickfix.fix44.TestRequest;

public class qf_SimpleEngine extends quickfix.MessageCracker implements Application {
    
    // D: NewOrderSingle
    @Handler
    public void onMessage(NewOrderSingle message, SessionID sessionID) {
        System.out.println("Received NewOrderSingle: " + message);
        try {
            validateNewOrderSingle(message);

            // muh logic to submit order to da market
            
            ExecutionReport executionReport = createExecutionReport(
                message, 
                "123", 
                OrdStatus.NEW, 
                ExecType.NEW, 
                0, 
                0
            );

            Session.sendToTarget(executionReport, sessionID);
        } catch (FieldNotFound | IncorrectDataFormat | IncorrectTagValue | SessionNotFound e) {
            e.printStackTrace();
        }
    }

    // F: OrderCancelRequest
    @Handler
    public void onMessage(OrderCancelRequest message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Received OrderCancelRequest: " + message);
    }

    // 8: ExecutionReport
    @Handler
    public void onMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Received ExecutionReport: " + message);
    }

    // 0: Heartbeat
    @Handler
    public void onMessage(Heartbeat message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Received Heartbeat: " + message);
    }

    // 1: TestRequest
    @Handler
    public void onMessage(TestRequest message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("Received TestRequest: " + message);
    }

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session created: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logon successful: " + sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logout: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        System.out.println("Sending admin message: " + message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        System.out.println("Received admin message: " + message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        System.out.println("Sending app message: " + message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("Received app message: " + message);
        crack(message, sessionId); 
    }

    public void validateNewOrderSingle(NewOrderSingle message) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue{
        if (!message.isSetField(Symbol.FIELD)) {
            throw new IncorrectTagValue(Symbol.FIELD);
        }

        if (!message.isSetField(Side.FIELD)) {
            throw new IncorrectTagValue(Side.FIELD);
        }

        if (!message.isSetField(OrdType.FIELD)) {
            throw new IncorrectTagValue(OrdType.FIELD);
        }

        if (!message.isSetField(OrderQty.FIELD)) {
            throw new IncorrectTagValue(OrderQty.FIELD);
        }
    }

    public ExecutionReport createExecutionReport(NewOrderSingle order, String execID, char ordStatus, char execType, double lastPrice, double lastQty) throws FieldNotFound {
    
        ExecutionReport executionReport = new ExecutionReport(
            new OrderID(order.getClOrdID().getValue()),   // Use the ClOrdID from the order as the OrderID (or generate a new one)
            new ExecID(execID),                           // Execution ID (unique for each execution)
            new ExecType(execType),                       // ExecType (e.g., New, Partial Fill, Fill)
            new OrdStatus(ordStatus),                     // OrdStatus (e.g., New, Partially Filled, Filled, Canceled)
            order.getSide(),                              // Side (same as the original order)
            new LeavesQty(order.getOrderQty().getValue()),// LeavesQty (quantity still open)
            new CumQty(0),                           // CumQty (cumulative quantity filled so far)
            new AvgPx(0)                             // AvgPx (average price of all fills)
        );

        executionReport.set(new TransactTime(LocalDateTime.now())); // TransactTime (time of execution)
        return executionReport;
    }
}
