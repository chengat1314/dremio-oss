/*
 * Copyright (C) 2017-2018 Dremio Corporation
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

import java.lang.Override;

<@pp.dropOutputFile />
<#list vv.types as type>
<#list type.minor as minor>
<#assign typeMapping = TypeMappings[minor.class]!{}>
<#assign supported = typeMapping.supported!true>
<#assign dremioMinorType = typeMapping.minor_type!minor.class?upper_case>
<#if supported>
<#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />

<#if type.major == "Fixed">
<@pp.changeOutputFile name="/org/apache/arrow/vector/${minor.class}VectorHelper.java" />
<#include "/@includes/license.ftl" />

package org.apache.arrow.vector;

<#include "/@includes/vv_imports.ftl" />

/**
 * generated from ${.template_name}
 */
public final class ${minor.class}VectorHelper extends BaseValueVectorHelper {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(${minor.class}VectorHelper.class);

  private ${minor.class}Vector vector;

  public ${minor.class}VectorHelper(${minor.class}Vector vector) {
    super(vector);
    this.vector = vector;
  }

  public void load(SerializedField metadata, ArrowBuf buffer) {
    Preconditions.checkArgument(vector.name.equals(metadata.getNamePart().getName()), "The field %s doesn't match the provided metadata %s.", vector.name, metadata);
    final int actualLength = metadata.getBufferLength();
    final int valueCount = metadata.getValueCount();
    final int expectedLength = valueCount * ${type.width};
    assert actualLength == expectedLength : String.format("Expected to load %d bytes but actually loaded %d bytes", expectedLength, actualLength);

    vector.clear();
    if (vector.data != null) {
      vector.data.release(1);
    }
    vector.data = buffer.slice(0, actualLength);
    vector.data.retain(1);
    vector.data.writerIndex(actualLength);
  }

  @Override
  public SerializedField.Builder getMetadataBuilder() {
    SerializedField.Builder builder = super.getMetadataBuilder();
    return builder.setMajorType(com.dremio.common.types.Types.required(com.dremio.common.types.TypeProtos.MinorType.${dremioMinorType}));
  }
}

</#if>
</#if>
</#list>
</#list>
