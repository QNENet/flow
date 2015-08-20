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

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;

/**
 * This is a simple drop-down select without, for instance, support for
 * multiselect, new items, lazyloading, and other advanced features. Sometimes
 * "native" select without all the bells-and-whistles of the ComboBox is a
 * better choice.
 */
@SuppressWarnings("serial")
public class NativeSelect extends AbstractSelect
        implements FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(
            this) {

        @Override
        protected void fireEvent(Event event) {
            NativeSelect.this.fireEvent(event);
        }
    };

    public NativeSelect() {
        super();
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption, Collection<?> options) {
        super(caption, options);
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption) {
        super(caption);
        registerRpc(focusBlurRpc);
    }

    @Override
    public void setMultiSelect(boolean multiSelect)
            throws UnsupportedOperationException {
        if (multiSelect == true) {
            throw new UnsupportedOperationException(
                    "Multiselect not supported");
        }
    }

    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions == true) {
            throw new UnsupportedOperationException(
                    "newItemsAllowed not supported");
        }
    }

}
