package io.github.zhenbing.frpc.config;

import io.github.zhenbing.frpc.AbstractConfigurationTest;
import io.github.zhenbing.frpc.annotation.FrpcConfigConfiguration;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * ApplicationConfigTest
 *
 * @author fengzhenbing
 */
public class FrpcConfigConfigurationTest extends AbstractConfigurationTest {

    @Test
    public void testFrpcConfigConfiguration() {
        final String name = "frpc-provider";
        final String[] inlinedProperties = new String[]{
                "frpc.application.name=" + name,
        };
        load(FrpcConfigConfiguration.class, inlinedProperties);
        ApplicationConfig properties = getContext().getBean(ApplicationConfig.class);
        assertThat(properties.getName(), is(name));
    }
}
