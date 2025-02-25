package qf.engine;
import quickfix.MessageStore;

import glide.api.GlideClusterClient;
import glide.api.models.configuration.NodeAddress;
import glide.api.models.configuration.GlideClusterClientConfiguration;
import glide.api.models.configuration.RequestRoutingConfiguration;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static glide.api.models.GlideString.gs;

public class qf_MessageStore implements MessageStore {

    private final GlideClusterClient glideClient;

    private static final Logger logger = Logger.getLogger(qf_MessageStore.class.getName());

    private static final String SEQ_KEY = "seq_key"; // seq hash
    private static final String MSG_KEY = "msg_key"; // msg hash
    private static final String SENDER_FIELD = "sender_field";
    private static final String TARGET_FIELD = "target_field";

    private int nextSenderSeqNum = 1;
    private int nextTargetSeqNum = 1;
    //private String host = System.getenv("MDB_HOST");
    private int port1 = 6379;
    private String host = "127.0.0.1";

    public qf_MessageStore() {

        // add console as handler for the java logger
        SimpleFormatter formatter = new SimpleFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        // Create cluster configuration
        GlideClusterClientConfiguration config = GlideClusterClientConfiguration.builder()
            .address(NodeAddress.builder()
            .host(host)
            .port(port1).build())
            .useTLS(false)
            .build();

        // Initialize the client
        try {
            logger.info("Initializing MemoryDB connection to host " + host);
            this.glideClient = GlideClusterClient.createClient(config).get();
            logger.info("Glide client created successfully");
            
            // Test the connection by pinging the cluster
            String pingResponse = this.glideClient.ping(gs("PING")).get().toString();
            logger.info("MemoryDB Connection Test - PING response: " + pingResponse);
        } catch (Exception e) {
            logger.severe("Failed to initialize MemoryDB connection: " + e.getMessage());
            throw new RuntimeException("Failed to initialize MemoryDB connection", e);
        }
    }

    // this logic is wrong, TODO: fix
    @Override
    public void setNextSenderMsgSeqNum(int next) {
        logger.info("Updating Outgoing Sequence Number to: " + next);
        try {
            Map<String, String> map = new HashMap<>();
            map.put(SENDER_FIELD, String.valueOf(next));
            glideClient.hset(SEQ_KEY, map).get();
            nextSenderSeqNum = next;
        } catch (Exception ex) {
            logger.severe("Failed to update Outgoing Sequence Number: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // same as above
    @Override
    public void setNextTargetMsgSeqNum(int next) {
        logger.info("Updating Incoming Sequence Number to: " + next);
        nextTargetSeqNum = next;
        // Write to your key-value database here
    }

    @Override
    public int getNextSenderMsgSeqNum() {
        logger.info("Retrieving Outgoing Sequence Number: " + nextSenderSeqNum);
        return nextSenderSeqNum;
    }

    @Override
    public int getNextTargetMsgSeqNum() {
        logger.info("Retrieving Incoming Sequence Number: " + nextTargetSeqNum);
        return nextTargetSeqNum;
    }

    @Override
    public void reset() {
        logger.info("Resetting Sequence Numbers");
        nextSenderSeqNum = 1;
        nextTargetSeqNum = 1;
    }

    @Override
    public boolean set(int sequence, String message) throws IOException {
        // TODO Auto-generated method stub
        //Adds a raw fix messages to the store with the given sequence number. 
        //(Most implementations just append the message data to the store so be careful about assuming random access behavior.)
        logger.info("Setting message: " + message);
        return true;
        //throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void get(int startSequence, int endSequence, Collection<String> messages) throws IOException {
        // TODO Auto-generated method stub
        logger.info("Getting messages from " + startSequence + " to " + endSequence);
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public void incrNextSenderMsgSeqNum() throws IOException {
        logger.info("Incrementing Outgoing Sequence Number to: " + (nextSenderSeqNum + 1));
        try {
            glideClient.incr(SEQ_KEY);
            nextSenderSeqNum++;
        } catch (Exception ex) {
            logger.severe("Failed to increment Outgoing Sequence Number: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // fix:seq:{SenderCompID}:{TargetCompID}:{SessionQualifier}:{InstanceID}:{Direction}
    @Override
    public void incrNextTargetMsgSeqNum() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'incrNextTargetMsgSeqNum'");
    }

    @Override
    public Date getCreationTime() throws IOException {
        return new Date();
    }

    @Override
    public void refresh() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refresh'");
    }

    // Implement other MessageStore methods here
}