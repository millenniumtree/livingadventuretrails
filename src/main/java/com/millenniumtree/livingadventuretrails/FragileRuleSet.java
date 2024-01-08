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

  public boolean ifTrigger(String worldID, String blockID, String type, Integer boost) {
    if(blockID.equals("minecraft:air")) return false;
    if(blockID.equals("minecraft:water")) return false;

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: ifTrigger trigger ratio " + ((float)LivingAdventureTrails.getDivisor(type) / (float)getTriggerMax()));

    double random = 0;
    double transitionFrequency = 0;
    double divisor = LivingAdventureTrails.getDivisor(type);
    double triggerMax = getTriggerMax();
    // Try boost number of times
    for(int i = 0; i < boost; i++) {
      random = LivingAdventureTrails.getRandomBounded(divisor);
      if (random < triggerMax) {
        // this is comparatively expensive, so we do it after first checking triggerMax
        if(transitionFrequency == 0) transitionFrequency = getTransitionFrequency(worldID, blockID);
        if(random < transitionFrequency) {
          return true;
        }
      }
    }

    return false;
  }

}
