package com.millenniumtree.livingadventuretrails.mixin;

import com.millenniumtree.livingadventuretrails.TransitionRule;
import com.millenniumtree.livingadventuretrails.LivingAdventureTrails;
import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Block.class)
public class BlockSteppedOn {
	@Unique
	private static Map<Integer, BlockPos> lastPositions = new HashMap<Integer, BlockPos>();

	@Inject(at = @At("TAIL"), method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V")
	private void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
    if(world.isClient) return;
    if(LATConfig.isPaused()) return;
    if(entity.isSneaking()) return;

    ServerPlayerEntity player;
    if(entity.isPlayer()) {
      player = (ServerPlayerEntity) entity;
    } else if(entity.hasPlayerRider()) {
      player = ((ServerPlayerEntity) entity.getControllingPassenger());
    } else {
      return;
    }
    if(player == null) return;
    if(LATConfig.isPaused(player)) return;
    if(blockAbovePreventsTransition(world, pos)) return;

		if(LivingAdventureTrails.entityCanAutoTrail(player)) {
      // Don't break persistent leaves
      if(state.isIn(BlockTags.LEAVES) && state.get(LeavesBlock.PERSISTENT)) return;

			Block touchedBlock = state.getBlock();
			String worldID = world.getRegistryKey().getValue().toString();
			String blockID = Registries.BLOCK.getId(touchedBlock).toString();
//			LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: BlockSteppedOn.onLandedUpon "+blockID);

			if(LATConfig.autoTrailRules.ifTrigger(worldID, blockID, "jump", LivingAdventureTrails.hasTool(player))) {
//				LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: BlockSteppedOn.onLandedUpon");
				TransitionRule transitionRule = LATConfig.autoTrailRules.getRandomTransitionRule(worldID, blockID, "jump");

				if(
					pos.getY() < transitionRule.belowY
					&& pos.getY() > transitionRule.aboveY
				) {
					Block nextBlock = Registries.BLOCK.get(Identifier.tryParse(transitionRule.blockID));

					if (transitionRule.loot) {
						world.breakBlock(pos, transitionRule.loot);
					}
          BlockState newState = nextBlock.getDefaultState();
//          if(
//            newState.isOf(Blocks.DIRT_PATH)
//            &&
//          )
          if(transitionRule.randomOrientation) {
            newState = newState.with(Properties.AXIS, transitionRule.getRandomOrientation());
          }
					BlockState newBlockState = Block.pushEntitiesUpBeforeBlockChange(state, newState, world, pos);

					world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
					world.updateComparators(pos, nextBlock);
				}
			}
		}

	}

	@Inject(at = @At("TAIL"), method = "onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;)V")
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
    if(world.isClient) return;
    if(LATConfig.isPaused()) return;
    if(entity.isSneaking()) return;

    ServerPlayerEntity player;
    if(entity.isPlayer()) {
      player = (ServerPlayerEntity) entity;
    } else if(entity.hasPlayerRider()) {
      player = ((ServerPlayerEntity) entity.getControllingPassenger());
    } else {
      return;
    }
    if(LATConfig.isPaused(player)) return;
    if(blockAbovePreventsTransition(world, pos)) return;

    if (
			(
				lastPositions.get(entity.getId()) == null
				|| !lastPositions.get(entity.getId()).equals(pos)
			)
		  && LivingAdventureTrails.entityCanAutoTrail(entity)
		) {
			lastPositions.put(entity.getId(), pos);

			Block touchedBlock = state.getBlock();
			String worldID = world.getRegistryKey().getValue().toString();
			String blockID = Registries.BLOCK.getId(touchedBlock).toString();

//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: onSteppedOn "+pos.getX()+","+pos.getY()+","+pos.getZ());

      String stepType = LivingAdventureTrails.entityIsHorseish(entity)? "horse": "step";
//      LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: stepType "+stepType);

      if(stepType == "horse") {
//        LivingAdventureTrails.horseLeafBreaker(world, pos, entity);

//        LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: blocks around " + pos.getX() + "," + pos.getY() + "," + pos.getZ()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(-1, 1, -1)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(-1, 1, 0)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(-1, 1, 1)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(0, 1, -1)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(0, 1, 1)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(1, 1, -1)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(1, 1, 0)).getBlock()).toString()
//          + " " + Registries.BLOCK.getId(world.getBlockState(pos.add(1, 1, 1)).getBlock()).toString()
//        );
      }

			if(LATConfig.autoTrailRules.ifTrigger(worldID, blockID, stepType, LivingAdventureTrails.hasTool(entity))) {
				TransitionRule transitionRule = LATConfig.autoTrailRules.getRandomTransitionRule(worldID, blockID, "step");
				Block nextBlock = Registries.BLOCK.get(Identifier.tryParse(transitionRule.blockID));

				if(transitionRule.loot) {
					world.breakBlock(pos, transitionRule.loot);
				}
        BlockState newState = nextBlock.getDefaultState();
        if(transitionRule.randomOrientation) {
          newState = newState.with(Properties.AXIS, transitionRule.getRandomOrientation());
        }

				BlockState newBlockState = Block.pushEntitiesUpBeforeBlockChange(state, newState, world, pos);
				world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
				world.updateComparators(pos, nextBlock);
			}
		}
	}

  public BlockState blockStateAbove(World world, BlockPos pos) {
    return world.getBlockState(pos.add(0,1,0));
  }

  public boolean blockAbovePreventsTransition(World world, BlockPos pos) {
    BlockState state = blockStateAbove(world, pos);
    if(state.isIn(BlockTags.DOORS)) return true;
    if(state.isIn(BlockTags.WOOL_CARPETS)) return true;
    if(state.isIn(BlockTags.MOSS_REPLACEABLE)) return true;
    if(state.getBlock() instanceof TorchBlock) return true;
    if(state.getBlock() instanceof LanternBlock) return true;
    return false;
  }
}
