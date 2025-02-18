package qf.engine;

import quickfix.MessageStore;
import quickfix.SessionID;
import quickfix.MessageStoreFactory;

public class qf_MessageStoreFactory implements MessageStoreFactory {
    
    @Override
    public MessageStore create(SessionID sessionID) {
        return new qf_MessageStore();
    }
}
