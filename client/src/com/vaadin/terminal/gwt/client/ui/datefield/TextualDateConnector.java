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

package com.vaadin.terminal.gwt.client.ui.datefield;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class TextualDateConnector extends AbstractDateFieldConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        int origRes = getWidget().currentResolution;
        String oldLocale = getWidget().currentLocale;
        super.updateFromUIDL(uidl, client);
        if (origRes != getWidget().currentResolution
                || oldLocale != getWidget().currentLocale) {
            // force recreating format string
            getWidget().formatStr = null;
        }
        if (uidl.hasAttribute("format")) {
            getWidget().formatStr = uidl.getStringAttribute("format");
        }

        getWidget().inputPrompt = uidl
                .getStringAttribute(VTextualDate.ATTR_INPUTPROMPT);

        getWidget().lenient = !uidl.getBooleanAttribute("strict");

        getWidget().buildDate();
        // not a FocusWidget -> needs own tabindex handling
        if (uidl.hasAttribute("tabindex")) {
            getWidget().text.setTabIndex(uidl.getIntAttribute("tabindex"));
        }

        if (getWidget().readonly) {
            getWidget().text.addStyleDependentName("readonly");
        } else {
            getWidget().text.removeStyleDependentName("readonly");
        }

    }

    @Override
    public VTextualDate getWidget() {
        return (VTextualDate) super.getWidget();
    }
}
