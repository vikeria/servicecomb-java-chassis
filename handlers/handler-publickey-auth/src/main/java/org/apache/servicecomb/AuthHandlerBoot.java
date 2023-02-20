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
package org.apache.servicecomb;

import org.apache.servicecomb.core.BootListener;
import org.apache.servicecomb.foundation.common.utils.KeyPairEntry;
import org.apache.servicecomb.foundation.common.utils.KeyPairUtils;
import org.apache.servicecomb.foundation.token.Keypair4Auth;
import org.apache.servicecomb.registry.RegistrationManager;
import org.apache.servicecomb.registry.definition.DefinitionConst;
import org.springframework.stereotype.Component;

/**
 *
 * initialize public and private key pair when system boot before registry instance to service center
 *
 *
 */
@Component
public class AuthHandlerBoot implements BootListener {


  @Override
  public void onBootEvent(BootEvent event) {
    if (!EventType.BEFORE_REGISTRY.equals(event.getEventType())) {
      return;
    }
    KeyPairEntry keyPairEntry = KeyPairUtils.generateRSAKeyPair();
    Keypair4Auth.INSTANCE.setPrivateKey(keyPairEntry.getPrivateKey());
    Keypair4Auth.INSTANCE.setPublicKey(keyPairEntry.getPublicKey());
    Keypair4Auth.INSTANCE.setPublicKeyEncoded(keyPairEntry.getPublicKeyEncoded());
    RegistrationManager.INSTANCE.getMicroserviceInstance().getProperties().put(DefinitionConst.INSTANCE_PUBKEY_PRO,
        keyPairEntry.getPublicKeyEncoded());
  }
}
