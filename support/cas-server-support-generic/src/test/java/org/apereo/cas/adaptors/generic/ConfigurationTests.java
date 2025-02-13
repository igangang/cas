package org.apereo.cas.adaptors.generic;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.config.CasCookieConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreLogoutConfiguration;
import org.apereo.cas.config.CasCoreMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreTicketsSerializationConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasCoreWebflowConfiguration;
import org.apereo.cas.config.CasMultifactorAuthenticationWebflowConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.config.CasWebflowContextConfiguration;
import org.apereo.cas.config.FileAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.GroovyAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.JsonResourceAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.RejectUsersAuthenticationEventExecutionPlanConfiguration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link ConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    CasCoreLogoutConfiguration.class,
    CasCoreWebflowConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreNotificationsConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreWebConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreMultifactorAuthenticationConfiguration.class,
    CasMultifactorAuthenticationWebflowConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreAuthenticationConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    CasCoreAuthenticationHandlersConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreTicketIdGeneratorsConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreTicketsSerializationConfiguration.class,
    CasCookieConfiguration.class,
    CasPersonDirectoryConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreUtilConfiguration.class,
    FileAuthenticationEventExecutionPlanConfiguration.class,
    GroovyAuthenticationEventExecutionPlanConfiguration.class,
    JsonResourceAuthenticationEventExecutionPlanConfiguration.class,
    RejectUsersAuthenticationEventExecutionPlanConfiguration.class
}, properties = {
    "cas.authn.file.filename=classpath:authentication.txt",
    "cas.authn.groovy.location=classpath:GroovyAuthnHandler.groovy",
    "cas.authn.json.location=classpath:sample-users.json",
    "cas.authn.reject.users=one,two,three"
})
@Tag("CasConfiguration")
class ConfigurationTests {
    @Autowired
    @Qualifier("fileAuthenticationHandler")
    private AuthenticationHandler fileAuthenticationHandler;

    @Autowired
    @Qualifier("groovyResourceAuthenticationHandler")
    private AuthenticationHandler groovyResourceAuthenticationHandler;

    @Autowired
    @Qualifier("jsonResourceAuthenticationHandler")
    private AuthenticationHandler jsonResourceAuthenticationHandler;

    @Autowired
    @Qualifier("rejectUsersAuthenticationHandler")
    private AuthenticationHandler rejectUsersAuthenticationHandler;

    @Test
    void verifyOperation() {
        assertNotNull(fileAuthenticationHandler);
        assertNotNull(groovyResourceAuthenticationHandler);
        assertNotNull(jsonResourceAuthenticationHandler);
        assertNotNull(rejectUsersAuthenticationHandler);
    }
}
