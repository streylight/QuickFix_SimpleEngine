package qf.engine;

import quickfix.*;
/**
 * Hello world!
 *
 */
public class qf_SimpleEngineApplication {
    public static void main( String[] args ) throws ConfigError, InterruptedException {
        try {
            System.out.println("Hello World!");
            SessionSettings settings = new SessionSettings("./src/main/resources/qf_SimpleEngine.cfg");
            qf_SimpleEngine application = new qf_SimpleEngine();
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Acceptor acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
            System.out.println("Starting acceptor...");
            acceptor.start();
            System.out.println("Acceptor started...");
            
            while (true) {
                System.out.println("Engine running...");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
