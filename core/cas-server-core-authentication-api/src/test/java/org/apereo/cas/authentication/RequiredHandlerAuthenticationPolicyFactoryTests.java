package org.apereo.cas.authentication;

import org.apereo.cas.authentication.policy.RequiredHandlerAuthenticationPolicyFactory;
import org.apereo.cas.services.ServiceContext;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link RequiredHandlerAuthenticationPolicyFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("AuthenticationHandler")
class RequiredHandlerAuthenticationPolicyFactoryTests {

    @Test
    void verifyOperation() {
        val input = new RequiredHandlerAuthenticationPolicyFactory();
        val policy = input.createPolicy(new ServiceContext(CoreAuthenticationTestUtils.getService(),
            CoreAuthenticationTestUtils.getRegisteredService()));
        assertNotNull(policy.getContext());
    }

}
