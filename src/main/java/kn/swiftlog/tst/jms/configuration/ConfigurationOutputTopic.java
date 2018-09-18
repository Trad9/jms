package kn.swiftlog.tst.jms.configuration;

import kn.swiftlog.tst.jms.converters.MesageConverterOutput;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.QueueConnectionFactory;

@Configuration
public class ConfigurationOutputTopic {

    private static Logger log = LoggerFactory.getLogger(ConfigurationOutputTopic.class);


    public  static final String OUTPUT_TOPIC = "outputTopic" ;
    @Value("${spring.activemq.broker.output.url}")
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


