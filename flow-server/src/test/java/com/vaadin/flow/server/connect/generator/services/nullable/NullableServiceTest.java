/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.server.connect.generator.services.nullable;

import java.util.Collections;

import org.junit.Test;

import com.vaadin.flow.server.connect.generator.services.AbstractServiceGenerationTest;

public class NullableServiceTest extends AbstractServiceGenerationTest {

    public NullableServiceTest() {
        super(Collections.singletonList(NullableService.class));
    }

    @Test
    public void should_GenerateNotNull_when_NotNullAnnotationAreUsed() {
        verifyOpenApiObjectAndGeneratedTs();
    }
}
