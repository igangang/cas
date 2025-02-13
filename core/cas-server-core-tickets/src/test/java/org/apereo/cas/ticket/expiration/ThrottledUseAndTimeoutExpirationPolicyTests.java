package org.apereo.cas.ticket.expiration;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.DefaultServiceTicketSessionTrackingPolicy;
import org.apereo.cas.ticket.DefaultTicketCatalog;
import org.apereo.cas.ticket.ServiceTicketSessionTrackingPolicy;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.apereo.cas.ticket.registry.DefaultTicketRegistry;
import org.apereo.cas.ticket.serialization.TicketSerializationManager;
import org.apereo.cas.util.serialization.JacksonObjectMapperFactory;
import org.apereo.cas.util.serialization.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link ThrottledUseAndTimeoutExpirationPolicy}.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Tag("ExpirationPolicy")
class ThrottledUseAndTimeoutExpirationPolicyTests {

    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "throttleUseAndTimeoutExpirationPolicy.json");

    private static final ObjectMapper MAPPER = JacksonObjectMapperFactory.builder()
        .defaultTypingEnabled(true).build().toObjectMapper();

    private static final long TIMEOUT = 2000;

    private ThrottledUseAndTimeoutExpirationPolicy expirationPolicy;

    private TicketGrantingTicketImpl ticket;

    private static ServiceTicketSessionTrackingPolicy getTrackingPolicy() {
        val props = new CasConfigurationProperties();
        props.getTicket().getTgt().getCore().setOnlyTrackMostRecentSession(true);
        return new DefaultServiceTicketSessionTrackingPolicy(props, new DefaultTicketRegistry(mock(TicketSerializationManager.class), new DefaultTicketCatalog()));
    }

    @BeforeEach
    public void initialize() {
        this.expirationPolicy = new ThrottledUseAndTimeoutExpirationPolicy();
        this.expirationPolicy.setTimeToKillInSeconds(TIMEOUT);
        this.expirationPolicy.setTimeInBetweenUsesInSeconds(TIMEOUT / 5);
        this.ticket = new TicketGrantingTicketImpl("test", CoreAuthenticationTestUtils
            .getAuthentication(), this.expirationPolicy);
    }

    @Test
    void verifyTicketIsNotExpired() {
        assertFalse(this.ticket.isExpired());
    }

    @Test
    void verifyTicketIsExpired() {
        expirationPolicy.setTimeToKillInSeconds(-TIMEOUT);
        assertTrue(this.ticket.isExpired());
    }

    @Test
    void verifyTicketUsedButWithTimeout() {
        this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false,
            getTrackingPolicy());
        expirationPolicy.setTimeToKillInSeconds(TIMEOUT);
        expirationPolicy.setTimeInBetweenUsesInSeconds(-10);
        assertFalse(this.ticket.isExpired());
    }

    @Test
    void verifyThrottleNotTriggeredWithinOneSecond() {
        this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false,
            getTrackingPolicy());
        val clock = Clock.fixed(this.ticket.getLastTimeUsed().toInstant().plusMillis(999), ZoneOffset.UTC);
        expirationPolicy.setClock(clock);
        assertFalse(this.ticket.isExpired());
    }

    @Test
    void verifyNotWaitingEnoughTime() {
        this.ticket.grantServiceTicket("test", RegisteredServiceTestUtils.getService(), this.expirationPolicy, false,
            getTrackingPolicy());
        val clock = Clock.fixed(this.ticket.getLastTimeUsed().toInstant().plusSeconds(1), ZoneOffset.UTC);
        expirationPolicy.setClock(clock);
        assertTrue(this.ticket.isExpired());
    }

    @Test
    void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        MAPPER.writeValue(JSON_FILE, expirationPolicy);
        val policyRead = MAPPER.readValue(JSON_FILE, ThrottledUseAndTimeoutExpirationPolicy.class);
        assertEquals(expirationPolicy, policyRead);
    }

    @Test
    void verifySerialization() {
        val result = SerializationUtils.serialize(expirationPolicy);
        val policyRead = SerializationUtils.deserialize(result, ThrottledUseAndTimeoutExpirationPolicy.class);
        assertEquals(expirationPolicy, policyRead);
    }
}
