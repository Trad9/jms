package kn.swiftlog.tst.jms.converters;

import kn.swiftlog.tst.jms.StockLevel;
import org.apache.activemq.ActiveMQQueueSession;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.support.converter.MessageConversionException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import static kn.swiftlog.tst.jms.configuration.XpathConfig.*;
import static org.mockito.Mockito.when;

public class MesageConverterOutputTest {

    private String refClientId;
    private String refWhId;
    private String refReqId;
    private StockLevel refSLObj;

    @Before
    public void setupValues(){
        MockitoAnnotations.initMocks(this);
        refClientId= "CLIE01";
        refWhId = "WHS01";
        refReqId = "bc2a55e8-5a07-4af6-85fd-8290d3ccfb51";
        refSLObj = new StockLevel(refClientId, refWhId, refReqId);
    }
    MesageConverterOutput msgConv = new MesageConverterOutput();
    @Mock
    Session session = new ActiveMQQueueSession(null);

    @Test
    public void toMessage() throws JMSException {
        when(session.createMessage()).thenReturn(new ActiveMQMessage());
        Message output = msgConv.toMessage(refSLObj,session);
        Assert.assertTrue(output.getStringProperty(CLIENT_ID.getNodeName()).equals(refClientId));
        Assert.assertTrue(output.getStringProperty(WH_ID.getNodeName()).equals(refWhId));
        Assert.assertTrue(output.getStringProperty(REQUEST_ID.getNodeName()).equals(refReqId));
    }

    @Test(expected = MessageConversionException.class)
    public void toMessageNonSLObj() throws JMSException {
        when(session.createMessage()).thenReturn(new ActiveMQMessage());
        Message output = msgConv.toMessage("test",session);
    }

    @Test
    public void fromMessage() throws JMSException {
        Message inputMsg = new ActiveMQMessage();
        inputMsg.setStringProperty(CLIENT_ID.getNodeName(),refClientId);
        inputMsg.setStringProperty(WH_ID.getNodeName(),refWhId);
        inputMsg.setStringProperty(REQUEST_ID.getNodeName(),refReqId);
        StockLevel outputObject = msgConv.fromMessage(inputMsg);
        Assert.assertTrue(outputObject.getClientId().equals(refClientId));
        Assert.assertTrue(outputObject.getRequestId().equals(refReqId));
        Assert.assertTrue(outputObject.getWarehouseId().equals(refWhId));
    }
}