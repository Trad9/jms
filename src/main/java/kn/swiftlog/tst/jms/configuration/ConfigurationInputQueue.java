package kn.swiftlog.tst.jms.configuration;

import kn.swiftlog.tst.jms.converters.MesageConverterInput;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Primary
@Configuration
public class ConfigurationInputQueue {

    private static Logger log = LoggerFactory.getLogger(ConfigurationInputQueue.class);

    public static final String INPUT_QUEUE = "inputQueue";

    @Value("${spring.activemq.broker.input.url}")
    private String inputBrokerUrl;

    @Bean
    public BrokerService inputBroker() throws Exception {
        final BrokerService broker = new BrokerService();
        broker.addConnector(inputBrokerUrl);
        broker.setBrokerName("inputBroker");
        broker.setUseJmx(false);
        broker.setPersistent(false);
        return broker;
    }

    @Bean
    @Primary
    public ConnectionFactory jmsConnectionFactoryInput() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(inputBrokerUrl);
        return connectionFactory;
    }

    @Bean
    @Primary
    public JmsTemplate jmsTemplateInputQueue() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactoryInput());
        jmsTemplate.setMessageConverter(new MesageConverterInput());
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setDefaultDestinationName(INPUT_QUEUE);
        return jmsTemplate;
    }


    @Bean
    @Primary
    public JmsListenerContainerFactory<?> queueListenerFactory(@Qualifier("jmsConnectionFactoryInput") ConnectionFactory connectionFactory,
                                                               DefaultJmsListenerContainerFactoryConfigurer configurer)
    {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setMessageConverter(new MesageConverterInput());
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        factory.setErrorHandler(t -> log.error("An error has occurred in the transaction" + t.getMessage()));

        return factory;
    }

}
