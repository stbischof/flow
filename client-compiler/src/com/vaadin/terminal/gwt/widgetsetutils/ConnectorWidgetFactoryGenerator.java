/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.widgetsetutils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * GWT generator that creates a Widget class for a given Connector class, based
 * on the return type of getWidget()
 * 
 * @since 7.0
 */
public class ConnectorWidgetFactoryGenerator extends
        AbstractConnectorClassBasedFactoryGenerator {
    @Override
    protected JClassType getTargetType(JClassType connectorType) {
        return getGetterReturnType(connectorType, "getWidget");
    }

    @Override
    protected Class<? extends ServerConnector> getConnectorType() {
        return ComponentConnector.class;
    }

}