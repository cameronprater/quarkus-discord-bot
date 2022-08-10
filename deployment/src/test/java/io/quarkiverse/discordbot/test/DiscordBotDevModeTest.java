package io.quarkiverse.discordbot.test;

import static io.restassured.RestAssured.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import discord4j.core.event.domain.message.MessageCreateEvent;
import io.quarkiverse.discordbot.GatewayEvent;
import io.quarkus.test.QuarkusDevModeTest;

public class DiscordBotDevModeTest {

    @RegisterExtension
    static final QuarkusDevModeTest config = new QuarkusDevModeTest()
            .withApplicationRoot(jar -> jar.addClass(DiscordBotDevModeTestEndpoint.class)
                    .addAsResource("message.json")
                    .addAsResource("application.properties"))
            .setLogRecordPredicate(lr -> lr.getLoggerName().equals(MyBean.class.getName()));

    @Test
    public void testLiveReload() {
        get("/discord");
        assertEquals(0, config.getLogRecords().size());

        config.addSourceFile(MyBean.class);
        get("/discord");
        assertEquals(1, config.getLogRecords().size());
    }

    static class MyBean {
        private static final Logger LOGGER = Logger.getLogger(MyBean.class);

        void onMessageCreate(@GatewayEvent MessageCreateEvent event) {
            LOGGER.info("Received MessageCreate");
        }
    }
}
