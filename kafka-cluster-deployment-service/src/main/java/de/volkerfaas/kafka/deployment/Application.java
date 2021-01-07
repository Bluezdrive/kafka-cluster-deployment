package de.volkerfaas.kafka.deployment;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Locale;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        final SpringApplication springApplication = new SpringApplicationBuilder(Application.class)
                .banner(new KafkaClusterDeploymentBanner())
                .bannerMode(Banner.Mode.CONSOLE)
                .build();
        springApplication.run(args);
    }

}
