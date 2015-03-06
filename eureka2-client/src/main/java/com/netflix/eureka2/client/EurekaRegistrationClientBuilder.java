/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.eureka2.client;

import com.netflix.eureka2.channel.RegistrationChannel;
import com.netflix.eureka2.client.channel.ClientChannelFactory;
import com.netflix.eureka2.client.channel.RegistrationChannelFactory;
import com.netflix.eureka2.client.registration.EurekaRegistrationClientImpl;
import com.netflix.eureka2.client.resolver.ServerResolver;

/**
 * @author David Liu
 */
public class EurekaRegistrationClientBuilder
        extends AbstractClientBuilder<EurekaRegistrationClient, EurekaRegistrationClientBuilder> {

    protected ServerResolver serverResolver;

    /**
     * Connect to write servers specified by the given write server resolver.
     *
     * @param serverResolver the resolver to specify which write server to connect to (may have redirects)
     * @return a builder to continue client construction
     */
    public EurekaRegistrationClientBuilder withServerResolver(ServerResolver serverResolver) {
        this.serverResolver = serverResolver;
        return self();
    }

    @Override
    protected EurekaRegistrationClient buildClient() {
        if (serverResolver == null) {
            throw new IllegalArgumentException("Cannot build client for registration without write server resolver");
        }

        ClientChannelFactory<RegistrationChannel> channelFactory
                = new RegistrationChannelFactory(transportConfig, serverResolver, clientMetricFactory);

        return new EurekaRegistrationClientImpl(channelFactory);
    }
}
