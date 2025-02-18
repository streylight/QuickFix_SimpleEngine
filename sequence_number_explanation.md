## FIX Session Sequence Number Generation

Based on the code in `qf_MessageStore.java`, here's how sequence numbers are currently handled in the FIX session:

1. **Sequence Number Storage**:
   - Two member variables track sequence numbers:
     - `nextSenderSeqNum` for outgoing messages
     - `nextTargetSeqNum` for incoming messages

2. **Current Implementation Status**:
   - Basic getter/setter methods exist for both sequence numbers
   - `setNextSenderMsgSeqNum()` and `setNextTargetMsgSeqNum()` allow setting sequence numbers
   - `getNextSenderMsgSeqNum()` and `getNextTargetMsgSeqNum()` retrieve current sequence numbers
   - Increment methods `incrNextSenderMsgSeqNum()` and `incrNextTargetMsgSeqNum()` are not yet implemented
   - Comments indicate sequence numbers should be persisted to a key-value database (not yet implemented)

3. **What Needs to be Implemented**:
   - The increment methods need to be implemented to properly advance sequence numbers
   - Persistence to a key-value database needs to be added for durability
   - The `reset()` method should properly initialize sequence numbers
   - Proper synchronization might be needed for thread safety

4. **Typical FIX Sequence Number Flow**:
   - Sequence numbers typically start at 1 for new sessions
   - Each sent/received message increments the respective sequence number
   - Numbers must be persistent across sessions for reliability
   - Sequence numbers help ensure message delivery and proper message ordering

5. **Recommendations for Implementation**:
   - Implement the increment methods to atomically increase sequence numbers
   - Add proper database persistence in setter methods
   - Add database retrieval in getter methods
   - Consider adding validation to prevent invalid sequence numbers
   - Implement proper error handling for database operations