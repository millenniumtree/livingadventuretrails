package com.millenniumtree.livingadventuretrails;

public class AutoTrailRuleSet extends RuleSetBase {
  private static int TRIGGER_MAX = 0;

  public AutoTrailRuleSet() {
    super();
//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: AutoPathRuleSet being created");
  }

  public int getTriggerMax() {
    if(TRIGGER_MAX > 0) return TRIGGER_MAX;
    TRIGGER_MAX = super.getTriggerMax();
    return TRIGGER_MAX;
  }

  public boolean ifTrigger(String worldID, String blockID, String type, boolean hasTool) {
    double random = 0;
    double transitionFrequency = 0;
    double divisor = LivingAdventureTrails.getDivisor(type, hasTool);
    if(divisor == 0) return false;

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: autotrail ifTrigger, steptype: "+type+" divisor: "+divisor);
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
