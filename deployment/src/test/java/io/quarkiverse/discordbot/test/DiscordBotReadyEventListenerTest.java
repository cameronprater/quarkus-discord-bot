package io.quarkiverse.discordbot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import io.quarkiverse.discordbot.GatewayEvent;
import io.quarkus.test.QuarkusUnitTest;

public class DiscordBotReadyEventListenerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClass(MyBean.class))
            .setLogRecordPredicate(lr -> lr.getLoggerName().equals(MyBean.class.getName()))
            .withConfigurationResource("application.properties");

    @Inject
    GatewayDiscordClient gateway;

    @AfterEach
    void cleanup() {
        if (gateway != null) {
            gateway.logout().block();
        }
    }

    @Test
    public void testListener() {
        config.assertLogRecords(lr -> assertEquals(1, lr.size()));
    }

    static class MyBean {
        private static final Logger LOGGER = Logger.getLogger(MyBean.class);

        void onReady(@GatewayEvent ReadyEvent ready) {
            LOGGER.info("Received ReadyEvent");
        }
    }
}