package com.millenniumtree.livingadventuretrails;

public class FragileRuleSet  extends RuleSetBase {
  private static int TRIGGER_MAX = 0;
  public FragileRuleSet() {
    super();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: FragileRuleSet being created");
  }

  public int getTriggerMax() {
    if(TRIGGER_MAX > 0) return TRIGGER_MAX;
    TRIGGER_MAX = super.getTriggerMax();
    return TRIGGER_MAX;
  }

  public boolean ifTrigger(String worldID, String blockID, String type, boolean hasTool) {
    if(blockID.equals("minecraft:air")) return false;
    if(blockID.equals("minecraft:water")) return false;

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: ifTrigger trigger ratio " + ((float)LivingAdventureTrails.getDivisor(type, hasTool) / (float)getTriggerMax()));

    double random = 0;
    double transitionFrequency = 0;
    double divisor = LivingAdventureTrails.getDivisor(type, hasTool);
    if(divisor == 0) return false;

    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: fragile ifTrigger, steptype: "+type+" divisor: "+divisor);
    double triggerMax = getTriggerMax();
    random = LivingAdventureTrails.getRandomBounded(divisor);
    if (random < triggerMax) {
      // this is comparatively expensive, so we do it after first checking triggerMax
      if(transitionFrequency == 0) transitionFrequency = getTransitionFrequency(worldID, blockID);
      if(random < transitionFrequency) {
        return true;
      }
    }

    return false;
  }

}
