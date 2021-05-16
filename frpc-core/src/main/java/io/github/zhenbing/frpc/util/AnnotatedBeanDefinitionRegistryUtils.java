package io.github.zhenbing.frpc.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * Annotated {@link BeanDefinition} Utilities
 * <p>
 *
 * @author
 */
public abstract class AnnotatedBeanDefinitionRegistryUtils {

    private static final Log logger = LogFactory.getLog(AnnotatedBeanDefinitionRegistryUtils.class);

    /**
     * Is present bean that was registered by the specified {@link Annotation annotated} {@link Class class}
     *
     * @param registry       {@link BeanDefinitionRegistry}
     * @param annotatedClass the {@link Annotation annotated} {@link Class class}
     * @return if present, return <code>true</code>, or <code>false</code>
     * @since 2.7.3
     */
    public static boolean isPresentBean(BeanDefinitionRegistry registry, Class<?> annotatedClass) {

        boolean present = false;

        String[] beanNames = registry.getBeanDefinitionNames();

        ClassLoader classLoader = annotatedClass.getClassLoader();

        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
                String className = annotationMetadata.getClassName();
                Class<?> targetClass = resolveClassName(className, classLoader);
                present = Objects.equals(targetClass, annotatedClass);
                if (present) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(format("The annotatedClass[class : %s , bean name : %s] was present in registry[%s]",
                                className, beanName, registry));
                    }
                    break;
                }
            }
        }

        return present;
    }

    /**
     * Register Beans if not present in {@link BeanDefinitionRegistry registry}
     *
     * @param registry         {@link BeanDefinitionRegistry}
     * @param annotatedClasses {@link Annotation annotation} class
     * {@link #isPresentBean(BeanDefinitionRegistry, Class)}
     */
    public static void registerBeans(BeanDefinitionRegistry registry, Class<?>... annotatedClasses) {

        if (ObjectUtils.isEmpty(annotatedClasses)) {
            return;
        }

        // Remove all annotated-classes that have been registered
        Iterator<Class<?>> iterator = new ArrayList<>(asList(annotatedClasses)).iterator();

        while (iterator.hasNext()) {
            Class<?> annotatedClass = iterator.next();
            if (isPresentBean(registry, annotatedClass)) {
                iterator.remove();
            }
        }

        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(registry);

        if (logger.isDebugEnabled()) {
            logger.debug(registry.getClass().getSimpleName() + " will register annotated classes : " + asList(annotatedClasses) + " .");
        }

        reader.register(annotatedClasses);

    }
}
