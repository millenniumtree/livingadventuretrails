package com.millenniumtree.livingadventuretrails;

import com.millenniumtree.livingadventuretrails.config.LATConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class LivingAdventureTrails implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("livingadventuretrails");

	public static final String MOD_ID = "livingadventuretrails";

	public static PlayerMoved playerMoved;

	public static int getEntityTransitionBoost(Entity entity) {
		int boost = 1;
		PlayerEntity playerEntity = null;
		if(entity instanceof PlayerEntity) playerEntity = ((PlayerEntity) entity);
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
			playerEntity = ((PlayerEntity) entity.getControllingPassenger());
		}
		if(playerEntity == null) return 1;
		for (ItemStack itemStack : playerEntity.getHandItems()) {
			if(itemStack.getItem() == Items.WOODEN_SHOVEL) boost = 5;
		}
		return boost;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		copyDefaultConfig();

		LATConfig.registerConfigs();

		playerMoved = new PlayerMoved();

		ServerTickEvents.END_WORLD_TICK.register((serverWorld) -> {
			for (ServerPlayerEntity player : serverWorld.getPlayers()) {
				playerMoved.onMove(player, serverWorld);
			}
		});

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: onInitialize complete");

	}

	public static boolean entityCanAutoTrail(Entity entity) {
		if(
			(
				entity instanceof PlayerEntity
				&& !((PlayerEntity)entity).isCreative()
				&& !((PlayerEntity)entity).isSpectator()
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

	public static double getDivisor(String type) {
		switch(type) {
			case "step":
				return LATConfig.DIVISOR_STEP;
			case "jump":
				return LATConfig.DIVISOR_JUMP;
			case "horse":
				return LATConfig.DIVISOR_HORSE;
		}
		return 0;
	}

	private void copyDefaultConfig() {
		// Specify the ID of your mod
		String modID = "livingadventuretrails";

		// Specify the default configuration string
		String defaultConfigString = "# Lower the divisor to make all transitions more common (suggested: 2500-10000)\n" +
			"divisor,step = 5000\n" +
			"divisor,jump = 4000\n" +
			"divisor,horse = 3000\n" +
			"\n" +
			"# Rules are defined as a BLOCK FILTER, with a set of one or more TRANSITION RULES\n" +
			"# BLOCK FILTER (comma-delimited set of parameters)\n" +
			"#   Action: one of [autotrail, fragile]\n" +
			"#   World: one of [overworld, the_nether, the_end]\n" +
			"#   Block ID: a Block ID ('minecraft:' prefix should not be used, but prefixes from other mods can be)\n" +
			"# = (TRANSITION RULES)\n" +
			"# Multiple transition rules can be separated by semicolons (;)\n" +
			"# Each transition rule is defined like this:\n" +
			"#   Block ID: a Block ID ('minecraft:' prefix should not be used, but prefixes from other mods can be)\n" +
			"#   Frequency: An frequency (integer number):\n" +
			"#     Higher frequency numbers are more likely to cause the block transition or breakage.\n" +
			"#   Optional flags: zero or mo of the following:\n" +
			"#     Optional flag 'random_orientation': used for deepslate to 'scuff up' the path\n" +
			"#     Optional flag 'loot': used to drop loot like seeds from grass, or saplings from leaves\n" +
			"#     Optional Y selector: one of [below #, above #]\n" +
			"\n" +
			"# The following autotrail transitions apply anywhere in the overworld\n" +
			"autotrail,overworld,podzol = (dirt,100; rooted_dirt,75; coarse_dirt,50; packed_mud,25)\n" +
			"autotrail,overworld,mycelium = (dirt,100; coarse_dirt,30; mud,30; packed_mud,10; rooted_dirt,20; brown_mushroom_block,10)\n" +
			"autotrail,overworld,grass_block = (dirt,100; rooted_dirt,25; coarse_dirt,25)\n" +
			"autotrail,overworld,dirt = (coarse_dirt,75; rooted_dirt,50; mud,15)\n" +
			"autotrail,overworld,coarse_dirt = (rooted_dirt,75; dirt,25; mud,15; packed_mud,5)\n" +
			"autotrail,overworld,rooted_dirt = (coarse_dirt,75; dirt,25; mud,5; packed_mud,10)\n" +
			"autotrail,overworld,packed_mud = (mud,50; smooth_basalt,25; rooted_dirt,25; podzol,10)\n" +
			"autotrail,overworld,mud = (smooth_basalt,50; packed_mud,10)\n" +
			"autotrail,overworld,smooth_basalt = (mud,50; cobbled_deepslate,25,below 0)\n" +
			"\n" +
			"autotrail,overworld,sand = (smooth_sandstone,50, gravel,20, red_sand,10)\n" +
			"autotrail,overworld,smooth_sandstone = (stripped_birch_wood,10; stripped_oak_wood,10; stripped_jungle_wood,10)\n" +
			"\n" +
			"autotrail,overworld,stone = (cobblestone,20; andesite,20; mossy_cobblestone,20)\n" +
			"autotrail,overworld,andesite = (cobblestone,40; mossy_cobblestone,20)\n" +
			"autotrail,overworld,cobblestone = (tuff,30; andesite,15; mossy_cobblestone,15)\n" +
			"autotrail,overworld,tuff = (cobblestone,40; mossy_cobblestone,20)\n" +
			"autotrail,overworld,mossy_cobblestone = (mossy_stone_bricks,40; cobblestone,20)\n" +
			"autotrail,overworld,mossy_stone_bricks = (stone_bricks,60; mossy_cobblestone,50)\n" +
			"autotrail,overworld,stone_bricks = (cracked_stone_bricks,5)\n" +
			"\n" +
			"autotrail,overworld,snow_block = (ice,25)\n" +
			"autotrail,overworld,ice = (packed_ice,25)\n" +
			"autotrail,overworld,packed_ice = (blue_ice,25)\n" +
			"\n" +
			"autotrail,overworld,gravel = (dead_fire_coral_block,10; dead_horn_coral_block,10; dead_brain_coral_block,10; dead_tube_coral_block,10; dead_bubble_coral_block,10)\n" +
			"\n" +
			"autotrail,overworld,red_sand = (smooth_red_sandstone,50; sand,10)\n" +
			"autotrail,overworld,smooth_red_sandstone = (orange_terracotta,50)\n" +
			"autotrail,overworld,orange_terracotta = (brown_concrete_powder,50)\n" +
			"\n" +
			"autotrail,overworld,deepslate_bricks = (cracked_deepslate_bricks,5)\n" +
			"autotrail,overworld,deepslate_tiles = (cracked_deepslate_tiles,5)\n" +
			"autotrail,overworld,polished_blackstone_bricks = (cracked_polished_blackstone_bricks,5)\n" +
			"\n" +
			"# The following autotrail transitions only apply in the overworld below Y 0\n" +
			"autotrail,overworld,deepslate = (deepslate,75,random_orientation,below 0; cobbled_deepslate,25,below 0)\n" +
			"autotrail,overworld,cobbled_deepslate = (smooth_basalt,50,below 0; deepslate,25,below 0)\n" +
			"# See the additional smooth_basalt transitions above\n" +
			"\n" +
			"# The following will break leaves if you step on them\n" +
			"autotrail,overworld,acacia_leaves = (air,50,loot)\n" +
			"autotrail,overworld,azalea_leaves = (air,50,loot)\n" +
			"autotrail,overworld,birch_leaves = (air,50,loot)\n" +
			"autotrail,overworld,cherry_leaves = (air,50,loot)\n" +
			"autotrail,overworld,dark_oak_leaves = (air,50,loot)\n" +
			"autotrail,overworld,flowering_azalea_leaves = (air,50,loot)\n" +
			"autotrail,overworld,jungle_leaves = (air,50,loot)\n" +
			"autotrail,overworld,oak_leaves = (air,50,loot)\n" +
			"autotrail,overworld,spruce_leaves = (air,50,loot)\n" +
			"\n" +
			"# The following autotrail transitions only apply in the nether dimension\n" +
			"autotrail,the_nether,soul_sand = (dead_tube_coral_block,25; dead_brain_coral_block,25)\n" +
			"\n" +
			"autotrail,the_nether,soul_soil = (mud,25; smooth_basalt,25)\n" +
			"autotrail,the_nether,smooth_basalt = (coal_block,10)\n" +
			"\n" +
			"autotrail,the_nether,netherrack = (red_terracotta,50)\n" +
			"autotrail,the_nether,red_terracotta = (nether_bricks,25)\n" +
			"autotrail,the_nether,nether_bricks = (cracked_nether_bricks,5)\n" +
			"\n" +
			"autotrail,the_nether,crimson_nylium = (netherrack,50)\n" +
			"autotrail,the_nether,warped_nylium = (netherrack,50)\n" +
			"\n" +
			"# The following autotrail transitions only apply in the end dimension\n" +
			"autotrail,the_end,end_stone = (dead_bubble_coral_block,10; dead_horn_coral_block,10; dead_fire_coral_block,10; cobblestone,10; tuff,10)\n" +
			"\n" +
			"# The following are fragile transparent blocks that may be broken if walked through without sneaking.\n" +
			"# Break frequency is much higher than autotrails, and they drop appropriate loot when broken (as if broken without a tool)\n" +
			"fragile,overworld,snow = (air,1500)\n" +
			"fragile,overworld,grass = (air,1500,loot)\n" +
			"fragile,overworld,tall_grass = (air,1500,loot)\n" +
			"fragile,overworld,poppy = (air,1500,loot)\n" +
			"fragile,overworld,dandelion = (air,600,loot)\n" +
			"fragile,overworld,blue_orchid = (air,1500,loot)\n" +
			"fragile,overworld,allium = (air,1500,loot)\n" +
			"fragile,overworld,fern = (air,1500,loot)\n" +
			"fragile,overworld,large_fern = (air,1500,loot)\n" +
			"fragile,overworld,dead_bush = (air,1500,loot)\n" +
			"fragile,overworld,azure_bluet = (air,1500,loot)\n" +
			"fragile,overworld,oxeye_daisy = (air,1500,loot)\n" +
			"fragile,overworld,pink_petals = (air,1500,loot)\n" +
			"fragile,overworld,tube_coral = (water,1000,loot)\n" +
			"fragile,overworld,tube_coral_fan = (water,1500,loot)\n" +
			"fragile,overworld,brain_coral = (water,1000,loot)\n" +
			"fragile,overworld,brain_coral_fan = (water,1500,loot)\n" +
			"fragile,overworld,horn_coral = (water,1000,loot)\n" +
			"fragile,overworld,horn_coral_fan = (water,1500,loot)\n" +
			"fragile,overworld,fire_coral = (water,1000,loot)\n" +
			"fragile,overworld,fire_coral_fan = (water,1500,loot)\n" +
			"fragile,overworld,bubble_coral = (water,1000,loot)\n" +
			"fragile,overworld,bubble_coral_fan = (water,1500,loot)\n" +
			"fragile,overworld,red_tulip = (air,1200,loot)\n" +
			"fragile,overworld,orange_tulip = (air,1200,loot)\n" +
			"fragile,overworld,white_tulip = (air,1200,loot)\n" +
			"fragile,overworld,pink_tulip = (air,1200,loot)\n" +
			"#fragile,overworld,glow_lichen = (air,1200,loot)\n" +
			"fragile,overworld,cornflower = (air,1200,loot)\n" +
			"fragile,overworld,sunflower = (air,800,loot)\n" +
			"fragile,overworld,peony = (air,800,loot)\n" +
			"fragile,overworld,rose_bush = (air,800,loot)\n" +
			"fragile,overworld,lilac = (air,800,loot)\n" +
			"#fragile,overworld,oak_sapling = (air,800,loot)\n" +
			"#fragile,overworld,spruce_sapling = (air,800,loot)\n" +
			"#fragile,overworld,birch_sapling = (air,800,loot)\n" +
			"#fragile,overworld,jungle_sapling = (air,800,loot)\n" +
			"#fragile,overworld,acacia_sapling = (air,800,loot)\n" +
			"#fragile,overworld,dark_oak_sapling = (air,800,loot)\n" +
			"#fragile,overworld,mangrove_propagule = (air,800,loot)\n" +
			"#fragile,overworld,cherry_sapling = (air,800,loot)\n" +
			"fragile,overworld,brown_mushroom = (air,1500,loot)\n" +
			"fragile,overworld,red_mushroom = (air,1500,loot)\n" +
			"fragile,overworld,crimson_fungus = (air,800,loot)\n" +
			"fragile,overworld,warped_fungus = (air,800,loot)\n" +
			"#fragile,overworld,sugar_cane = (air,100,loot)\n" +
			"fragile,overworld,small_dripleaf = (air,100,loot)\n" +
			"\n" +
			"fragile,the_nether,nether_sprouts = (air,1200,loot)\n" +
			"fragile,the_nether,warped_roots = (air,1200,loot)\n" +
			"fragile,the_nether,crimson_roots = (air,1200,loot)\n" +
			"fragile,the_nether,nether_wart = (air,800,loot)\n";

		// Specify the path where you want to copy the default configuration file in the config directory
		Path targetConfigPath = FabricLoader.getInstance().getConfigDir().resolve(modID+".properties");

//    LivingAdventureTrails.LOGGER.info("LivingAdventureTrails: copyDefaultConfig targetConfigPath " + targetConfigPath.toString());

		// Check if the target config file already exists
		try {
			if (
				!Files.exists(targetConfigPath)
				|| Files.size(targetConfigPath) < 10
			) {
				// Create the parent directories if they don't exist
				Files.createDirectories(targetConfigPath.getParent());

				// Write the default configuration string to the config file
				Files.write(targetConfigPath, defaultConfigString.getBytes());
			}
		} catch (IOException e) {
//				e.printStackTrace();
		}
	}

}
