// idk what this does

package com.yourname.getoffmyblocks;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CobblemonSpawnHandler {
    public static void register() {
        // Register the spawn event listener
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(event -> {
            SpawningContext context = event.getCtx();
            World world = context.getWorld();
            BlockPos pos = context.getPosition();
            
            // Get the block below the spawn position
            Block blockBelow = world.getBlockState(pos.down()).getBlock();
            
            // Check if the block is in our banned list
            if (BlockSpawnManager.isBlockBanned(blockBelow)) {
                GetOffMyBlocks.LOGGER.debug("Preventing Pokemon spawn on banned block: {}", 
                    net.minecraft.registry.Registries.BLOCK.getId(blockBelow));
                event.cancel();
            }
        });
    }
}
