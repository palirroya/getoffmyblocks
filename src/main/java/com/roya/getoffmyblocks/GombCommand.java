package com.yourname.getoffmyblocks;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GombCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("gomb")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("add")
                .then(CommandManager.argument("block", BlockArgumentType.block(Registries.BLOCK))
                    .executes(GombCommand::addBlock)))
            .then(CommandManager.literal("remove")
                .then(CommandManager.argument("block", BlockArgumentType.block(Registries.BLOCK))
                    .executes(GombCommand::removeBlock)))
            .then(CommandManager.literal("list")
                .executes(GombCommand::listBlocks)));
    }

    private static int addBlock(CommandContext<ServerCommandSource> context) {
        try {
            Block block = BlockArgumentType.getBlock(context, "block").value();
            boolean added = BlockSpawnManager.addBlock(block);
            
            Identifier blockId = Registries.BLOCK.getId(block);
            if (added) {
                context.getSource().sendFeedback(() -> 
                    Text.literal("Added ").formatted(Formatting.GREEN)
                        .append(Text.literal(blockId.toString()).formatted(Formatting.YELLOW))
                        .append(Text.literal(" to the no-spawn list").formatted(Formatting.GREEN)), 
                    false);
            } else {
                context.getSource().sendFeedback(() -> 
                    Text.literal("Block ").formatted(Formatting.YELLOW)
                        .append(Text.literal(blockId.toString()).formatted(Formatting.YELLOW))
                        .append(Text.literal(" is already in the no-spawn list").formatted(Formatting.YELLOW)), 
                    false);
            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to add block: " + e.getMessage()));
            return 0;
        }
    }

    private static int removeBlock(CommandContext<ServerCommandSource> context) {
        try {
            Block block = BlockArgumentType.getBlock(context, "block").value();
            boolean removed = BlockSpawnManager.removeBlock(block);
            
            Identifier blockId = Registries.BLOCK.getId(block);
            if (removed) {
                context.getSource().sendFeedback(() -> 
                    Text.literal("Removed ").formatted(Formatting.RED)
                        .append(Text.literal(blockId.toString()).formatted(Formatting.YELLOW))
                        .append(Text.literal(" from the no-spawn list").formatted(Formatting.RED)), 
                    false);
            } else {
                context.getSource().sendFeedback(() -> 
                    Text.literal("Block ").formatted(Formatting.YELLOW)
                        .append(Text.literal(blockId.toString()).formatted(Formatting.YELLOW))
                        .append(Text.literal(" was not in the no-spawn list").formatted(Formatting.YELLOW)), 
                    false);
            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to remove block: " + e.getMessage()));
            return 0;
        }
    }

    private static int listBlocks(CommandContext<ServerCommandSource> context) {
        var blocks = BlockSpawnManager.getNoSpawnBlocks();
        
        if (blocks.isEmpty()) {
            context.getSource().sendFeedback(() -> 
                Text.literal("No blocks are currently banned from Pokemon spawning").formatted(Formatting.YELLOW), 
                false);
        } else {
            context.getSource().sendFeedback(() -> 
                Text.literal("Blocks banned from Pokemon spawning:").formatted(Formatting.GREEN), 
                false);
            
            for (Block block : blocks) {
                Identifier blockId = Registries.BLOCK.getId(block);
                context.getSource().sendFeedback(() -> 
                    Text.literal("- ").formatted(Formatting.GRAY)
                        .append(Text.literal(blockId.toString()).formatted(Formatting.YELLOW)), 
                    false);
            }
        }
        return 1;
    }
}
