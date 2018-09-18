package kn.swiftlog.tst.jms.converters;

import com.google.common.annotations.VisibleForTesting;
import kn.swiftlog.tst.jms.StockLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

import static kn.swiftlog.tst.jms.configuration.XpathConfig.CLIENT_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.REQUEST_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.WH_ID;

@Component
public class MesageConverterInput implements MessageConverter {

    private static Logger log = LoggerFactory.getLogger(MesageConverterInput.class);


    @Override
    public Message toMessage(Object object, Session session)
        throws JMSException, MessageConversionException {
        if (!(object instanceof String)){
            throw new MessageConversionException("Incorrect input type. String is expected");
        }
        Message msg = session.createTextMessage();
        ((TextMessage) msg).setText((String) object);
        return msg;
    }


    @Override
    public StockLevel fromMessage(Message message)
            throws MessageConversionException {
        StockLevel output;
        try{
            output = processStockLevelInput(((TextMessage)message).getText());
        }catch (Exception e){
            throw new MessageConversionException(e.getMessage(),e);
        }
        return output;
    }

    private StockLevel processStockLevelInput(String inboundXMLString) throws Exception{

        Document inboundXML = processStringToXML(inboundXMLString);
        XPath xPath = XPathFactory.newInstance().newXPath();



        return new StockLevel(
                (String)xPath.evaluate(CLIENT_ID.getPathToNode(), inboundXML, XPathConstants.STRING),
                (String)xPath.evaluate(WH_ID.getPathToNode(), inboundXML, XPathConstants.STRING),
                (String)xPath.evaluate(REQUEST_ID.getPathToNode(), inboundXML, XPathConstants.STRING)
        );
    }

    private Document processStringToXML(String xmlString) throws Exception
    {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = df.newDocumentBuilder();
        InputSource insrc = new InputSource(new StringReader(xmlString));
        return db.parse(insrc);
    }
}
