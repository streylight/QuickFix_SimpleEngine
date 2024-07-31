package qf.client;

import quickfix.*;
import quickfix.field.OrdType;
import quickfix.field.Side;

public class qf_ClientMessageSenderApplication {
    public static void main(String[] args) throws ConfigError, InterruptedException, SessionNotFound {
        System.out.println("Hello world!");
        // ./src/main/resources/qf_SimpleEngine.cfg
        SessionSettings settings = new SessionSettings("./src/main/resources/sender-fix.cfg");
        Application application = new ApplicationAdapter();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);   
        MessageFactory messageFactory = new DefaultMessageFactory();

        SocketInitiator initiator = new SocketInitiator(
            application, 
            messageStoreFactory, 
            settings, 
            logFactory, 
            messageFactory
        );

        initiator.start();

        // idk why this is needed, seems dumb
        Thread.sleep(5000);

        // init new session to connect to engine
        SessionID sessionID = new SessionID("FIX.4.4", "CLIENT", "ENGINE");

        qf_ClientMessageSender messageSender = new qf_ClientMessageSender(
            sessionID, 
            "123", 
            "AMZN", // pump it 
            Side.BUY, 
            OrdType.MARKET, 
            100.0, 
            100
        );

        for (int i = 0; i < 10; i++) {
            messageSender.sendNewOrderSingle();
            System.out.println("Sending message " + i);
            Thread.sleep(1000); // wait for 1 second between messages
        }

        initiator.stop();
    }
}