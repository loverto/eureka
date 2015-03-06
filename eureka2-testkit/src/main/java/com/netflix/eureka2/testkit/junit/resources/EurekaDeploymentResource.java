package com.netflix.eureka2.testkit.junit.resources;

import com.netflix.eureka2.client.EurekaInterestClient;
import com.netflix.eureka2.client.EurekaInterestClientBuilder;
import com.netflix.eureka2.client.EurekaRegistrationClient;
import com.netflix.eureka2.client.EurekaRegistrationClientBuilder;
import com.netflix.eureka2.client.resolver.ServerResolver;
import com.netflix.eureka2.config.BasicEurekaTransportConfig;
import com.netflix.eureka2.config.EurekaTransportConfig;
import com.netflix.eureka2.testkit.embedded.EurekaDeployment;
import com.netflix.eureka2.testkit.embedded.EurekaDeployment.EurekaDeploymentBuilder;
import com.netflix.eureka2.testkit.embedded.server.EmbeddedReadServer;
import com.netflix.eureka2.testkit.embedded.server.EmbeddedWriteServer;
import com.netflix.eureka2.testkit.junit.resources.EurekaExternalResources.EurekaExternalResource;

/**
 * @author Tomasz Bak
 */
public class EurekaDeploymentResource extends EurekaExternalResource {

    private final int writeClusterSize;
    private final int readClusterSize;
    private final EurekaTransportConfig transportConfig;

    private EurekaDeployment eurekaDeployment;

    public EurekaDeploymentResource(int writeClusterSize, int readClusterSize) {
        this(writeClusterSize, readClusterSize, new BasicEurekaTransportConfig.Builder().build());
    }

    public EurekaDeploymentResource(int writeClusterSize, int readClusterSize, EurekaTransportConfig transportConfig) {
        this.writeClusterSize = writeClusterSize;
        this.readClusterSize = readClusterSize;
        this.transportConfig = transportConfig;
    }

    public EurekaDeployment getEurekaDeployment() {
        return eurekaDeployment;
    }

    /**
     * Create a {@link EurekaRegistrationClient} instance to register with a particular write server
     *
     * @param idx id of a write server where to connect
     */
    public EurekaRegistrationClient registrationClientToWriteServer(int idx) {
        EmbeddedWriteServer server = eurekaDeployment.getWriteCluster().getServer(idx);
        return new EurekaRegistrationClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(server.getRegistrationResolver())
                .build();

    }

    /**
     * Create a {@link EurekaRegistrationClient} instance to register with any instance in a write cluster
     */
    public EurekaRegistrationClient registrationClientToWriteCluster() {
        return new EurekaRegistrationClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(eurekaDeployment.getWriteCluster().registrationResolver())
                .build();

    }

    /**
     * Create a {@link EurekaInterestClient} instance to do interest discovery with a particular write server
     *
     * @param idx id of a write server where to connect
     */
    public EurekaInterestClient interestClientToWriteServer(int idx) {
        EmbeddedWriteServer server = eurekaDeployment.getWriteCluster().getServer(idx);
        return new EurekaInterestClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(server.getInterestResolver())
                .build();
    }

    /**
     * Create a {@link EurekaInterestClient} instance to do interest discovery with any instance in a write cluster
     */
    public EurekaInterestClient interestClientToWriteCluster() {
        return new EurekaInterestClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(eurekaDeployment.getWriteCluster().interestResolver())
                .build();
    }

    /**
     * Create a {@link EurekaInterestClient} instance to do interest discovery with a particular read server
     *
     * @param idx id of a write server where to connect
     */
    public EurekaInterestClient interestClientToReadServer(int idx) {
        EmbeddedReadServer server = eurekaDeployment.getReadCluster().getServer(idx);
        return new EurekaInterestClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(server.getInterestResolver())
                .build();
    }

    /**
     * Create a {@link EurekaInterestClient} instance to do interest discovery with any instance in a read cluster
     */
    public EurekaInterestClient interestClientToReadCluster() {
        return new EurekaInterestClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(eurekaDeployment.getReadCluster().interestResolver())
                .build();
    }

    /**
     * Create a {@link EurekaInterestClient} instance to do interest discovery with any instance in a read cluster,
     * using the canonical method to first discover the read cluster from the write cluster
     */
    public EurekaInterestClient cannonicalInterestClient() {
        return new EurekaInterestClientBuilder()
                .withTransportConfig(transportConfig)
                .withServerResolver(ServerResolver.fromEureka(eurekaDeployment.getWriteCluster().interestResolver())
                        .forVips(eurekaDeployment.getReadCluster().getVip()))
                .build();
    }

    @Override
    protected void before() throws Throwable {
        eurekaDeployment = new EurekaDeploymentBuilder()
                .withWriteClusterSize(writeClusterSize)
                .withReadClusterSize(readClusterSize)
                .withEphemeralPorts(true)
                .withTransportConfig(transportConfig)
                .build();
    }

    @Override
    protected void after() {
        if (eurekaDeployment != null) {
            eurekaDeployment.shutdown();
        }
    }
}
