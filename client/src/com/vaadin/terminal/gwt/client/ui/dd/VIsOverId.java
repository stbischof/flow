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
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.ui.AbstractSelect;

@AcceptCriterion(AbstractSelect.TargetItemIs.class)
final public class VIsOverId extends VAcceptCriterion {

    @Override
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        try {

            String pid = configuration.getStringAttribute("s");
            VDropHandler currentDropHandler = VDragAndDropManager.get()
                    .getCurrentDropHandler();
            ComponentConnector dropHandlerConnector = currentDropHandler
                    .getConnector();
            ConnectorMap paintableMap = ConnectorMap.get(currentDropHandler
                    .getApplicationConnection());

            String pid2 = dropHandlerConnector.getConnectorId();
            if (pid2.equals(pid)) {
                Object searchedId = drag.getDropDetails().get("itemIdOver");
                String[] stringArrayAttribute = configuration
                        .getStringArrayAttribute("keys");
                for (String string : stringArrayAttribute) {
                    if (string.equals(searchedId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}