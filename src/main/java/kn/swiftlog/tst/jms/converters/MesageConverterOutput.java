package kn.swiftlog.tst.jms.converters;

import kn.swiftlog.tst.jms.StockLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import static kn.swiftlog.tst.jms.configuration.XpathConfig.CLIENT_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.REQUEST_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.WH_ID;

@Component
public class MesageConverterOutput implements MessageConverter {

    private static Logger log = LoggerFactory.getLogger(MesageConverterOutput.class);

    @Override
    public Message toMessage(Object object, Session session)
        throws JMSException, MessageConversionException {
        if (!(object instanceof StockLevel)){
            throw new MessageConversionException("Wrong input type. StockLevel is expected");
        }
        Message msg = session.createMessage();
        StockLevel sl = (StockLevel)object;
        msg.setStringProperty(CLIENT_ID.getNodeName(),sl.getClientId());
        msg.setStringProperty(WH_ID.getNodeName(),sl.getWarehouseId());
        msg.setStringProperty(REQUEST_ID.getNodeName(),sl.getRequestId());
        return msg;
    }


    @Override
    public StockLevel fromMessage(Message message)
            throws JMSException, MessageConversionException {
        StockLevel output = new StockLevel(
                message.getStringProperty(CLIENT_ID.getNodeName()),
                message.getStringProperty(WH_ID.getNodeName()),
                message.getStringProperty(REQUEST_ID.getNodeName()));

        return output;
    }


}
