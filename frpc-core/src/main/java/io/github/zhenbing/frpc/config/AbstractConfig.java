package io.github.zhenbing.frpc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AbstractConfig
 *
 * @author fengzhenbing
 */
@Slf4j
public abstract class AbstractConfig implements Serializable {
    private static final long serialVersionUID = 4267533505537413571L;

    public static final String FRPC = "frpc";
    /**
     * The maximum length of a <b>parameter's value</b>
     */
    private static final int MAX_LENGTH = 200;


    protected static void checkLength(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, null);
    }

    protected static void checkProperty(String property, String value, int maxlength, Pattern pattern) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (value.length() > maxlength) {
            throw new IllegalStateException("Invalid " + property + "=\"" + value + "\" is longer than " + maxlength);
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new IllegalStateException("Invalid " + property + "=\"" + value + "\" contains illegal " +
                        "character, only digit, letter, '-', '_' or '.' is legal.");
            }
        }
    }


    public void refresh() {
        //todo
    }

    public String getFullConfigPrefix() {
        return FRPC + "." + getConfigPrefix();
    }

    public abstract String getConfigPrefix();

    public void bindProperties(AbstractConfig frpcConfig, Map<String, Object> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }

        properties.entrySet().forEach((e) -> {
            setField(frpcConfig, e.getKey(), e.getValue());
        });

    }

    private void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            FieldAccessor fieldAccessor = ReflectionFactory.getReflectionFactory().newFieldAccessor(field, true);
            fieldAccessor.set(target, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("properties are not valid , target -> {}, value -> {}", target, value);
        }

    }

}
