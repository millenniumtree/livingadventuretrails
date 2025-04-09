package com.millenniumtree.livingadventuretrails;

import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

public class PlayerMoved {

//  private static BlockPos lastPosition = new BlockPos(0,0,0);
  private static Map<Integer, BlockPos> lastPositions = new HashMap<Integer, BlockPos>();

  public void onMove(ServerPlayerEntity player, ServerWorld serverWorld) {
    if(serverWorld.isClient) return;
    if(LATConfig.isPaused(player)) return;
    if(player.isSneaking()) return;
    BlockPos playerPos = player.getBlockPos();

    if (
      (
        lastPositions.get(player.getId()) == null
        || !lastPositions.get(player.getId()).equals(playerPos)
      )
      && LivingAdventureTrails.entityCanAutoTrail(player)
    ) {
      lastPositions.put(player.getId(), playerPos);

      BlockState touchedBlockState = serverWorld.getBlockState(playerPos);
      Block touchedBlock = touchedBlockState.getBlock();

      String worldID = serverWorld.getRegistryKey().getValue().toString();
      String blockID = Registries.BLOCK.getId(touchedBlock).toString();

      String stepType = "step";
      if(
        player.hasVehicle()
          && LivingAdventureTrails.entityIsHorseish(player.getVehicle())
      ) {
        stepType = "horse";
        if(LivingAdventureTrails.hasLeafTool(player)) {
          LivingAdventureTrails.horseLeafBreaker(serverWorld, playerPos, player);
        }
      }

      if(blockID.equals("minecraft:air")) return;
      if(blockID.equals("minecraft:water")) return;

//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: stepType "+stepType);

      if(LATConfig.fragileRules.ifTrigger(worldID, blockID, stepType, LivingAdventureTrails.hasTool(player))) {
        TransitionRule transitionRule = LATConfig.fragileRules.getRandomTransitionRule(worldID, blockID, stepType);
        // LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: fragileRules.ifTrigger "+transitionRule.blockID);

        Block nextBlock = Registries.BLOCK.get(Identifier.tryParse(transitionRule.blockID));

        // Player trampled a fragile block - break it as if with the empty hand, and drop the loot, if necessary
        serverWorld.breakBlock(playerPos, transitionRule.loot);
        serverWorld.setBlockState(playerPos, nextBlock.getDefaultState(), Block.NOTIFY_LISTENERS);
        serverWorld.updateComparators(playerPos, nextBlock);
      }
    }
  }
}
