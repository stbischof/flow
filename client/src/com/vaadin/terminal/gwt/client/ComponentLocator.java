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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.terminal.gwt.client.ui.SubPartAware;
import com.vaadin.terminal.gwt.client.ui.gridlayout.VGridLayout;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.VMeasuringOrderedLayout;
import com.vaadin.terminal.gwt.client.ui.root.VRoot;
import com.vaadin.terminal.gwt.client.ui.tabsheet.VTabsheetPanel;
import com.vaadin.terminal.gwt.client.ui.window.VWindow;
import com.vaadin.terminal.gwt.client.ui.window.WindowConnector;

/**
 * ComponentLocator provides methods for generating a String locator for a given
 * DOM element and for locating a DOM element using a String locator.
 */
public class ComponentLocator {

    /**
     * Separator used in the String locator between a parent and a child widget.
     */
    private static final String PARENTCHILD_SEPARATOR = "/";

    /**
     * Separator used in the String locator between the part identifying the
     * containing widget and the part identifying the target element within the
     * widget.
     */
    private static final String SUBPART_SEPARATOR = "#";

    /**
     * String that identifies the root panel when appearing first in the String
     * locator.
     */
    private static final String ROOT_ID = "Root";

    /**
     * Reference to ApplicationConnection instance.
     */
    private ApplicationConnection client;

    /**
     * Construct a ComponentLocator for the given ApplicationConnection.
     * 
     * @param client
     *            ApplicationConnection instance for the application.
     */
    public ComponentLocator(ApplicationConnection client) {
        this.client = client;
    }

    /**
     * Generates a String locator which uniquely identifies the target element.
     * The {@link #getElementByPath(String)} method can be used for the inverse
     * operation, i.e. locating an element based on the return value from this
     * method.
     * <p>
     * Note that getElementByPath(getPathForElement(element)) == element is not
     * always true as {@link #getPathForElement(Element)} can return a path to
     * another element if the widget determines an action on the other element
     * will give the same result as the action on the target element.
     * </p>
     * 
     * @since 5.4
     * @param targetElement
     *            The element to generate a path for.
     * @return A String locator that identifies the target element or null if a
     *         String locator could not be created.
     */
    public String getPathForElement(Element targetElement) {
        String pid = null;

        Element e = targetElement;

        while (true) {
            pid = ConnectorMap.get(client).getConnectorId(e);
            if (pid != null) {
                break;
            }

            e = DOM.getParent(e);
            if (e == null) {
                break;
            }
        }

        Widget w = null;
        if (pid != null) {
            // If we found a Paintable then we use that as reference. We should
            // find the Paintable for all but very special cases (like
            // overlays).
            w = ((ComponentConnector) ConnectorMap.get(client)
                    .getConnector(pid)).getWidget();

            /*
             * Still if the Paintable contains a widget that implements
             * SubPartAware, we want to use that as a reference
             */
            Widget targetParent = findParentWidget(targetElement, w);
            while (targetParent != w && targetParent != null) {
                if (targetParent instanceof SubPartAware) {
                    /*
                     * The targetParent widget is a child of the Paintable and
                     * the first parent (of the targetElement) that implements
                     * SubPartAware
                     */
                    w = targetParent;
                    break;
                }
                targetParent = targetParent.getParent();
            }
        }
        if (w == null) {
            // Check if the element is part of a widget that is attached
            // directly to the root panel
            RootPanel rootPanel = RootPanel.get();
            int rootWidgetCount = rootPanel.getWidgetCount();
            for (int i = 0; i < rootWidgetCount; i++) {
                Widget rootWidget = rootPanel.getWidget(i);
                if (rootWidget.getElement().isOrHasChild(targetElement)) {
                    // The target element is contained by this root widget
                    w = findParentWidget(targetElement, rootWidget);
                    break;
                }
            }
            if (w != null) {
                // We found a widget but we should still see if we find a
                // SubPartAware implementor (we cannot find the Paintable as
                // there is no link from VOverlay to its paintable/owner).
                Widget subPartAwareWidget = findSubPartAwareParentWidget(w);
                if (subPartAwareWidget != null) {
                    w = subPartAwareWidget;
                }
            }
        }

        if (w == null) {
            // Containing widget not found
            return null;
        }

        // Determine the path for the target widget
        String path = getPathForWidget(w);
        if (path == null) {
            /*
             * No path could be determined for the target widget. Cannot create
             * a locator string.
             */
            return null;
        }

        if (w.getElement() == targetElement) {
            /*
             * We are done if the target element is the root of the target
             * widget.
             */
            return path;
        } else if (w instanceof SubPartAware) {
            /*
             * If the widget can provide an identifier for the targetElement we
             * let it do that
             */
            String elementLocator = ((SubPartAware) w)
                    .getSubPartName(targetElement);
            if (elementLocator != null) {
                return path + SUBPART_SEPARATOR + elementLocator;
            }
        }
        /*
         * If everything else fails we use the DOM path to identify the target
         * element
         */
        return path + getDOMPathForElement(targetElement, w.getElement());
    }

    /**
     * Finds the first widget in the hierarchy (moving upwards) that implements
     * SubPartAware. Returns the SubPartAware implementor or null if none is
     * found.
     * 
     * @param w
     *            The widget to start from. This is returned if it implements
     *            SubPartAware.
     * @return The first widget (upwards in hierarchy) that implements
     *         SubPartAware or null
     */
    private Widget findSubPartAwareParentWidget(Widget w) {

        while (w != null) {
            if (w instanceof SubPartAware) {
                return w;
            }
            w = w.getParent();
        }
        return null;
    }

    /**
     * Returns the first widget found when going from {@code targetElement}
     * upwards in the DOM hierarchy, assuming that {@code ancestorWidget} is a
     * parent of {@code targetElement}.
     * 
     * @param targetElement
     * @param ancestorWidget
     * @return The widget whose root element is a parent of
     *         {@code targetElement}.
     */
    private Widget findParentWidget(Element targetElement, Widget ancestorWidget) {
        /*
         * As we cannot resolve Widgets from the element we start from the
         * widget and move downwards to the correct child widget, as long as we
         * find one.
         */
        if (ancestorWidget instanceof HasWidgets) {
            for (Widget w : ((HasWidgets) ancestorWidget)) {
                if (w.getElement().isOrHasChild(targetElement)) {
                    return findParentWidget(targetElement, w);
                }
            }
        }

        // No children found, this is it
        return ancestorWidget;
    }

    /**
     * Locates an element based on a DOM path and a base element.
     * 
     * @param baseElement
     *            The base element which the path is relative to
     * @param path
     *            String locator (consisting of domChild[x] parts) that
     *            identifies the element
     * @return The element identified by path, relative to baseElement or null
     *         if the element could not be found.
     */
    private Element getElementByDOMPath(Element baseElement, String path) {
        String parts[] = path.split(PARENTCHILD_SEPARATOR);
        Element element = baseElement;

        for (String part : parts) {
            if (part.startsWith("domChild[")) {
                String childIndexString = part.substring("domChild[".length(),
                        part.length() - 1);
                try {
                    int childIndex = Integer.parseInt(childIndexString);
                    element = DOM.getChild(element, childIndex);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return element;
    }

    /**
     * Generates a String locator using domChild[x] parts for the element
     * relative to the baseElement.
     * 
     * @param element
     *            The target element
     * @param baseElement
     *            The starting point for the locator. The generated path is
     *            relative to this element.
     * @return A String locator that can be used to locate the target element
     *         using {@link #getElementByDOMPath(Element, String)} or null if
     *         the locator String cannot be created.
     */
    private String getDOMPathForElement(Element element, Element baseElement) {
        Element e = element;
        String path = "";
        while (true) {
            Element parent = DOM.getParent(e);
            if (parent == null) {
                return null;
            }

            int childIndex = -1;

            int childCount = DOM.getChildCount(parent);
            for (int i = 0; i < childCount; i++) {
                if (e == DOM.getChild(parent, i)) {
                    childIndex = i;
                    break;
                }
            }
            if (childIndex == -1) {
                return null;
            }

            path = PARENTCHILD_SEPARATOR + "domChild[" + childIndex + "]"
                    + path;

            if (parent == baseElement) {
                break;
            }

            e = parent;
        }

        return path;
    }

    /**
     * Locates an element using a String locator (path) which identifies a DOM
     * element. The {@link #getPathForElement(Element)} method can be used for
     * the inverse operation, i.e. generating a string expression for a DOM
     * element.
     * 
     * @since 5.4
     * @param path
     *            The String locater which identifies the target element.
     * @return The DOM element identified by {@code path} or null if the element
     *         could not be located.
     */
    public Element getElementByPath(String path) {
        /*
         * Path is of type "targetWidgetPath#componentPart" or
         * "targetWidgetPath".
         */
        String parts[] = path.split(SUBPART_SEPARATOR, 2);
        String widgetPath = parts[0];
        Widget w = getWidgetFromPath(widgetPath);
        if (w == null || !Util.isAttachedAndDisplayed(w)) {
            return null;
        }

        if (parts.length == 1) {
            int pos = widgetPath.indexOf("domChild");
            if (pos == -1) {
                return w.getElement();
            }

            // Contains dom reference to a sub element of the widget
            String subPath = widgetPath.substring(pos);
            return getElementByDOMPath(w.getElement(), subPath);
        } else if (parts.length == 2) {
            if (w instanceof SubPartAware) {
                return ((SubPartAware) w).getSubPartElement(parts[1]);
            }
        }

        return null;
    }

    /**
     * Creates a locator String for the given widget. The path can be used to
     * locate the widget using {@link #getWidgetFromPath(String)}.
     * 
     * Returns null if no path can be determined for the widget or if the widget
     * is null.
     * 
     * @param w
     *            The target widget
     * @return A String locator for the widget
     */
    private String getPathForWidget(Widget w) {
        if (w == null) {
            return null;
        }

        if (w instanceof VRoot) {
            return "";
        } else if (w instanceof VWindow) {
            Connector windowConnector = ConnectorMap.get(client)
                    .getConnector(w);
            List<WindowConnector> subWindowList = client.getRootConnector()
                    .getSubWindows();
            int indexOfSubWindow = subWindowList.indexOf(windowConnector);
            return PARENTCHILD_SEPARATOR + "VWindow[" + indexOfSubWindow + "]";
        } else if (w instanceof RootPanel) {
            return ROOT_ID;
        }

        Widget parent = w.getParent();

        String basePath = getPathForWidget(parent);
        if (basePath == null) {
            return null;
        }
        String simpleName = Util.getSimpleName(w);

        /*
         * Check if the parent implements Iterable. At least VPopupView does not
         * implement HasWdgets so we cannot check for that.
         */
        if (!(parent instanceof Iterable<?>)) {
            // Parent does not implement Iterable so we cannot find out which
            // child this is
            return null;
        }

        Iterator<?> i = ((Iterable<?>) parent).iterator();
        int pos = 0;
        while (i.hasNext()) {
            Object child = i.next();
            if (child == w) {
                return basePath + PARENTCHILD_SEPARATOR + simpleName + "["
                        + pos + "]";
            }
            String simpleName2 = Util.getSimpleName(child);
            if (simpleName.equals(simpleName2)) {
                pos++;
            }
        }

        return null;
    }

    /**
     * Locates the widget based on a String locator.
     * 
     * @param path
     *            The String locator that identifies the widget.
     * @return The Widget identified by the String locator or null if the widget
     *         could not be identified.
     */
    private Widget getWidgetFromPath(String path) {
        Widget w = null;
        String parts[] = path.split(PARENTCHILD_SEPARATOR);

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.equals(ROOT_ID)) {
                w = RootPanel.get();
            } else if (part.equals("")) {
                w = client.getRootConnector().getWidget();
            } else if (w == null) {
                String id = part;
                // Must be old static pid (PID_S*)
                ServerConnector connector = ConnectorMap.get(client)
                        .getConnector(id);
                if (connector == null) {
                    // Lookup by debugId
                    // TODO Optimize this
                    connector = findConnectorById(client.getRootConnector(),
                            id.substring(5));
                }

                if (connector instanceof ComponentConnector) {
                    w = ((ComponentConnector) connector).getWidget();
                } else {
                    // Not found
                    return null;
                }
            } else if (part.startsWith("domChild[")) {
                // The target widget has been found and the rest identifies the
                // element
                break;
            } else if (w instanceof Iterable) {
                // W identifies a widget that contains other widgets, as it
                // should. Try to locate the child
                Iterable<?> parent = (Iterable<?>) w;

                // Part is of type "VVerticalLayout[0]", split this into
                // VVerticalLayout and 0
                String[] split = part.split("\\[", 2);
                String widgetClassName = split[0];
                String indexString = split[1];
                int widgetPosition = Integer.parseInt(indexString.substring(0,
                        indexString.length() - 1));

                // AbsolutePanel in GridLayout has been removed -> skip it
                if (w instanceof VGridLayout
                        && "AbsolutePanel".equals(widgetClassName)) {
                    continue;
                }

                if (w instanceof VTabsheetPanel && widgetPosition != 0) {
                    // TabSheetPanel now only contains 1 connector => the index
                    // is always 0 which indicates the widget in the active tab
                    widgetPosition = 0;
                }

                /*
                 * The new grid and ordered layotus do not contain
                 * ChildComponentContainer widgets. This is instead simulated by
                 * constructing a path step that would find the desired widget
                 * from the layout and injecting it as the next search step
                 * (which would originally have found the widget inside the
                 * ChildComponentContainer)
                 */
                if ((w instanceof VMeasuringOrderedLayout || w instanceof VGridLayout)
                        && "ChildComponentContainer".equals(widgetClassName)
                        && i + 1 < parts.length) {

                    HasWidgets layout = (HasWidgets) w;

                    String nextPart = parts[i + 1];
                    String[] nextSplit = nextPart.split("\\[", 2);
                    String nextWidgetClassName = nextSplit[0];

                    // Find the n:th child and count the number of children with
                    // the same type before it
                    int nextIndex = 0;
                    for (Widget child : layout) {
                        boolean matchingType = nextWidgetClassName.equals(Util
                                .getSimpleName(child));
                        if (matchingType && widgetPosition == 0) {
                            // This is the n:th child that we looked for
                            break;
                        } else if (widgetPosition < 0) {
                            // Error if we're past the desired position without
                            // a match
                            return null;
                        } else if (matchingType) {
                            // If this was another child of the expected type,
                            // increase the count for the next step
                            nextIndex++;
                        }

                        // Don't count captions
                        if (!(child instanceof VCaption)) {
                            widgetPosition--;
                        }
                    }

                    // Advance to the next step, this time checking for the
                    // actual child widget
                    parts[i + 1] = nextWidgetClassName + '[' + nextIndex + ']';
                    continue;
                }

                // Locate the child
                Iterator<? extends Widget> iterator;

                /*
                 * VWindow and VContextMenu workarounds for backwards
                 * compatibility
                 */
                if (widgetClassName.equals("VWindow")) {
                    List<WindowConnector> windows = client.getRootConnector()
                            .getSubWindows();
                    List<VWindow> windowWidgets = new ArrayList<VWindow>(
                            windows.size());
                    for (WindowConnector wc : windows) {
                        windowWidgets.add(wc.getWidget());
                    }
                    iterator = windowWidgets.iterator();
                } else if (widgetClassName.equals("VContextMenu")) {
                    return client.getContextMenu();
                } else {
                    iterator = (Iterator<? extends Widget>) parent.iterator();
                }

                boolean ok = false;

                // Find the widgetPosition:th child of type "widgetClassName"
                while (iterator.hasNext()) {

                    Widget child = iterator.next();
                    String simpleName2 = Util.getSimpleName(child);

                    if (widgetClassName.equals(simpleName2)) {
                        if (widgetPosition == 0) {
                            w = child;
                            ok = true;
                            break;
                        }
                        widgetPosition--;
                    }
                }

                if (!ok) {
                    // Did not find the child
                    return null;
                }
            } else {
                // W identifies something that is not a "HasWidgets". This
                // should not happen as all widget containers should implement
                // HasWidgets.
                return null;
            }
        }

        return w;
    }

    private ServerConnector findConnectorById(ServerConnector root, String id) {
        SharedState state = root.getState();
        if (state instanceof ComponentState
                && id.equals(((ComponentState) state).getDebugId())) {
            return root;
        }
        for (ServerConnector child : root.getChildren()) {
            ServerConnector found = findConnectorById(child, id);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

}
