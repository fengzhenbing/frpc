package io.github.zhenbing.frpc.util;

import org.springframework.core.env.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.unmodifiableMap;

/**
 * PropertySourcesUtils
 *
 * @author fengzhenbing
 */
public class PropertySourcesUtils {

    /**
     * Get prefixed {@link Properties}
     *
     * @param propertySources {@link PropertySource} Iterable
     * @param prefix          the prefix of property name
     * @return Map
     * @see Properties
     */
    public static Map<String, Object> getPrefixedProperties(Iterable<PropertySource<?>> propertySources, String prefix) {

        MutablePropertySources mutablePropertySources = new MutablePropertySources();

        for (PropertySource<?> source : propertySources) {
            mutablePropertySources.addLast(source);
        }

        return getPrefixedProperties(mutablePropertySources, prefix);

    }

    /**
     * Get prefixed {@link Properties}
     *
     * @param propertySources {@link PropertySources}
     * @param prefix          the prefix of property name
     * @return Map
     * @see Properties
     */
    public static Map<String, Object> getPrefixedProperties(PropertySources propertySources, String prefix) {

        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        Map<String, Object> prefixedProperties = new LinkedHashMap<>();

        String normalizedPrefix = buildPrefix(prefix);

        Iterator<PropertySource<?>> iterator = propertySources.iterator();

        while (iterator.hasNext()) {
            PropertySource<?> source = iterator.next();
            if (source instanceof EnumerablePropertySource) {
                for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                    if (!prefixedProperties.containsKey(name) && name.startsWith(normalizedPrefix)) {
                        String subName = name.substring(normalizedPrefix.length());
                        // take first one
                        if (!prefixedProperties.containsKey(subName)) {
                            Object value = source.getProperty(name);
                            if (value instanceof String) {
                                // Resolve placeholder
                                value = propertyResolver.resolvePlaceholders((String) value);
                            }
                            prefixedProperties.put(subName, value);
                        }
                    }
                }
            }
        }

        return unmodifiableMap(prefixedProperties);
    }

    /**
     * Build the prefix
     *
     * @param prefix the prefix
     * @return the prefix
     */
    public static String buildPrefix(String prefix) {
        if (prefix.endsWith(".")) {
            return prefix;
        } else {
            return prefix + ".";
        }
    }

}
