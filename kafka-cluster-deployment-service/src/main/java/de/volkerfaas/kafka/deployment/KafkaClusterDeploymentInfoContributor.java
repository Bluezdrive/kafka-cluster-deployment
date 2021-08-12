package de.volkerfaas.kafka.deployment;

import de.volkerfaas.kafka.deployment.utils.ImplementationEntries;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class KafkaClusterDeploymentInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        final ImplementationEntries implementationEntries = ImplementationEntries.get(Application.class);
        builder.withDetail("vendor", implementationEntries.getVendor("unknown"));
        builder.withDetail("version", implementationEntries.getVersion("X.X-SNAPSHOT"));
    }

}
