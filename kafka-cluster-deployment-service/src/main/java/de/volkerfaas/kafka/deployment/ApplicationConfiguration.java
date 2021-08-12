package de.volkerfaas.kafka.deployment;

import de.volkerfaas.kafka.deployment.config.Config;
import de.volkerfaas.kafka.deployment.config.MailConfig;
import de.volkerfaas.kafka.deployment.integration.impl.BaseRepositoryImpl;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Objects;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "de.volkerfaas.kafka.deployment.integration",
        repositoryBaseClass = BaseRepositoryImpl.class
)
@EnableJpaAuditing
public class ApplicationConfiguration {

    @Bean
    public InMemoryAuditEventRepository repository(){
        return new InMemoryAuditEventRepository();
    }

    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect(Metrics.globalRegistry);
    }

    @Bean
    public JavaMailSender javaMailSender(@Autowired Config config) {
        final MailConfig mailConfig = config.getMail();
        final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        if (Objects.nonNull(mailConfig)) {
            javaMailSender.setHost(mailConfig.getHost());
            javaMailSender.setPort(mailConfig.getPort());
            javaMailSender.setUsername(mailConfig.getUsername());
            javaMailSender.setPassword(mailConfig.getPassword());
            javaMailSender.setJavaMailProperties(mailConfig.getProperties());
        }

        return javaMailSender;
    }

    @Bean
    public StringEncryptor jasyptStringEncryptor(@Value("${password}") final String password) {
        final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor;
    }

}
