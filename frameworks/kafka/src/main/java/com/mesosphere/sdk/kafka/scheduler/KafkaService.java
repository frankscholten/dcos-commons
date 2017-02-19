package com.mesosphere.sdk.kafka.scheduler;

import com.mesosphere.sdk.config.ConfigStore;
import com.mesosphere.sdk.kafka.api.BrokerController;
import com.mesosphere.sdk.scheduler.DefaultScheduler;
import com.mesosphere.sdk.specification.DefaultService;
import com.mesosphere.sdk.specification.DefaultServiceSpec;
import com.mesosphere.sdk.specification.ServiceSpec;
import com.mesosphere.sdk.state.StateStore;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Kafka Service.
 */
public class KafkaService extends DefaultService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);


    public KafkaService(File pathToYamlSpecification) throws Exception {
        super(pathToYamlSpecification);
    }


    @Override
    protected void startApiServer(DefaultScheduler defaultScheduler,
                                  int apiPort,
                                  Collection<Object> additionalResources) {
        final ServiceSpec serviceSpec = super.getServiceSpec();
        final Collection<Object> apiResources = new ArrayList<>();
        final String zkUri = String.format("%s/dcos-service-%s",
                serviceSpec.getZookeeperConnection(), serviceSpec.getName());
        apiResources.add(new BrokerController(zkUri));
        if (CollectionUtils.isNotEmpty(additionalResources)) {
            apiResources.addAll(additionalResources);
        }
        LOGGER.info("Starting API server with resources: {}", apiResources);
        super.startApiServer(defaultScheduler, apiPort, apiResources);
    }

    private void updater(ConfigStore configStore, StateStore stateStore){
        // Generate a ConfigStore<SpecStore> from existing ConfigStore<KafkaSchedulerConfiguration>

        DefaultServiceSpec.newBuilder().name("kafka");
    }

    private boolean checkUpdate(){
        if (System.getenv("CONFIG_UPDATE") != null) {
            LOGGER.info("Kafka Update Configuration Update Mode !");
            return true;
        }
            return false;
    }
}
