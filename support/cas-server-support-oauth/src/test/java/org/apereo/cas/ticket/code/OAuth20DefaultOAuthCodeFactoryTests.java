package org.apereo.cas.ticket.code;

import org.apereo.cas.AbstractOAuth20Tests;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.oauth.OAuth20GrantTypes;
import org.apereo.cas.support.oauth.OAuth20ResponseTypes;
import org.apereo.cas.support.oauth.services.DefaultRegisteredServiceOAuthCodeExpirationPolicy;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link OAuth20DefaultOAuthCodeFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Tag("OAuthToken")
class OAuth20DefaultOAuthCodeFactoryTests extends AbstractOAuth20Tests {

    @Test
    void verifyOperationWithExpPolicy() {
        val registeredService = getRegisteredService("https://code.oauth.org", "clientid-code", "secret-at");
        registeredService.setCodeExpirationPolicy(
            new DefaultRegisteredServiceOAuthCodeExpirationPolicy(10, "PT10S"));
        servicesManager.save(registeredService);
        val tgt = new MockTicketGrantingTicket("casuser");
        val token = defaultOAuthCodeFactory.create(RegisteredServiceTestUtils.getService("https://code.oauth.org"),
            RegisteredServiceTestUtils.getAuthentication(),
            tgt,
            Set.of("Scope1", "Scope2"), "code-challenge", "plain",
            "clientid-code", Map.of(),
            OAuth20ResponseTypes.CODE, OAuth20GrantTypes.AUTHORIZATION_CODE);
        assertNotNull(token);
        assertTrue(tgt.getDescendantTickets().stream().anyMatch(t -> t.equalsIgnoreCase(token.getId())));
    }

    @Test
    void verifyOperationWithoutExpPolicy() {
        val registeredService = getRegisteredService("https://noexp.oauth.org", "clientid-code-noexp", "secret-at");
        servicesManager.save(registeredService);
        val tgt = new MockTicketGrantingTicket("casuser");
        val token = defaultOAuthCodeFactory.create(RegisteredServiceTestUtils.getService("https://noexp.oauth.org"),
            RegisteredServiceTestUtils.getAuthentication(),
            tgt, Set.of("Scope1", "Scope2"), "code-challenge", "plain",
            "clientid-code-noexp", Map.of(), OAuth20ResponseTypes.CODE, OAuth20GrantTypes.AUTHORIZATION_CODE);
        assertNotNull(token);
        assertNotNull(defaultAccessTokenFactory.get(OAuth20Code.class));
        assertTrue(tgt.getDescendantTickets().stream().anyMatch(t -> t.equalsIgnoreCase(token.getId())));
    }

}
