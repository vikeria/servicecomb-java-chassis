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

package org.apache.servicecomb.common.rest.codec.param;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.servicecomb.common.rest.codec.RestClientRequest;
import org.apache.servicecomb.common.rest.codec.header.HeaderCodec;
import org.apache.servicecomb.common.rest.codec.header.HeaderCodecMulti;
import org.apache.servicecomb.common.rest.codec.header.HeaderCodecsUtils;
import org.apache.servicecomb.swagger.invocation.exception.InvocationException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.netflix.config.DynamicPropertyFactory;

import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;

public class HeaderProcessorCreator implements ParamValueProcessorCreator {
  public static final String PARAMTYPE = "header";

  public static class HeaderProcessor extends AbstractParamProcessor {
    // This configuration is used for temporary use only. Do not use it if you are sure how it works. And may be deleted in future.
    private final boolean ignoreRequiredCheck = DynamicPropertyFactory.getInstance()
        .getBooleanProperty("servicecomb.rest.parameter.header.ignoreRequiredCheck", false).get();

    private final HeaderCodec headerCodec;

    public HeaderProcessor(HeaderParameter headerParameter, JavaType targetType) {
      super(headerParameter.getName(), targetType, headerParameter.getDefaultValue(), headerParameter.getRequired());

      if ((ArrayProperty.isType(headerParameter.getType())) && headerParameter.getCollectionFormat() == null) {
        // compatible to default settings
        this.headerCodec = HeaderCodecsUtils.find(HeaderCodecMulti.NAME);
      } else {
        this.headerCodec = HeaderCodecsUtils.find(headerParameter.getCollectionFormat());
      }
    }

    @Override
    public Object getValue(HttpServletRequest request) {
      return headerCodec.decode(this, request);
    }

    public Object checkRequiredAndDefaultValue() {
      if (!ignoreRequiredCheck && isRequired()) {
        throw new InvocationException(Status.BAD_REQUEST, "Parameter is required.");
      }
      return getDefaultValue();
    }

    @Override
    public void setValue(RestClientRequest clientRequest, Object arg) throws Exception {
      headerCodec.encode(clientRequest, paramPath, arg);
    }

    @Override
    public String getProcessorType() {
      return PARAMTYPE;
    }
  }

  public HeaderProcessorCreator() {
    ParamValueProcessorCreatorManager.INSTANCE.register(PARAMTYPE, this);
  }

  @Override
  public ParamValueProcessor create(Parameter parameter, Type genericParamType) {
    JavaType targetType =
        genericParamType == null ? null : TypeFactory.defaultInstance().constructType(genericParamType);
    return new HeaderProcessor((HeaderParameter) parameter, targetType);
  }
}
