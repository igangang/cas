package org.apereo.cas.shell.commands.jasypt;

import org.apereo.cas.shell.commands.BaseCasShellCommandTests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link JasyptListAlgorithmsCommandTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@EnableAutoConfiguration
@Tag("SHELL")
class JasyptListAlgorithmsCommandTests extends BaseCasShellCommandTests {
    @Test
    void verifyOperation() {
        assertDoesNotThrow(() -> runShellCommand(() -> () -> "jasypt-list-algorithms --includeBC"));
    }

    @Test
    void verifyNoBouncyCastleOperation() {
        assertDoesNotThrow(() -> runShellCommand(() -> () -> "jasypt-list-algorithms"));
    }
}
