package kn.swiftlog.tst.jms.recievers;

import kn.swiftlog.tst.jms.StockLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import javax.jms.Session;

import static kn.swiftlog.tst.jms.configuration.ConfigurationInputQueue.INPUT_QUEUE;
import static kn.swiftlog.tst.jms.configuration.ConfigurationOutputTopic.OUTPUT_TOPIC;

@Service
public class XMLReciever {

    private static Logger log = LoggerFactory.getLogger(XMLReciever.class);
    @Qualifier("jmsTemplateOutputTopic")
    @Autowired
    private JmsTemplate topicTemplate;
    @SendTo(OUTPUT_TOPIC)
    @JmsListener(destination = INPUT_QUEUE, containerFactory = "queueListenerFactory")
    public void receiveMessage(@Payload StockLevel stockLevel,
                               @Headers MessageHeaders headers,
                               Message message,
                               Session session) {
        log.info("Recieved" + stockLevel.toString());
        topicTemplate.convertAndSend(OUTPUT_TOPIC,stockLevel);
    }
}
