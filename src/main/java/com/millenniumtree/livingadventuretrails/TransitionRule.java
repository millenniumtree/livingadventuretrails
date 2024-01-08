package com.millenniumtree.livingadventuretrails;

public class TransitionRule {
  public String worldID;
  public String blockID;
  public int frequency;
  public int aboveY;
  public int belowY;
  public boolean randomOrientation;
  public boolean loot;

  public TransitionRule(String world, String block, int frequencyVal, int above, int below, boolean isRandomOrientation, boolean isLoot) {
    worldID = world;
    blockID = block;
    frequency = frequencyVal;
    aboveY = above;
    belowY = below;
    randomOrientation = isRandomOrientation;
    loot = isLoot;

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: TransitionRule created: "+worldID+", "+blockID+", "+frequency+", "+aboveY+", "+belowY+", "+randomOrientation+", "+loot);
  }


}
