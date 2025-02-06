/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.serviceregistry.refresh;

import java.net.URI;
import java.util.List;

import org.apache.servicecomb.foundation.common.net.IpPort;
import org.apache.servicecomb.foundation.common.net.URIEndpointObject;
import org.apache.servicecomb.http.client.common.AbstractAddressManager;
import org.apache.servicecomb.http.client.event.RefreshEndpointEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ServiceRegistryAddressManager extends AbstractAddressManager {
  private static final String URI_PREFIX = "rest://";

  public ServiceRegistryAddressManager(List<String> addresses, EventBus eventBus) {
    super(addresses);
    eventBus.register(this);
  }

  public IpPort getAvailableIpPort() {
    return transformIpPort(this.address());
  }

  @Override
  protected String normalizeUri(String endpoint) {
    return new URIEndpointObject(endpoint).toString();
  }

  public IpPort transformIpPort(String address) {
    URI uri = URI.create(URI_PREFIX + address);
    return new IpPort(uri.getHost(), uri.getPort());
  }

  @Subscribe
  public void onRefreshEndpointEvent(RefreshEndpointEvent event) {
    refreshEndpoint(event, RefreshEndpointEvent.SERVICE_CENTER_NAME);
  }
}
