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

package com.vaadin.terminal;

import java.io.IOException;
import java.io.Serializable;

import com.vaadin.Application;

/**
 * Handler for producing a response to non-UIDL requests. Handlers can be added
 * to applications using {@link Application#addRequestHandler(RequestHandler)}
 */
public interface RequestHandler extends Serializable {

    /**
     * Handles a non-UIDL request. If a response is written, this method should
     * return <code>false</code> to indicate that no more request handlers
     * should be invoked for the request.
     * 
     * @param application
     *            The application to which the request belongs
     * @param request
     *            The request to handle
     * @param response
     *            The response object to which a response can be written.
     * @return true if a response has been written and no further request
     *         handlers should be called, otherwise false
     * @throws IOException
     */
    boolean handleRequest(Application application, WrappedRequest request,
            WrappedResponse response) throws IOException;

}
