package org.devzone.mailcollector;

import org.devzone.mailcollector.service.EmailFetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MailCollectorApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(MailCollectorApplication.class);

	private EmailFetchService fetchService;

	public MailCollectorApplication(EmailFetchService fetchService) {
		this.fetchService = fetchService;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(MailCollectorApplication.class, args);
		// shut down all beans, otherwise the application would keep running
		ctx.close();

	}

	@Override
	public void run(String... args) throws Exception {
		logger.debug("Start process");
		fetchService.fetchMails();
	}
}
