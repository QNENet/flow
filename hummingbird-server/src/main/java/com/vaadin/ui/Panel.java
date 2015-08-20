/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.ui;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.Scrollable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.panel.PanelServerRpc;
import com.vaadin.shared.ui.panel.PanelState;
import com.vaadin.ui.Component.Focusable;

/**
 * Panel - a simple single component container.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class Panel extends AbstractSingleComponentContainer
        implements Scrollable, Focusable {

    private PanelServerRpc rpc = new PanelServerRpc() {
        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new ClickEvent(Panel.this, mouseDetails));
        }
    };

    /**
     * Creates a new empty panel.
     */
    public Panel() {
        this((ComponentContainer) null);
    }

    /**
     * Creates a new empty panel which contains the given content.
     *
     * @param content
     *            the content for the panel.
     */
    public Panel(Component content) {
        registerRpc(rpc);
        setContent(content);
        setWidth(100, Unit.PERCENTAGE);
        getState().tabIndex = -1;
    }

    /**
     * Creates a new empty panel with caption.
     *
     * @param caption
     *            the caption used in the panel (HTML).
     */
    public Panel(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new empty panel with the given caption and content.
     *
     * @param caption
     *            the caption of the panel (HTML).
     * @param content
     *            the content used in the panel.
     */
    public Panel(String caption, Component content) {
        this(content);
        setCaption(caption);
    }

    /**
     * Sets the caption of the panel.
     *
     * Note that the caption is interpreted as HTML and therefore care should be
     * taken not to enable HTML injection and XSS attacks using panel captions.
     * This behavior may change in future versions.
     *
     * @see AbstractComponent#setCaption(String)
     */
    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
    }

    /* Scrolling functionality */

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Scrollable#setScrollable(boolean)
     */
    @Override
    public int getScrollLeft() {
        return getState(false).scrollLeft;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Scrollable#setScrollable(boolean)
     */
    @Override
    public int getScrollTop() {
        return getState(false).scrollTop;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Scrollable#setScrollLeft(int)
     */
    @Override
    public void setScrollLeft(int scrollLeft) {
        if (scrollLeft < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        getState().scrollLeft = scrollLeft;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.server.Scrollable#setScrollTop(int)
     */
    @Override
    public void setScrollTop(int scrollTop) {
        if (scrollTop < 0) {
            throw new IllegalArgumentException(
                    "Scroll offset must be at least 0");
        }
        getState().scrollTop = scrollTop;
    }

    /**
     * Add a click listener to the Panel. The listener is called whenever the
     * user clicks inside the Panel. Also when the click targets a component
     * inside the Panel, provided the targeted component does not prevent the
     * click event from propagating.
     *
     * Use {@link #removeListener(ClickListener)} to remove the listener.
     *
     * @param listener
     *            The listener to add
     */
    public void addClickListener(ClickListener listener) {
        addListener(ClickListener.class, listener);
    }

    /**
     * Remove a click listener from the Panel. The listener should earlier have
     * been added using {@link #addListener(ClickListener)}.
     *
     * @param listener
     *            The listener to remove
     */
    public void removeClickListener(ClickListener listener) {
        removeListener(ClickListener.class, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    /**
     * Moves keyboard focus to the component. {@see Focusable#focus()}
     *
     */
    @Override
    public void focus() {
        super.focus();
    }

    @Override
    protected PanelState getState() {
        return (PanelState) super.getState();
    }

    @Override
    protected PanelState getState(boolean markAsDirty) {
        return (PanelState) super.getState(markAsDirty);
    }

}
