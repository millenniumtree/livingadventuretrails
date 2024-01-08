package com.millenniumtree.livingadventuretrails;

import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerMoved {

//  private static BlockPos lastPosition = new BlockPos(0,0,0);
  private static Map<Integer, BlockPos> lastPositions = new HashMap<Integer, BlockPos>();

  public void onMove(PlayerEntity player, ServerWorld serverWorld) {
    if (serverWorld.isClient) return;

    BlockPos playerPos = player.getBlockPos();

    if (
      !player.isSneaking()
      && (
        lastPositions.get(player.getId()) == null
        || !lastPositions.get(player.getId()).equals(playerPos)
      )
      && LivingAdventureTrails.entityCanAutoTrail(player)
    ) {
      lastPositions.put(player.getId(), playerPos);

      Block touchedBlock = serverWorld.getBlockState(playerPos).getBlock();

      String worldID = serverWorld.getRegistryKey().getValue().toString();
      String blockID = Registries.BLOCK.getId(touchedBlock).toString();

      if(LATConfig.fragileRules.ifTrigger(worldID, blockID, "step", LivingAdventureTrails.getEntityTransitionBoost(player))) {
        TransitionRule transitionRule = LATConfig.fragileRules.getRandomTransitionRule(worldID, blockID, "step");

        Block nextBlock = Registries.BLOCK.get(new Identifier(transitionRule.blockID));

        // Player trampled a fragile block - break it as if with the empty hand, and drop the loot, if necessary
        serverWorld.breakBlock(playerPos, transitionRule.loot);
        serverWorld.setBlockState(playerPos, nextBlock.getDefaultState(), Block.NOTIFY_LISTENERS);
        serverWorld.updateComparators(playerPos, nextBlock);
      }
    }
  }
}
