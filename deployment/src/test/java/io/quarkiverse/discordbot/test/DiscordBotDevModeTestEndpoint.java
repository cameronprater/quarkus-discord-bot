package io.quarkiverse.discordbot.test;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;
import discord4j.gateway.ShardInfo;

@Path("/discord")
public class DiscordBotDevModeTestEndpoint {

    @Inject
    GatewayDiscordClient gateway;

    @Inject
    ObjectMapper objectMapper;

    @GET
    public void publishEvent() throws IOException {
        MessageData data = objectMapper.readValue(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("message.json"),
                MessageData.class);
        gateway.getEventDispatcher()
                .publish(new MessageCreateEvent(gateway, ShardInfo.create(0, 1), new Message(gateway, data), null, null));
    }
}
