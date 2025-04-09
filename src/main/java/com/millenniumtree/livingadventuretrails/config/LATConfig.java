package com.millenniumtree.livingadventuretrails.config;

import com.millenniumtree.livingadventuretrails.AutoTrailRuleSet;
import com.millenniumtree.livingadventuretrails.FragileRuleSet;
import com.millenniumtree.livingadventuretrails.LivingAdventureTrails;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LATConfig {
  public static String error = "";
  private static boolean paused;
  private static HashMap<Integer, Boolean> pausedSelf = new HashMap<Integer, Boolean>();

  public static SimpleConfig latConfig;
  private static LATConfigProvider latConfigProvider;
  // Pre-computing these max trigger values will allow us to ignore most random triggers to save CPU time
  // The values are the highest 'frequency' number from the configs for block autotrailing (stepping/landing),
  // and fragile blocks.  Any random trigger generated between the divisor and these max triggers can be ignored.
  public static String[] TOOLS = {};
  public static Item[] TOOL_ITEMS = {};
  public static String[] LEAF_TOOLS = {};
  public static Item[] LEAF_TOOL_ITEMS = {};

  public static double DIVISOR_STEP = 0;
  public static double DIVISOR_STEPTOOL = 0;
  public static double DIVISOR_JUMP = 0;
  public static double DIVISOR_JUMPTOOL = 0;
  public static double DIVISOR_HORSE = 0;
  public static double DIVISOR_HORSETOOL = 0;
//  public static int AUTOPATH_TRIGGER_MAX = 0;
//  public static int FRAGILE_TRIGGER_MAX = 0;

  public static AutoTrailRuleSet autoTrailRules;
  public static FragileRuleSet fragileRules;

  public static Path getConfigFilePath() {
    return FabricLoader.getInstance().getConfigDir().resolve(LivingAdventureTrails.MOD_ID + ".properties");
  }

  public static void registerConfigs() {
    copyDefaultConfig();

    latConfigProvider = new LATConfigProvider();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: Attempting to load config file "+LivingAdventureTrails.MOD_ID+".properties");
    // This reads from the config file livingadventuretrails.properties under run->config
    latConfig = SimpleConfig.of(LivingAdventureTrails.MOD_ID).provider(latConfigProvider).request();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: File loaded with "+latConfig.config.size()+" entries");

//    DIVISOR_STEP = LATConfig.latConfig.getOrDefault("global.divisor", 10000);

    fragileRules = new FragileRuleSet();
    autoTrailRules = new AutoTrailRuleSet();

    for (Map.Entry<String, String> entry : latConfig.config.entrySet()) {
      String transitionValue = entry.getValue().trim();
      String transitionKey = entry.getKey().trim();
      String[] transitionKeySplit = transitionKey.split(",");
      String type = transitionKeySplit[0];
      String worldID = "";
      String blockID = "";

//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: config line type/key "+type+" ("+transitionKey+")");

      if(
        type.equals("tool")
      ) {
        TOOLS = transitionValue.split(";");
        TOOL_ITEMS = new Item[TOOLS.length];
        for(int n = 0; n < TOOLS.length; n++) {
          TOOLS[n] = TOOLS[n].trim();
          if(!TOOLS[n].contains(":")) {
            TOOLS[n] = "minecraft:" + TOOLS[n];
          }
          TOOL_ITEMS[n] = Registries.ITEM.get(Identifier.tryParse(TOOLS[n]));
          // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: loading tool "+TOOLS[n]);
          // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: loading tool_item "+TOOL_ITEMS[n].toString());
        }

      } else if(
        type.equals("leaftool")
      ) {
        LEAF_TOOLS = transitionValue.split(";");
        LEAF_TOOL_ITEMS = new Item[LEAF_TOOLS.length];
        for(int n = 0; n < LEAF_TOOLS.length; n++) {
          LEAF_TOOLS[n] = LEAF_TOOLS[n].trim();
          if(!LEAF_TOOLS[n].contains(":")) {
            LEAF_TOOLS[n] = "minecraft:" + LEAF_TOOLS[n];
          }
          LEAF_TOOL_ITEMS[n] = Registries.ITEM.get(Identifier.tryParse(LEAF_TOOLS[n]));
          // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: loading leaftool "+LATConfig.LEAF_TOOLS[n]);
          // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: loading leaftool_item "+LEAF_TOOL_ITEMS[n].toString());
        }

      } else if(
        type.equals("divisor")
      ) {
        if(transitionKeySplit.length > 1) {
          switch (transitionKeySplit[1]) {
            case "step":
              DIVISOR_STEP = Integer.parseInt(transitionValue);
              break;
            case "steptool":
              DIVISOR_STEPTOOL = Integer.parseInt(transitionValue);
              break;
            case "jump":
              DIVISOR_JUMP = Integer.parseInt(transitionValue);
              break;
            case "jumptool":
              DIVISOR_JUMPTOOL = Integer.parseInt(transitionValue);
              break;
            case "horse":
              DIVISOR_HORSE = Integer.parseInt(transitionValue);
              break;
            case "horsetool":
              DIVISOR_HORSETOOL = Integer.parseInt(transitionValue);
              break;
          }
        } else {
          // This is an old config - set all divisor numbers the same, so the game doesn't crash
          DIVISOR_STEP = Integer.parseInt(transitionValue);
          DIVISOR_JUMP = Integer.parseInt(transitionValue);
          DIVISOR_HORSE = Integer.parseInt(transitionValue);
        }
      } else if(type.equals("autotrail")) {
        worldID = transitionKeySplit[1];
        blockID = transitionKeySplit[2];
        autoTrailRules.addRuleFromConfig(worldID, blockID, transitionValue);

        // Find the TRANSITION_TRIGGER_MAX, and FRAGILE_TRIGGER_MAX
      } else if(type.equals("fragile")) {
        worldID = transitionKeySplit[1];
        blockID = transitionKeySplit[2];
        fragileRules.addRuleFromConfig(worldID, blockID, transitionValue);

//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: fragile rule found - worldID: "+worldID+", blockID: "+blockID);
        // Find the TRANSITION_TRIGGER_MAX, and FRAGILE_TRIGGER_MAX
      }

    }

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: DIVISOR_STEP: "+DIVISOR_STEP);
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: DIVISOR_JUMP: "+DIVISOR_JUMP);
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: DIVISOR_HORSE: "+DIVISOR_HORSE);

//    AUTOPATH_TRIGGER_MAX = autoTrailRules.getTriggerMax();
//    FRAGILE_TRIGGER_MAX = fragileRules.getTriggerMax();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: AUTOPATH_TRIGGER_MAX: "+autoTrailRules.getTriggerMax());
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: FRAGILE_TRIGGER_MAX: "+fragileRules.getTriggerMax());

//    if(autoTrailRules.ifTrigger("the_end", "minecraft:end_stone")) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: the_end;minecraft:end_stone triggered");
//    }
//    if(autoTrailRules.ifTrigger("the_nether", "minecraft:crimson_nylium")) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: the_nether;minecraft:crimson_nylium triggered");
//    }
//    if(fragileRules.ifTrigger("overworld", "minecraft:azure_bluet")) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: overworld;minecraft:azure_bluet triggered");
//    }
//    if(fragileRules.ifTrigger("the_nether", "minecraft:nether_sprouts")) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: the_nether;minecraft:nether_sprouts triggered");
//    }
  }

//  public static int getFragileBlockFrequency() {
//    String transition = LATConfig.latConfig.getOrDefault(currentWorldId+negative+blockID, "");
//    if(negative == "" && transition == "") {
//      transition = LATConfig.latConfig.getOrDefault(currentWorldId+blockID, "");
//    }
//
//  }

  public static void run() {
    paused = false;
  }
  public static void runSelf(ServerPlayerEntity player) {
    pausedSelf.put(player.getId(), false);
  }

  public static boolean isPaused() {
    return paused;
  }
  public static boolean isPaused(ServerPlayerEntity player) {
    if(paused) return true;
    if(
      pausedSelf.getOrDefault(player.getId(), false)) return true;
    return false;
  }

  public static void pause() {
    paused = true;
  }
  public static void pauseSelf(ServerPlayerEntity player) {
    pausedSelf.put(player.getId(), true);
  }

  public static Path getDefaultConfigFilePath() {
    List paths = FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().getRootPaths();
//    if(FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().findPath(".").isPresent()) {
      String packagedFile = "assets/"+LivingAdventureTrails.MOD_ID+"/config/"+LivingAdventureTrails.MOD_ID+".properties";
//      LATConfig.error = "getConfigFile "+FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().findPath(packagedFile).get();
//    }
//    LATConfig.error = "getConfigFile "+FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().findPath("/").isPresent();
//    LATConfig.error = "getConfigFile "+FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().findPath("/config/"+LivingAdventureTrails.MOD_ID+".properties");

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: path "+paths.get(p));

//      LATConfig.error = "ERROR! default config file not found at "+paths.get(p) + "/assets/"+LivingAdventureTrails.MOD_ID+"/config/"+LivingAdventureTrails.MOD_ID+".properties"+"!";

    Path defaultConfigFile = FabricLoader.getInstance().getModContainer(LivingAdventureTrails.MOD_ID).get().findPath(packagedFile).get();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: defaultConfigFile "+defaultConfigFile.toString());

    if(Files.exists(defaultConfigFile)) {
      LATConfig.error = "";

      return defaultConfigFile;
    }
    return null;
  }

  public static void restoreDefaultConfigFile() {
    Path targetConfigFilePath = getConfigFilePath();
    Path defaultConfigFilePath = getDefaultConfigFilePath();
    if(defaultConfigFilePath == null) {
//      LATConfig.error = "ERROR! default config file not found!";
      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: ERROR! default config file not found!");
      return;
    }

    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: defaultConfigFile "+defaultConfigFilePath);
    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: targetConfigPath "+targetConfigFilePath);

    if(Files.exists(defaultConfigFilePath)) {
      try {
//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: copying "+defaultConfigFilePath+" to "+targetConfigFilePath);
        Files.copy(defaultConfigFilePath, targetConfigFilePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException("LivingAdventureTrails: Failed to copy default config file", e);
      }
    }
  }

  public static void copyDefaultConfig() {
    // Specify the path where you want to copy the default configuration file in the config directory
    Path targetConfigPath = getConfigFilePath();

    // Check if the target config file already exists
    try {
      if (
        !Files.exists(targetConfigPath)
          || Files.size(targetConfigPath) < 10
      ) {
        // Create the parent directories if they don't exist
        Files.createDirectories(targetConfigPath.getParent());

        // Write the default configuration string to the config file
        restoreDefaultConfigFile();
      }
    } catch (IOException e) {
      // e.printStackTrace();
    }

  }

}
