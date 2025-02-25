package qf.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import quickfix.*;
/**
 * Hello world!
 *
 */
public class qf_SimpleEngineApplication {
    public static void main( String[] args ) throws ConfigError, InterruptedException {
        try {
            System.out.println("Hello World!");
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            SessionSettings settings = new SessionSettings("./engine/src/main/resources/qf_SimpleEngine.cfg");
            qf_SimpleEngine application = new qf_SimpleEngine();
            MessageStoreFactory messageStoreFactory = new qf_MessageStoreFactory();
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Acceptor acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
            System.out.println("Starting acceptor...");
            acceptor.start();
            System.out.println("Acceptor started...");
            
            while (true) {
                //System.out.println("Engine running...");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
