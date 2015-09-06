package org.mogware.messagebus.channels;

import org.mogware.messagebus.ChannelEnvelope;
import org.mogware.messagebus.DeliveryContext;
import org.mogware.system.Disposable;

public interface MessageAuditor extends Disposable {
    void auditReceive(DeliveryContext delivery);
    void auditSend(ChannelEnvelope envelope, DeliveryContext delivery);    
}
