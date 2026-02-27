package com.acetp.feeder.config;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FeederPropertiesValidator {

    private final FeederProperties feederProperties;
    private final Environment environment;

    public FeederPropertiesValidator(FeederProperties feederProperties, Environment environment) {
        this.feederProperties = feederProperties;
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        boolean mqFeederProfile = environment.matchesProfiles("mqfeeder");
        boolean startupPurgeOnlyMode = mqFeederProfile
                && feederProperties.getMq().isPurgeOnStartupEnabled()
                && feederProperties.getMaxMessagesPerRun() == 0;

        if (feederProperties.getMaxMessagesPerRun() <= 0 && !startupPurgeOnlyMode) {
            throw new IllegalStateException(
                    "app.feeder.max-messages-per-run doit être supérieur à 0, "
                            + "sauf en mode mqfeeder avec purge-on-startup-enabled=true et max-messages-per-run=0.");
        }
    }
}
