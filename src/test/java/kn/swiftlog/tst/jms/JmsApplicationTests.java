package kn.swiftlog.tst.jms;

import javafx.application.Application;
import kn.swiftlog.tst.jms.converters.MesageConverterOutput;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

import static kn.swiftlog.tst.jms.configuration.ConfigurationInputQueue.INPUT_QUEUE;
import static kn.swiftlog.tst.jms.configuration.ConfigurationOutputTopic.OUTPUT_TOPIC;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.CLIENT_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.REQUEST_ID;
import static kn.swiftlog.tst.jms.configuration.XpathConfig.WH_ID;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JmsApplicationTests  {

	@Value("${test.output.broker.url}")
	private String outputBrokerUrl;
	private ActiveMQConnectionFactory connFactoryOuput;
	private String refClientId;
	private String refWhId;
	private String refReqId;
	private String refInputMessageStr;
	private StockLevel refSLObj;

	@Configuration
	@Import(JmsApplication.class)
	public static class TestConfig
	{
		@Value("${test.output.broker.url}")
		private String outputBrokerUrl;

		@Qualifier("outputBroker")
		@Bean
		public BrokerService outputBroker() throws Exception {
			final BrokerService broker = new BrokerService();
			broker.addConnector(outputBrokerUrl);
			broker.setBrokerName("outputBroker");
			broker.setUseJmx(false);
			broker.setPersistent(false);
			return broker;
		}

		@Bean
		public QueueConnectionFactory jmsConnectionFactoryOutput() {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(outputBrokerUrl);
			return connectionFactory;
		}

		@Qualifier("jmsTemplateOutputTopic")
		@Bean
		public JmsTemplate jmsTemplateOutputTopic() {
			JmsTemplate jmsTemplate = new JmsTemplate();
			jmsTemplate.setConnectionFactory(jmsConnectionFactoryOutput());
			jmsTemplate.setMessageConverter(new MesageConverterOutput());
			jmsTemplate.setPubSubDomain(true);
			jmsTemplate.setDefaultDestinationName(OUTPUT_TOPIC);
			return jmsTemplate;
		}

	}


	@Before
	public void setup()  throws Exception {
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
		refSLObj = new StockLevel(refClientId, refWhId, refReqId);
		connFactoryOuput = new ActiveMQConnectionFactory(outputBrokerUrl+ "?jms.prefetchPolicy.all=1");
	}


	@Qualifier("jmsTemplateInputQueue")
	@Autowired
	private JmsTemplate jmsTemplateInput;



	@Test
	public void integrationTest() throws Exception {

		MessageConsumer messageConsumer1 = getMessageConsumer();
		MessageConsumer messageConsumer2 = getMessageConsumer();

		this.jmsTemplateInput.convertAndSend(INPUT_QUEUE, refInputMessageStr);

		Message message1 = messageConsumer1.receive(1000);
		Message message2 = messageConsumer2.receive(1000);

		assertEquals(message1.getStringProperty(CLIENT_ID.getNodeName()), refClientId);
		assertEquals(message1.getStringProperty(WH_ID.getNodeName()), refWhId);
		assertEquals(message1.getStringProperty(REQUEST_ID.getNodeName()), refReqId);
		assertEquals(message1.getStringProperty(CLIENT_ID.getNodeName()), message2.getStringProperty(CLIENT_ID.getNodeName()));
		assertEquals(message1.getStringProperty(WH_ID.getNodeName()), message2.getStringProperty(WH_ID.getNodeName()));
		assertEquals(message1.getStringProperty(REQUEST_ID.getNodeName()), message2.getStringProperty(REQUEST_ID.getNodeName()));
	}


	private MessageConsumer getMessageConsumer() throws JMSException {
		Connection outputConnection = connFactoryOuput.createConnection();
		outputConnection.start();
		Session session =
				outputConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(OUTPUT_TOPIC);
		return session.createConsumer(topic);
	}

}
