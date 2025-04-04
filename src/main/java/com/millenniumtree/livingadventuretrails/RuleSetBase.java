package com.millenniumtree.livingadventuretrails;

import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.*;

public class RuleSetBase {
  public static int TRIGGER_MAX = 0;

  protected String worldID;

  protected Hashtable<String, TransitionRule[]> transitions;

  public RuleSetBase() {
    transitions = new Hashtable<>();
  }

  public void addRuleFromConfig(String worldID, String blockID, String configValue) {

    if(!worldID.contains(":")) {
      worldID = "minecraft:"+worldID;
    }
    if(!blockID.contains(":")) {
      blockID = "minecraft:"+blockID;
    }

    // This block ID changed in Minecraft 1.20.3
    if(blockID.equals("minecraft:grass")) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.addRuleFromConfig - blockID: minecraft:grass has been replaced with minecraft:short_grass. Please update your config file!");
      blockID = "minecraft:short_grass";
    }

    TransitionRule[] possibleTransitions;
    String transitionBlockID;
    int transitionFrequency;
    boolean randomOrientation;
    int aboveY;
    int belowY;
    boolean loot;

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.addRuleFromConfig - worldID: "+worldID+", blockID: "+blockID+", configValue: "+configValue);
    String[] possibleTransitionsConfig = configValue.split(";");

    possibleTransitions = new TransitionRule[possibleTransitionsConfig.length];

    for (int possibleTransitionKey = 0; possibleTransitionKey < possibleTransitionsConfig.length; possibleTransitionKey++) {
      String possibleTransitionConfig = possibleTransitionsConfig[possibleTransitionKey].replaceAll("[()]", "").trim();
      String[] transitionOptionsConfig = possibleTransitionConfig.split(",");

      transitionBlockID = "";
      transitionFrequency = 0;
      aboveY = -1000;
      belowY = 1000;
      randomOrientation = false;
      loot = false;

      for (int transitionOptionKey = 0; transitionOptionKey < transitionOptionsConfig.length; transitionOptionKey++) {
        String transitionOption = transitionOptionsConfig[transitionOptionKey].trim();
        if(transitionOptionKey == 0) {
          transitionBlockID = transitionOption;
//          LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.addRuleFromConfig - transitionBlockID: "+transitionBlockID);
        } else if(transitionOptionKey == 1) {
          transitionFrequency = Integer.parseInt(transitionOption);
//          LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.addRuleFromConfig - transitionFrequency: "+transitionFrequency);
        } else {
          if(transitionOption.equals("loot")) {
            loot = true;
          } else if(transitionOption.equals("random_orientation")) {
            randomOrientation = true;
          } else if(transitionOption.contains("above ")) {
            aboveY = Integer.parseInt(transitionOption.split(" ")[1]);
          } else if(transitionOption.contains("below ")) {
            belowY = Integer.parseInt(transitionOption.split(" ")[1]);
          }
        }
      }

      possibleTransitions[possibleTransitionKey] = new TransitionRule(worldID, transitionBlockID, transitionFrequency, aboveY, belowY, randomOrientation, loot);

    }

    transitions.put(worldID+";"+blockID, possibleTransitions);

  }
  protected boolean worldMatch(World world) {
    return (world.getRegistryKey().getValue().toString().equals(worldID));
  }

  protected boolean blockMatch(Block block) {
    return false;
//    return (Registries.BLOCK.getId(block).toString().equals(blockID));
  }

  /**
   * Returns the maximum trigger for any world/block transition in the rule set
   * @return int
   */
  public int getTriggerMax() {
    final int[] triggerMax = {0};
    transitions.forEach((String id, TransitionRule[] transitionRules) -> {
      int transitionFrequencyTotal = 0;
      for (TransitionRule transitionRule : transitionRules) {
        transitionFrequencyTotal += transitionRule.frequency;
        // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.getTriggerMax - worldID,blockID,frequency: " + transitionRule.worldID + ',' + transitionRule.blockID + ',' + transitionRule.frequency);
      }
      if(triggerMax[0] < transitionFrequencyTotal) {
        triggerMax[0] = transitionFrequencyTotal;
      }
    });
    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.getTriggerMax - triggerMax: " + triggerMax[0]);
    return triggerMax[0];
  }

  /**
   * Returns the maximum trigger for any world/block transition in the rule set
   * @return int
   */
  public int getTransitionFrequency(String worldID, String blockID) {
    String ruleKey = worldID+";"+blockID;
    int transitionFrequencyTotal = 0;
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.ifTrigger - ruleKey: " + ruleKey);

//    for ( String key : transitions.keySet() ) {
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.ifTrigger - key: " + key);
//    }

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.ifTrigger - transitions.keys().toString(): " + transitions.keySet().toString());

    if(transitions.containsKey(ruleKey)) {
      TransitionRule[] transitionRules = transitions.get(ruleKey);

      for (TransitionRule transitionRule : transitionRules) {
              transitionFrequencyTotal += transitionRule.frequency;
      }
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.ifTrigger - transitionFrequencyTotal: " + transitionFrequencyTotal);
      // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: RuleSetBase.getTriggerMax - triggerMax: " + triggerMax[0]);
    }
    return transitionFrequencyTotal;
  }

  public TransitionRule getRandomTransitionRule(String worldID, String blockID, String type) {
    String ruleKey = worldID+";"+blockID;
    int transitionFrequencyTotal = 0;

    if(transitions.containsKey(ruleKey)) {
      TransitionRule[] transitionRules = transitions.get(ruleKey);
      for (TransitionRule transitionRule : transitionRules) {
        transitionFrequencyTotal += transitionRule.frequency;
      }
      double randomBlockFrequency = LivingAdventureTrails.getRandomBounded(transitionFrequencyTotal);

      transitionFrequencyTotal = 0;
      TransitionRule transitionRuleChosen;

      for (TransitionRule transitionRule : transitionRules) {
//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: transitionRule ruleKey: " + ruleKey);
//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: transitionRule aboveY: " + transitionRule.aboveY);
//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: transitionRule belowY: " + transitionRule.belowY);
        transitionFrequencyTotal += transitionRule.frequency;
        if(randomBlockFrequency < transitionFrequencyTotal) {
          return transitionRule;
        }
      }
    }
    return null;
  }
}
