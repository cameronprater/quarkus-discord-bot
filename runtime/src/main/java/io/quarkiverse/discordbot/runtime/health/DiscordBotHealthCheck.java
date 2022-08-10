package io.quarkiverse.discordbot.runtime.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.GatewayClient;
import discord4j.gateway.GatewayClientGroup;
import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;

@Readiness
public class DiscordBotHealthCheck implements AsyncHealthCheck {
    public static final String NAME = "Discord Bot health check";

    @Inject
    GatewayDiscordClient gateway;

    @Override
    public Uni<HealthCheckResponse> call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(NAME).up();

        GatewayClientGroup gatewayClientGroup = gateway.getGatewayClientGroup();
        List<Uni<?>> unis = new ArrayList<>();
        for (int i = 0; i < gatewayClientGroup.getShardCount(); i++) {
            Optional<GatewayClient> client = gatewayClientGroup.find(i);
            if (client.isEmpty()) {
                continue;
            }

            String responseTime = client.get().getResponseTime().toString().substring(2).toLowerCase();
            String shardName = "shard." + i;

            unis.add(
                    Uni.createFrom().publisher(client.get().isConnected().doOnNext(connected -> {
                        if (connected) {
                            builder.withData(shardName + ".response.time", responseTime);
                        } else {
                            builder.down().withData("reason", shardName + " is not connected");
                        }
                    })));
        }

        return Uni.combine().all().unis(unis).combinedWith(ignored -> builder.build());
    }
}
