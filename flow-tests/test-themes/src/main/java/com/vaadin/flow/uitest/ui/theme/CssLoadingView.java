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
package com.vaadin.flow.uitest.ui.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;

/**
 * A view for testing for CSS loading
 *
 * @author Vaadin Ltd
 */
@Route("com.vaadin.flow.uitest.ui.theme.CssLoadingView")
@CssImport("./src/css-loading-view/css-loading-view.css")
@StyleSheet("context://styles/css-loading-view-style.css")
public class CssLoadingView extends Div {

    public static final String CSS_IMPORT__STYLESHEET = "css-import-stylesheet";
    public static final String CSS_IMPORT__PAGE_ADD_STYLESHEET = "css-import-page-stylesheet";
    public static final String PAGE_ADD_STYLESHEET__STYLESHEET = "page-stylesheet-stylesheet";

    public CssLoadingView() {
        Paragraph p1 = new Paragraph("This is paragraph 1.");
        p1.setId(CSS_IMPORT__STYLESHEET);
        p1.addClassNames("css-import", "stylesheet");

        Paragraph p2 = new Paragraph("This is paragraph 2.");
        p2.setId(CSS_IMPORT__PAGE_ADD_STYLESHEET);
        p2.addClassNames("css-import", "page-style");

        Paragraph p3 = new Paragraph("This is paragraph 3.");
        p3.setId(PAGE_ADD_STYLESHEET__STYLESHEET);
        p3.addClassNames("page-style", "stylesheet");

        add(p1, p2, p3);

        UI.getCurrent().getPage().addStyleSheet(
                "/styles/css-loading-view-page-style.css", LoadMode.INLINE);
    }
}
