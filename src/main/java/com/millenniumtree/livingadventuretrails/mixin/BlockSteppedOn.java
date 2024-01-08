package com.millenniumtree.livingadventuretrails.mixin;

import com.millenniumtree.livingadventuretrails.TransitionRule;
import com.millenniumtree.livingadventuretrails.LivingAdventureTrails;
import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
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
		if(
			!world.isClient
			&& !entity.isSneaking()
			&& LivingAdventureTrails.entityCanAutoTrail(entity)
		) {
			Block touchedBlock = state.getBlock();
			String worldID = world.getRegistryKey().getValue().toString();
			String blockID = Registries.BLOCK.getId(touchedBlock).toString();
//			LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: BlockSteppedOn.onLandedUpon "+blockID);

			if(LATConfig.autoTrailRules.ifTrigger(worldID, blockID, "jump", LivingAdventureTrails.getEntityTransitionBoost(entity))) {
//				LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: BlockSteppedOn.onLandedUpon");
				TransitionRule transitionRule = LATConfig.autoTrailRules.getRandomTransitionRule(worldID, blockID, "jump");

				if(
					pos.getY() < transitionRule.belowY
					&& pos.getY() > transitionRule.aboveY
				) {
					Block nextBlock = Registries.BLOCK.get(new Identifier(transitionRule.blockID));

					if (transitionRule.loot) {
						world.breakBlock(pos, transitionRule.loot);
					}
					BlockState newBlockState = Block.pushEntitiesUpBeforeBlockChange(state, nextBlock.getDefaultState(), world, pos);
					world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
					world.updateComparators(pos, nextBlock);
				}
			}
		}

	}

	@Inject(at = @At("TAIL"), method = "onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;)V")
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
		if (
			!world.isClient
			&& !entity.isSneaking()
			&& (
				lastPositions.get(entity.getId()) == null
				|| !lastPositions.get(entity.getId()).equals(pos)
			)
		  && LivingAdventureTrails.entityCanAutoTrail(entity)
		) {
			lastPositions.put(entity.getId(), pos);

			Block touchedBlock = state.getBlock();
			String worldID = world.getRegistryKey().getValue().toString();
			String blockID = Registries.BLOCK.getId(touchedBlock).toString();

			if(LATConfig.autoTrailRules.ifTrigger(worldID, blockID, "step", LivingAdventureTrails.getEntityTransitionBoost(entity))) {
				TransitionRule transitionRule = LATConfig.autoTrailRules.getRandomTransitionRule(worldID, blockID, "step");
				Block nextBlock = Registries.BLOCK.get(new Identifier(transitionRule.blockID));

				if(transitionRule.loot) {
					world.breakBlock(pos, transitionRule.loot);
				}
				BlockState newBlockState = Block.pushEntitiesUpBeforeBlockChange(state, nextBlock.getDefaultState(), world, pos);
				world.setBlockState(pos, newBlockState, Block.NOTIFY_LISTENERS);
				world.updateComparators(pos, nextBlock);
			}
		}
	}

}
