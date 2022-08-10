package io.quarkiverse.discordbot.commands.runtime.config;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "discord-bot", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class DiscordBotCommandsConfig {
    /**
     * Global commands configuration
     */
    @ConfigItem
    public GlobalCommandsConfig globalCommands;

    /**
     * Guild commands configuration
     */
    @ConfigItem
    public Map<String, GuildCommandsConfig> guildCommands;
}
