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

package com.vaadin.terminal.gwt.client;

import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.AbstractJavaScriptExtension;
import com.vaadin.terminal.gwt.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.terminal.gwt.client.extensions.AbstractExtensionConnector;

@Connect(AbstractJavaScriptExtension.class)
public final class JavaScriptExtension extends AbstractExtensionConnector
        implements HasJavaScriptConnectorHelper {
    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this);

    @Override
    protected void init() {
        super.init();
        helper.init();
    }

    @Override
    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }

    @Override
    public JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }
}
