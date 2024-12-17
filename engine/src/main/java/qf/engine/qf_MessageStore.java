package qf.engine;
import quickfix.MessageStore;

import glide.api.GlideClusterClient;
import glide.api.models.configuration.NodeAddress;
import glide.api.models.configuration.GlideClusterClientConfiguration;
import glide.api.models.configuration.RequestRoutingConfiguration;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import static glide.api.models.GlideString.gs;

public class qf_MessageStore implements MessageStore {

    private final GlideClusterClient glideClient;

    private static final Logger logger = Logger.getLogger(qf_MessageStore.class.getName());
    private int nextSenderSeqNum = 1;
    private int nextTargetSeqNum = 1;
    private String host = "";
    private int port1 = 6379;

    public qf_MessageStore(String clusterEndpoint, int port) {
        // Create cluster configuration
        GlideClusterClientConfiguration config = GlideClusterClientConfiguration.builder()
            .address(NodeAddress.builder().host(host).port(port1).build())
            .useTLS(true)
            .build();

        // Initialize the client
        try {
            this.glideClient = GlideClusterClient.createClient(config).get();
            System.out.println("Glide client created successfully");
            System.out.println("PING: " + this.glideClient.ping(gs("PING")).get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MemoryDB connection", e);
        }
    }
    @Override
    public void setNextSenderMsgSeqNum(int next) {
        logger.info("Updating Outgoing Sequence Number to: " + next);
        nextSenderSeqNum = next;
        // Write to your key-value database here
    }

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
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void get(int startSequence, int endSequence, Collection<String> messages) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public void incrNextSenderMsgSeqNum() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'incrNextSenderMsgSeqNum'");
    }

    @Override
    public void incrNextTargetMsgSeqNum() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'incrNextTargetMsgSeqNum'");
    }

    @Override
    public Date getCreationTime() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCreationTime'");
    }

    @Override
    public void refresh() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refresh'");
    }

    // Implement other MessageStore methods here
}