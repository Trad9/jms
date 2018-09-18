package kn.swiftlog.tst.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableJms
@SpringBootApplication
public class JmsApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(JmsApplication.class, args);
	}
}
