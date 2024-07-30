package qf.engine;

import quickfix.*;
/**
 * Hello world!
 *
 */
public class qf_SimpleEngine {
    public static void main( String[] args ) throws ConfigError {
        System.out.println( "Hello World!" );
        SessionSettings settings = new SessionSettings("./src/main/resources/qf_SimpleEngine.cfg");
        qf_SimpleEngineApplication application = new qf_SimpleEngineApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Initiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
        initiator.start();
    }
}
