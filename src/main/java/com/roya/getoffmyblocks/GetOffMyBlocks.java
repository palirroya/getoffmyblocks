// MAIN MOD FILE

package com.roya.getoffmyblocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetOffMyBlocks implements ModInitializer {
    public static final String MOD_ID = "getoffmyblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("getoffmyblocks initializing...");
        
        // Initialize the block manager
        BlockSpawnManager.initialize();
        
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            GombCommand.register(dispatcher);
        });
        
        // Register Cobblemon spawn event listener
        CobblemonSpawnHandler.register();
        
        LOGGER.info("getoffmyblocks initialized");
    }
}
