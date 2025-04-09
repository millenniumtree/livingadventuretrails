package com.millenniumtree.livingadventuretrails;

import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static net.minecraft.server.command.CommandManager.literal;

/*
IDEAS:
Passing through leaves with a horse and a hoe in hand will break the leaves

Command to increase dramatically the conversion rate for laying paths between villages.
Fires when you get 8 blocks away from your last location.
Attempts to flatten a bit, the natural ground between the two places
    - Bringing some blocks up or down
    - Make at least 4 conversions
    - Maybe place some stairs if going up or down significantly between the two spots

Flattening and widening of the trail:
track direction of travel
pick a block 0-3 blocks perpendicular to that direction
if air, and block BELOW that block is dirt, bring it up
if block is solid, and block ABOVE that block air, bring the surface down

Break leaf blocks automatically, especially if you pass through them on a horse.



*/

public class LivingAdventureTrails implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("livingadventuretrails");

  public static final String MOD_ID = "livingadventuretrails";

  public static PlayerMoved playerMoved;

  public static String mcVersion;

  public static boolean hasTool(Entity entity) {
    ServerPlayerEntity playerEntity = null;
    if(entity instanceof ServerPlayerEntity) playerEntity = ((ServerPlayerEntity) entity);
    else if(
      (
        entity instanceof HorseEntity
        || entity instanceof PigEntity
        || entity instanceof SkeletonHorseEntity
        || entity instanceof DonkeyEntity
        || entity instanceof MuleEntity
        || entity instanceof StriderEntity
        || entity instanceof CamelEntity
      ) && entity.hasPlayerRider()
    ) {
        playerEntity = ((ServerPlayerEntity) entity.getControllingPassenger());
    }
    if(playerEntity == null) return false;

    for (ItemStack itemStack : playerEntity.getHandItems()) {
      for(int n = 0; n < LATConfig.TOOL_ITEMS.length; n++) {
        if(itemStack.getItem() == LATConfig.TOOL_ITEMS[n]) return true;
      }
    }
    return false;
  }

  public static boolean hasLeafTool(Entity entity) {
    ServerPlayerEntity playerEntity = null;
    if(entity instanceof ServerPlayerEntity) playerEntity = ((ServerPlayerEntity) entity);
    else if(
      (
        entity instanceof HorseEntity
          || entity instanceof PigEntity
          || entity instanceof SkeletonHorseEntity
          || entity instanceof DonkeyEntity
          || entity instanceof MuleEntity
          || entity instanceof StriderEntity
          || entity instanceof CamelEntity
      ) && entity.hasPlayerRider()
    ) {
      playerEntity = ((ServerPlayerEntity) entity.getControllingPassenger());
    }
    if(playerEntity == null) return false;

    for (ItemStack itemStack : playerEntity.getHandItems()) {
      for(int n = 0; n < LATConfig.LEAF_TOOL_ITEMS.length; n++) {
        if(itemStack.getItem() == LATConfig.LEAF_TOOL_ITEMS[n]) return true;
      }
    }
    return false;
  }

  @Override
  public void onInitialize() {

    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
      dispatcher.register(
        literal("latrails")
        .requires(source -> source.hasPermissionLevel(0))
        .then(
          literal("on")
          .requires(source -> source.hasPermissionLevel(0))
          .executes(context -> {
            LATConfig.runSelf(context.getSource().getPlayer());
            if(LATConfig.isPaused()) {
              context.getSource().sendFeedback(() -> Text.literal("Enabling Living Adventure:Trails for current player, but it is disabled globally"), false);
            } else {
              context.getSource().sendFeedback(() -> Text.literal("Living Adventure:Trails is now active"), false);
            }
            return 1;
          })
          .then(
            literal("global")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
              context.getSource().sendFeedback(() -> Text.literal("Enabling Living Adventure:Trails globally"), false);
              LATConfig.run();
              return 1;
            })
          )
        )
        .then(
          literal("off")
          .requires(source -> source.hasPermissionLevel(0))
          .executes(context -> {
            if(LATConfig.isPaused()) {
              context.getSource().sendFeedback(() -> Text.literal("Disabling Living Adventure:Trails for current player, it is also disabled globally"), false);
            } else {
              context.getSource().sendFeedback(() -> Text.literal("Disabling Living Adventure:Trails for current player"), false);
            }
            LATConfig.pauseSelf(context.getSource().getPlayer());
            return 1;
          })
          .then(
            literal("global")
              .requires(source -> source.hasPermissionLevel(2))
              .executes(context -> {
                context.getSource().sendFeedback(() -> Text.literal("Disabling Living Adventure:Trails globally"), false);
                LATConfig.pause();
                return 1;
              })
          )
        )
        .then(
          literal("reload")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
              context.getSource().sendFeedback(() -> Text.literal("Living Adventure:Trails reloading configs"), false);
              // store paused state before reload
              boolean paused = LATConfig.isPaused();

              // pause and reload configs
              LATConfig.pause();
              LATConfig.registerConfigs();

              // If we were running before, run again
              if(!paused) LATConfig.run();
              return 1;
            })
        )
        .then(
          literal("defaultconfig")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
              context.getSource().sendFeedback(() -> Text.literal("Living Adventure:Trails restoring default configs"), false);
              // store paused state before reload
              boolean paused = LATConfig.isPaused();

              // pause and reload configs
              LATConfig.pause();
              LATConfig.restoreDefaultConfigFile();
              if(LATConfig.error.length() > 0) {
                context.getSource().sendFeedback(() -> Text.literal("Living Adventure:Trails "+LATConfig.error), false);
              }
              LATConfig.registerConfigs();

              // If we were running before, run again
              if(!paused) LATConfig.run();
              return 1;
            })
        )
      )
    );

    LivingAdventureTrails.mcVersion = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: onInitialize start minecraft version "+LivingAdventureTrails.mcVersion);

    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LATConfig.registerConfigs();

    playerMoved = new PlayerMoved();

    ServerTickEvents.END_WORLD_TICK.register((serverWorld) -> {
      // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: tick");

      for (ServerPlayerEntity player : serverWorld.getPlayers()) {
        playerMoved.onMove(player, serverWorld);
      }
    });

    // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: onInitialize complete");

    LATConfig.run();
  }

  public static boolean entityIsHorseish(Entity entity) {
    if(
      entity instanceof HorseEntity
      || entity instanceof PigEntity
      || entity instanceof SkeletonHorseEntity
      || entity instanceof DonkeyEntity
      || entity instanceof MuleEntity
      || entity instanceof StriderEntity
      || entity instanceof CamelEntity
    ) return true;
    return false;
  }

  public static boolean entityCanAutoTrail(Entity entity) {
    if(
      (
        entity instanceof ServerPlayerEntity
        && !((ServerPlayerEntity)entity).isCreative()
        && !((ServerPlayerEntity)entity).isSpectator()
      )
      || (
        (
          entity instanceof HorseEntity
          || entity instanceof PigEntity
          || entity instanceof SkeletonHorseEntity
          || entity instanceof DonkeyEntity
          || entity instanceof MuleEntity
          || entity instanceof StriderEntity
          || entity instanceof CamelEntity
        )
        && entity.hasPlayerRider()
      )
    ) return true;
    return false;
  }

  // Returns a random number between 0 and bound
  public static double getRandomBounded(double bound) {
    Random rand = new Random();
    return (double) Math.abs(rand.nextInt()) / (double) Integer.MAX_VALUE * bound;
  }

  public static double getDivisor(String type, boolean hasTool) {
    switch(type) {
      case "step":
        if(hasTool) return LATConfig.DIVISOR_STEPTOOL;
        return LATConfig.DIVISOR_STEP;
      case "jump":
        if(hasTool) return LATConfig.DIVISOR_JUMPTOOL;
        return LATConfig.DIVISOR_JUMP;
      case "horse":
        if(hasTool) return LATConfig.DIVISOR_HORSETOOL;
        return LATConfig.DIVISOR_HORSE;
    }
    return 0;
  }


  public static boolean verAtLeast(String v2) {
    String[] ver1 = LivingAdventureTrails.mcVersion.split("\\.");
    String[] ver2 = v2.split("\\.");

    for (int i = 0; i < ver2.length; i++) {
      if(ver1.length < ver2.length) return false;
      // If any part is greater than the check version, then true
      if(Integer.parseInt(ver1[i]) > Integer.parseInt(ver2[i])) return true;
      // If any part is less than the check version, then false
      if(Integer.parseInt(ver1[i]) < Integer.parseInt(ver2[i])) return false;
    }
    // All parts must be exactly equal at this point, return true
    return true;
  }

  public static void horseLeafBreaker(World world, BlockPos pos, Entity entity) {
    Direction direction = entity.getHorizontalFacing();
    float yaw = entity.getYaw() + 360;
    int cardinal = Math.round(yaw / 45f) % 8;

    breakLeafColumn(world, pos.add(0,0,0));

    switch (cardinal) {
      case 0: // south
        breakLeafColumn(world, pos.add(-1,0,1));
        breakLeafColumn(world, pos.add(0,0,1));
        breakLeafColumn(world, pos.add(1,0,1));
        break;
      case 1: // south-west
        breakLeafColumn(world, pos.add(-1,0,1));
        breakLeafColumn(world, pos.add(0,0,1));
        breakLeafColumn(world, pos.add(-1,0,0));
        break;
      case 2: // west
        breakLeafColumn(world, pos.add(-1,0,-1));
        breakLeafColumn(world, pos.add(-1,0,0));
        breakLeafColumn(world, pos.add(-1,0,1));
        break;
      case 3: // north-west
        breakLeafColumn(world, pos.add(-1,0,-1));
        breakLeafColumn(world, pos.add(0,0,-1));
        breakLeafColumn(world, pos.add(-1,0,0));
        break;
      case 4: // north
        breakLeafColumn(world, pos.add(-1,0,-1));
        breakLeafColumn(world, pos.add(0,0,-1));
        breakLeafColumn(world, pos.add(1,0,-1));
        break;
      case 5: // north-east
        breakLeafColumn(world, pos.add(1,0,-1));
        breakLeafColumn(world, pos.add(0,0,-1));
        breakLeafColumn(world, pos.add(1,0,0));
        break;
      case 6: // east
        breakLeafColumn(world, pos.add(1,0,-1));
        breakLeafColumn(world, pos.add(1,0,0));
        breakLeafColumn(world, pos.add(1,0,1));
        break;
      case 7: // south-east
        breakLeafColumn(world, pos.add(1,0,1));
        breakLeafColumn(world, pos.add(0,0,1));
        breakLeafColumn(world, pos.add(1,0,0));
        break;
    };
  }

  public static void breakLeafColumn(World world, BlockPos pos) {
    for(int y = -1; y <= 3; y++) {
      if(world.getBlockState(pos.add(0,y,0)).isIn(BlockTags.LOGS)) break; // Don't break any leaves above logs
      breakLeaves(world, pos.add(0,y,0));
    }
  }

  public static void breakLeaves(World world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    if(
      (
        state.isIn(BlockTags.LEAVES)
        && !state.get(LeavesBlock.PERSISTENT)
      )
      || state.isOf(Blocks.VINE)
    ) {
      world.breakBlock(pos, true);
      BlockState newBlockState = Block.pushEntitiesUpBeforeBlockChange(state, Blocks.AIR.getDefaultState(), world, pos);
      world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
      world.updateComparators(pos, Blocks.AIR);
    }
  }

}
