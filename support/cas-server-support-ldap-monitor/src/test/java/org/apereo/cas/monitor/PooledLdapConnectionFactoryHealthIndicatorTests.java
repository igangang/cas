package org.apereo.cas.monitor;

import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.LdapMonitorConfiguration;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link PooledLdapConnectionFactoryHealthIndicator} class.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@SpringBootTest(classes = {
    LdapMonitorConfiguration.class,
    CasCoreUtilConfiguration.class,
    RefreshAutoConfiguration.class
},
    properties = {
        "cas.monitor.ldap[0].ldap-url=ldap://localhost:10389",
        "cas.monitor.ldap[0].name=LDAP"
    })
@Tag("Ldap")
@EnabledIfListeningOnPort(port = 10389)
class PooledLdapConnectionFactoryHealthIndicatorTests {
    @Autowired
    @Qualifier("pooledLdapConnectionFactoryHealthIndicator")
    private CompositeHealthContributor monitor;

    @Autowired
    @Qualifier("pooledLdapConnectionFactoryHealthIndicatorListFactoryBean")
    private ListFactoryBean pooledLdapConnectionFactoryHealthIndicatorListFactoryBean;

    @Test
    void verifyObserve() throws Exception {
        val results = monitor.stream()
            .map(it -> HealthIndicator.class.cast(it.getContributor()))
            .map(it -> it.health().getStatus()).toList();
        assertFalse(results.isEmpty());
        assertEquals(Status.UP, results.get(0));
        pooledLdapConnectionFactoryHealthIndicatorListFactoryBean.destroy();
    }
}
