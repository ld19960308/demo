package com.example.demo;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;

@SpringBootApplication
public class Demo1Application {
	
	private static final Logger log = LoggerFactory.getLogger(Demo1Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Demo1Application.class, args);
	}
	/*@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }*/
	
	/*@Bean
	public CommandLineRunner demo(CustomerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			repository.findById((short) 1)
				.ifPresent(customer -> {
					log.info("Customer found with findById(1L):");
					log.info("--------------------------------");
					log.info(customer.toString());
					log.info("");
				});

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByLastName("Bauer").forEach(bauer -> {
				log.info(bauer.toString());
			});
			// for (Customer bauer : repository.findByLastName("Bauer")) {
			// 	log.info(bauer.toString());
			// }
			log.info("");
		};
	}
	*/
	
	
	@Bean
	  public GracefulShutdown gracefulShutdown() {
	    return new GracefulShutdown();
	  }
	 
	  /**
	   * 配置tomcat
	   *
	   * @return
	   */
	  @Bean
	  public ServletWebServerFactory servletContainer() {
	    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	    tomcat.addConnectorCustomizers(gracefulShutdown());
	    return tomcat;
	  }
	 
	  /**
	   * 优雅关闭 Spring Boot。容器必须是 tomcat
	   */
	  private class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
	    private final Logger log = LoggerFactory.getLogger(GracefulShutdown.class);
	    private volatile Connector connector;
	    private final int waitTime = 10;
	 
	    @Override
	    public void customize(Connector connector) {
	      this.connector = connector;
	    }
	 
	    @Override
	    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
	      this.connector.pause();
	      Executor executor = this.connector.getProtocolHandler().getExecutor();
	      if (executor instanceof ThreadPoolExecutor) {
	        try {
	          ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
	          threadPoolExecutor.shutdown();
	          if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
	            log.warn("Tomcat 进程在" + waitTime + " 秒内无法结束，尝试强制结束");
	          }
	        } catch (InterruptedException ex) {
	          Thread.currentThread().interrupt();
	        }
	      }
	    }
	  }

}

