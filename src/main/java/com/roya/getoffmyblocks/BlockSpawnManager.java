// idk what this does yet

package com.yourname.getoffmyblocks;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockSpawnManager {
    private static final Set<Block> noSpawn = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();
    private static final Path caonfigFile = FabricLoader.getInstance().getConfigDir().resolve("getoffmyblocks.json");

    public static void initialize() {
        loadFromFile();
    }

    public static boolean addBlock(Block block) {
        boolean added = noSpawn.add(block);
        if (added) {
            saveToFile();
        }
        return added;
    }

    public static boolean removeBlock(Block block) {
        boolean removed = noSpawn.remove(block);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    public static Set<Block> listNoSpawn() {
        return new HashSet<>(noSpawn);
    }

    public static boolean isBlockBanned(Block block) {
        return noSpawn.contains(block);
    }

    private static void saveToFile() {
        try {
            Set<String> blockIds = new HashSet<>();
            for (Block block : noSpawn) {
                Identifier id = Registries.BLOCK.getId(block);
                blockIds.add(id.toString());
            }
            
            String json = gson.toJson(blockIds);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            GetOffMyBlocks.LOGGER.error("Failed to save config file", e);
        }
    }

    private static void loadFromFile() {
        if (!Files.exists(configFile)) {
            return;
        }

        try {
            String json = Files.readString(configFile);
            Type setType = new TypeToken<Set<String>>(){}.getType();
            Set<String> blockIds = gson.fromJson(json, setType);
            
            if (blockIds != null) {
                noSpawn.clear();
                for (String blockId : blockIds) {
                    Identifier id = new Identifier(blockId);
                    Block block = Registries.BLOCK.get(id);
                    if (block != null) {
                        noSpawn.add(block);
                    }
                }
            }
        } catch (IOException e) {
            GetOffMyBlocks.LOGGER.error("Error: The config file didnt load :(", e);
        }
    }
}
