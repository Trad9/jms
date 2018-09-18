package kn.swiftlog.tst.jms.converters;

import kn.swiftlog.tst.jms.StockLevel;
import org.apache.activemq.ActiveMQQueueSession;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.support.converter.MessageConversionException;

import javax.jms.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MesageConverterInputTest {

    private String refClientId;
    private String refWhId;
    private String refReqId;
    private String refInputMessageStr;

    @Before
    public void setupValues(){
        MockitoAnnotations.initMocks(this);
        refClientId= "CLIE01";
        refWhId = "WHS01";
        refReqId = "bc2a55e8-5a07-4af6-85fd-8290d3ccfb51";
        refInputMessageStr = "<UC_STOCK_LEVEL_IFD>\n" +
                "<CTRL_SEG>\n" +
                "<TRNNAM>UC_STOCK_LEVEL</TRNNAM>\n" +
                "<TRNVER>20180100</TRNVER>\n" +
                "<UUID>0de01919-81eb-4cc7-a51d-15f6085fc1a4</UUID>\n" +
                "<WH_ID>"+refWhId+"</WH_ID>\n" +
                "<CLIENT_ID>"+refClientId+"</CLIENT_ID>\n" +
                "<ISO_2_CTRY_NAME>GB</ISO_2_CTRY_NAME>\n" +
                "<REQUEST_ID>"+refReqId+"</REQUEST_ID>\n" +
                "<ROUTE_ID>186</ROUTE_ID>\n" +
                "</CTRL_SEG>\n" +
                "</UC_STOCK_LEVEL_IFD>\n";
    }
    @Mock
    Session session = new ActiveMQQueueSession(null);
    MesageConverterInput msgConv = new MesageConverterInput();

    @Test
    public void toMessage() throws JMSException {
        when(session.createTextMessage()).thenReturn(new ActiveMQTextMessage());
        Message outputMsg = msgConv.toMessage(refInputMessageStr,session);
        Assert.assertTrue(((TextMessage)outputMsg).getText().equals(refInputMessageStr));
    }

    @Test(expected = MessageConversionException.class)
    public void toMessageIncorrectInput() throws JMSException {
        when(session.createTextMessage()).thenReturn(new ActiveMQTextMessage());
        Message outputMsg = msgConv.toMessage(024,session);
    }

    @Test
    public void fromMessage() throws Exception{
        Message inputMsg = new ActiveMQTextMessage();
        ((ActiveMQTextMessage) inputMsg).setText(refInputMessageStr);
        StockLevel output = msgConv.fromMessage(inputMsg);
        Assert.assertTrue(output.getClientId().equals(refClientId));
        Assert.assertTrue(output.getRequestId().equals(refReqId));
        Assert.assertTrue(output.getWarehouseId().equals(refWhId));
    }

    @Test(expected = MessageConversionException.class)
    public void fromMessageIncorrectMessageType() throws Exception{
        Message inputMsg = new ActiveMQBytesMessage();
        StockLevel output = msgConv.fromMessage(inputMsg);
    }
}