package io.quarkiverse.discordbot.runtime.devmode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.jboss.logging.Logger;

import io.quarkiverse.discordbot.runtime.DiscordBotRecorder;
import io.quarkus.dev.spi.HotReplacementContext;
import io.quarkus.dev.spi.HotReplacementSetup;

public class DiscordBotHotReplacementSetup implements HotReplacementSetup {
    private static final Logger LOGGER = Logger.getLogger(DiscordBotHotReplacementSetup.class);
    private final Executor executor = Executors.newSingleThreadExecutor();
    private HotReplacementContext context;
    private volatile long nextUpdate;

    @Override
    public void setupHotDeployment(HotReplacementContext context) {
        this.context = context;
        DiscordBotRecorder.hotReplacementHandler = new HotReplacementHandler();
    }

    private class HotReplacementHandler implements Supplier<CompletableFuture<Boolean>> {
        @Override
        public CompletableFuture<Boolean> get() {
            synchronized (this) {
                if (nextUpdate < System.currentTimeMillis()) {
                    CompletableFuture<Boolean> result = new CompletableFuture<>();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                boolean restarted = context.doScan(true);
                                if (context.getDeploymentProblem() != null) {
                                    LOGGER.error("Failed to redeploy application on changes", context.getDeploymentProblem());
                                }
                                result.complete(restarted);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                result.complete(false);
                            }
                        }
                    });
                    nextUpdate = System.currentTimeMillis() + 2000;
                    return result;
                }
            }
            return CompletableFuture.completedFuture(false);
        }
    }
}
