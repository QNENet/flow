/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.flow.component;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.webcomponent.PropertyConfiguration;
import com.vaadin.flow.component.webcomponent.WebComponent;
import com.vaadin.flow.component.webcomponent.WebComponentConfiguration;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.webcomponent.PropertyConfigurationImpl;
import com.vaadin.flow.server.webcomponent.PropertyData;
import com.vaadin.flow.server.webcomponent.UnsupportedPropertyTypeException;
import com.vaadin.flow.server.webcomponent.WebComponentBinding;

import elemental.json.JsonValue;

/**
 * Provides a way to exporter a class which extends {@link Component} as an
 * embeddable web component. A non-null tag must be provided by calling {@code
 * super(String)} in the no-args constructor of the extending exporter.
 * Otherwise an exception will be throw during startup.
 * <p>
 * Limitations regarding the tag are:
 * <ul>
 * <li>The tag must be a non-null, non-empty string with dash-separated words,
 * i.e. "dash-separated".</li>
 * <li>Exporter cannot share the tag with the component being exported. If they
 * do, an exception will be thrown during run-time.</li>
 * </ul>
 * <p>
 * Example of exporting {@code MyComponent} component as an embeddable web
 * component:
 *
 * <pre>
 * &#064;Tag("my-component")
 * public class Exporter implements WebComponentExporter&lt;MyComponent&gt;() {
 *     public Exporter() {
 *         super("my-component");
 *         addProperty("name", "John Doe").onChange(MyComponent::setName);
 *     }
 *
 *     &#064;Override
 *     public void configureInstance(WebComponent&lt;MyComponent&gt;
 *              webComponent, MyComponent component) {
 *          // add e.g. a listener to the {@code component}
 *          // and do something with {@code webComponent}
 *     }
 * }
 * </pre>
 *
 * @param <C>
 *         type of the component to export
 * @author Vaadin Ltd.
 */
public abstract class WebComponentExporter<C extends Component>
        implements Serializable {

    private static final List<Class> SUPPORTED_TYPES =
            Collections.unmodifiableList(Arrays.asList(
                    Boolean.class, String.class, Integer.class, Double.class,
                    JsonValue.class));

    private final String tag;
    private final Class<C> componentClass;
    private HashMap<String,
            PropertyConfigurationImpl<C, ? extends Serializable>> propertyConfigurationMap = new HashMap<>();

    /**
     * Creates a new {@code WebComponentExporter} instance and configures the
     * tag name of the web component created based on this exporter.
     * <p>
     * This constructor is not meant to be overridden unless the {@code
     * exporter} can be extended. Rather, create a non-args constructor and call
     * this constructor from it.
     *
     * @param tag
     *         tag name of the web component created by the exporter, cannot be
     *         {@code null}
     */
    @SuppressWarnings("unchecked")
    protected WebComponentExporter(String tag) {
        if (tag == null) {
            throw new NullTagException("Parameter 'tag' must not be null!");
        }
        this.tag = tag;

        componentClass = (Class<C>) ReflectTools.getGenericInterfaceType(
                this.getClass(), WebComponentExporter.class);

        assert componentClass != null : "Failed to determine component class "
                + "from WebComponentExporter's type parameter.";
    }

    private <P extends Serializable> PropertyConfiguration<C, P> addProperty(
            String name, Class<P> type, P defaultValue) {
        Objects.requireNonNull(name, "Parameter 'name' cannot be null!");
        Objects.requireNonNull(type, "Parameter 'type' cannot be null!");

        if (!isSupportedType(type)) {
            throw new UnsupportedPropertyTypeException(String.format(
                    "PropertyConfiguration "
                            + "cannot handle type %s. Use any of %s instead.",
                    type.getCanonicalName(),
                    SUPPORTED_TYPES.stream().map(Class::getSimpleName)
                            .collect(Collectors.joining(", "))));
        }

        PropertyConfigurationImpl<C, P> propertyConfigurationImpl =
                new PropertyConfigurationImpl<>(
                        componentClass, name, type, defaultValue);

        propertyConfigurationMap.put(name, propertyConfigurationImpl);

        return propertyConfigurationImpl;
    }

    /**
     * Add an {@code Integer} property to the exported web component identified
     * by {@code name}.
     *
     * @param name
     *         name of the property. While all formats are allowed, names in
     *         camelCase will be converted to dash-separated form, when property
     *         update events are generated, using form "property-name-changed",
     *         if the property is called "propertyName"
     * @param defaultValue
     *         default value of property.
     * @return fluent {@code PropertyConfiguration} for configuring the
     *         property
     */
    public final PropertyConfiguration<C, Integer> addProperty(
            String name, int defaultValue) {
        return addProperty(name, Integer.class, defaultValue);
    }

    /**
     * Add an {@code Double} property to the exported web component identified
     * by {@code name}.
     *
     * @param name
     *         name of the property. While all formats are allowed, names in
     *         camelCase will be converted to dash-separated form, when property
     *         update events are generated, using form "property-name-changed",
     *         if the property is called "propertyName"
     * @param defaultValue
     *         default value of property.
     * @return fluent {@code PropertyConfiguration} for configuring the
     *         property
     */
    public final PropertyConfiguration<C, Double> addProperty(
            String name, double defaultValue) {
        return addProperty(name, Double.class, defaultValue);
    }

    /**
     * Add an {@code String} property to the exported web component identified
     * by {@code name}.
     *
     * @param name
     *         name of the property. While all formats are allowed, names in
     *         camelCase will be converted to dash-separated form, when property
     *         update events are generated, using form "property-name-changed",
     *         if the property is called "propertyName"
     * @param defaultValue
     *         default value of property.
     * @return fluent {@code PropertyConfiguration} for configuring the
     *         property
     */
    public final PropertyConfiguration<C, String> addProperty(
            String name, String defaultValue) {
        return addProperty(name, String.class, defaultValue);
    }

    /**
     * Add an {@code Boolean} property to the exported web component identified
     * by {@code name}.
     *
     * @param name
     *         name of the property. While all formats are allowed, names in
     *         camelCase will be converted to dash-separated form, when property
     *         update events are generated, using form "property-name-changed",
     *         if the property is called "propertyName"
     * @param defaultValue
     *         default value of property.
     * @return fluent {@code PropertyConfiguration} for configuring the
     *         property
     */
    public final PropertyConfiguration<C, Boolean> addProperty(
            String name, boolean defaultValue) {
        return addProperty(name, Boolean.class, defaultValue);
    }

    /**
     * Add an {@code JsonValue} property to the exported web component
     * identified by {@code name}.
     *
     * @param name
     *         name of the property. While all formats are allowed, names in
     *         camelCase will be converted to dash-separated form, when property
     *         update events are generated, using form "property-name-changed",
     *         if the property is called "propertyName"
     * @param defaultValue
     *         default value of property.
     * @return fluent {@code PropertyConfiguration} for configuring the
     *         property
     */
    public final PropertyConfiguration<C, JsonValue> addProperty(
            String name, JsonValue defaultValue) {
        return addProperty(name, JsonValue.class, defaultValue);
    }

    /**
     * If custom initialization for the created {@link Component} instance is
     * needed, it can be done here. It is also possible to configure custom
     * communication between the {@code component} instance and client-side web
     * component using the {@link WebComponent} instance. The {@code
     * webComponent} and {@code component} are in 1-to-1 relation.
     *
     * @param webComponent
     *         instance representing the client-side web component instance
     *         matching the component
     * @param component
     *         instance of the exported web component
     */
    public abstract void configureInstance(
            WebComponent<C> webComponent, C component);

    private static boolean isSupportedType(Class clazz) {
        return SUPPORTED_TYPES.contains(clazz);
    }

    private final static class WebComponentConfigurationImpl<C extends Component> implements WebComponentConfiguration<C> {
        private WebComponentExporter<C> exporter;
        private final Map<String, PropertyConfigurationImpl<C,
                ? extends Serializable>> immutablePropertyMap;

        private WebComponentConfigurationImpl(WebComponentExporter<C> exporter) {
            this.exporter = exporter;
            immutablePropertyMap =
                    Collections.unmodifiableMap(exporter.propertyConfigurationMap);
        }

        @Override
        public boolean hasProperty(String propertyName) {
            return immutablePropertyMap.containsKey(propertyName);
        }

        @Override
        public Class<? extends Serializable> getPropertyType(String propertyName) {
            if (hasProperty(propertyName)) {
                return immutablePropertyMap.get(propertyName).getPropertyData()
                        .getType();
            } else {
                return null;
            }
        }

        @Override
        public Class<C> getComponentClass() {
            return this.exporter.componentClass;
        }

        @Override
        public Set<PropertyData<? extends Serializable>> getPropertyDataSet() {
            return immutablePropertyMap.values().stream()
                    .map(PropertyConfigurationImpl::getPropertyData)
                    .collect(Collectors.toSet());
        }

        @Override
        public WebComponentBinding<C> createWebComponentBinding(Instantiator instantiator, Element element) {
            assert (instantiator != null);

            final C componentReference =
                    instantiator.createComponent(this.getComponentClass());

            if (componentReference == null) {
                throw new RuntimeException("Failed to instantiate a new "
                        + this.getComponentClass().getCanonicalName());
            }

            /*
             * The tag check cannot be done before the creation of the Component
             * being exported. This is due to the unavailability of
             * Instantiator before VaadinService has been initialized (which
             * happens after collecting all the exporters).
             */
            String componentTag = componentReference.getElement().getTag();
            if (this.exporter.tag.equals(componentTag)) {
                throw new IllegalStateException(String.format(
                        "WebComponentExporter '%s' cannot share a tag with the "
                                + "%s instance being exported! Change the tag "
                                + "from '%s' to something else.",
                        this.getClass().getCanonicalName(),
                        componentReference.getClass().getCanonicalName(),
                        this.exporter.tag));
            }

            WebComponentBinding<C> binding =
                    new WebComponentBinding<>(componentReference);

            immutablePropertyMap
                    .values().forEach(binding::bindProperty);

            this.exporter.configureInstance(new WebComponent<>(binding, element),
                    binding.getComponent());

            binding.updatePropertiesToComponent();

            return binding;
        }

        @Override
        public String getTag() {
            return this.exporter.tag;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends WebComponentExporter<C>> getExporterClass() {
            return (Class<? extends WebComponentExporter<C>>) exporter.getClass();
        }

        @Override
        public int hashCode() {
            Object[] objs = new Object[immutablePropertyMap.size() + 1];

            objs[0] = getTag();
            int place = 1;
            for (PropertyConfiguration configuration :
                    immutablePropertyMap.values()) {
                objs[place] = configuration;
                place++;
            }

            return Objects.hash(objs);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WebComponentConfigurationImpl) {

                WebComponentConfigurationImpl<?> other =
                        (WebComponentConfigurationImpl<?>) obj;

                boolean isSame = getTag().equals(other.getTag());
                isSame = isSame && (immutablePropertyMap.size()
                        == other.immutablePropertyMap.size());

                if (!isSame) {
                    return false;
                }

                PropertyConfiguration<?, ?> otherConf;
                for (String key : immutablePropertyMap.keySet()) {
                    otherConf = other.immutablePropertyMap.get(key);
                    if (!immutablePropertyMap.get(key).equals(otherConf)) {
                        return false;
                    }
                }
                return true;
            }

            return false;
        }
    }

    /**
     * Produces {@link WebComponentConfiguration} instances from either {@link
     * WebComponentExporter} classes or instances.
     *
     * @author Vaadin Ltd
     */
    public static final class WebComponentConfigurationFactory implements Serializable {

        /**
         * Creates a {@link WebComponentConfiguration} from the provided {@link
         * WebComponentExporter} class.
         *
         * @param clazz
         *         exporter class, not {@code null}
         * @return a web component configuration matching the instance of
         *         received {@code clazz}
         * @throws NullPointerException
         *         when {@code clazz} is {@code null}
         */
        public WebComponentConfiguration<? extends Component> create(Class<?
                extends WebComponentExporter<? extends Component>> clazz) {
            Objects.requireNonNull(clazz, "Parameter 'clazz' cannot be null!");
            WebComponentExporter<? extends Component> exporter;
            try {
                exporter = ReflectTools
                        .createInstance(clazz);
            } catch (IllegalArgumentException e) {
                if (e.getCause() != null && e.getCause().getClass().equals(InvocationTargetException.class)) {
                    Throwable cause2 = e.getCause().getCause();
                    if (cause2 != null && cause2.getClass().equals(NullTagException.class)) {
                        throw new IllegalArgumentException(
                                String.format("Unable to construct %s! Did " +
                                                "'%s' give null value to " +
                                                "super(String) constructor?",
                                        WebComponentConfiguration.class.getSimpleName(),
                                        clazz.getCanonicalName()), e);
                    }
                }
                // unknown reason, cannot add information
                throw e;
            }
            return create(exporter);
        }

        /**
         * Creates a {@link WebComponentConfiguration} for the provided {@link
         * WebComponentExporter} instances.
         *
         * @param exporter
         *         exporter instance, not {@code null}
         * @return a web component configuration matching the instance of
         *         received {@code exporter}
         * @throws NullPointerException
         *         when {@code exporter} is {@code null}
         */
        public WebComponentConfiguration<? extends Component> create(WebComponentExporter<?
                extends Component> exporter) {
            Objects.requireNonNull(exporter, "Parameter 'exporter' cannot be " +
                    "null!");

            return new WebComponentConfigurationImpl<>(exporter);
        }
    }

    private static class NullTagException extends NullPointerException {
        NullTagException(String msg) {
            super(msg);
        }
    }
}
