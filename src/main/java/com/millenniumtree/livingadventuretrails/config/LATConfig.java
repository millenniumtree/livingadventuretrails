package com.millenniumtree.livingadventuretrails.config;

import com.millenniumtree.livingadventuretrails.AutoTrailRuleSet;
import com.millenniumtree.livingadventuretrails.FragileRuleSet;
import com.millenniumtree.livingadventuretrails.LivingAdventureTrails;
import net.fabricmc.loader.impl.launch.FabricMixinBootstrap;

import java.util.Map;

public class LATConfig {

  public static SimpleConfig latConfig;
  private static LATConfigProvider latConfigProvider;
  // Pre-computing these max trigger values will allow us to ignore most random triggers to save CPU time
  // The values are the highest 'frequency' number from the configs for block autotrailing (stepping/landing),
  // and fragile blocks.  Any random trigger generated between the divisor and these max triggers can be ignored.
  public static double DIVISOR_STEP = 0;
  public static double DIVISOR_JUMP = 0;
  public static double DIVISOR_HORSE = 0;
//  public static int AUTOPATH_TRIGGER_MAX = 0;
//  public static int FRAGILE_TRIGGER_MAX = 0;

  public static AutoTrailRuleSet autoTrailRules;
  public static FragileRuleSet fragileRules;

  public static void registerConfigs() {

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

      if(
        type.equals("divisor")
      ) {
        if(transitionKeySplit.length > 1) {
          switch (transitionKeySplit[1]) {
            case "step":
              DIVISOR_STEP = Integer.parseInt(transitionValue);
              break;
            case "jump":
              DIVISOR_JUMP = Integer.parseInt(transitionValue);
              break;
            case "horse":
              DIVISOR_HORSE = Integer.parseInt(transitionValue);
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
}
