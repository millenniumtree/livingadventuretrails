![Living Adventure: Trails logo](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-logo.jpg?itok=WVG55Uv6 "Living Adventure: Trails logo")

Welcome to Living Adventure: Trails
==================

Living Adventure: Trails is a Minecraft mod that gradually creates beautiful trails everywhere you roam.  The more you walk around, the more the trail will grow.

Features
--------

- Fully customizable with a config file.
- Trails are created in the the overworld, nether and the end!
- Trails are created at different rates for walking, jumping, or riding a horse.
- Trails will take longer to generate on harder materials
- Trail building can be different above or below certain Y coordinates (configurable)
- "Fragile" blocks such as tall grass, crops, flowers, mushrooms, snow layers, etc, have a chance of breaking when stepped on.
- Carrying a wooden shovel in either hand increases the trail-making 5-fold, for quickly connecting remote villages and other points of interest.
- No blocks are changed while sneaking

**Please feel free to send me a tip, or buy me a coffee, with the donate button above.**

I have tried to make some sensible block transitions/palettes for all vanilla biomes.

Examples
--------

- Grass, dirt, podzol, and mycelium all convert to rooted dirt, coarse dirt, then to mud, packed mud, smooth basalt, etc.
- Sand will convert to smooth sandstone, then a few similarly-colored planks
- Gravel will convert to dead coral blocks
- Ice will gradually become packed ice, then blue ice
- Netherrack will convert to red terracotta, then nether bricks
- End stone will convert to various dead coral blocks

Questions & Answers
-------------------

#### How are different gamemodes handled?
No trails are generated in creative or spectator mode, only in survival

#### What if I don't like the path_block?
The default configuration does not use the path_block, because it can stop boat movement, so some people may prefer not to use it. Also, if a path block is created under a door, the door will pop off.  If you'd like path blocks anyway, you may edit the config, or just create them with a shovel.  If you enable it, remember to put your doors and torches on blocks that won't ever convert to path blocks.

#### Where is the config file?
The default config is written out when you first load the mod, or if you delete or empty the existing config. The configuration file is located in your minecraft folder at .minecraft/config/livingadventuretrails.properties

#### How does the config file work?
The configuration file contains useful comments (beginning with a #) to help you make changes.
The divisor lines at the top control the overall speed of block conversion.  HIGHER divisors will slow down the conversion.  LOWER divisors will speed it up.

````
divisor,step = 4000
````


````
divisor,jump = 3000
````


````
divisor,horse = 2500
````

I recommend not lowering any divisor below the total of any 'autotrail' or 'fragile' line, as this will result in a 100% conversion rate, and look kind of terrible.

The 'autotrail' lines in the config look like this:

````
autotrail,overworld,mud = (smooth_basalt,50; packed_mud,10)
````

In this example, in the overworld, mud will be converted into either smooth_basalt, or packed mud.  There's a 60 (50+10) in 4000 (the divisor) chance when you step on a mud block that it will be converted.  smooth_basalt is 5x more likely than packed_mud.

The 'fragile' lines work identically, except that they break the block, and drop loot.

````
fragile,overworld,tall_grass = (air,1500,loot)
````

In this case, the tall_grass is very likely to break (1500 / 4000 chance), and the 'loot' tag means it will drop seeds with the same chance as if you broke it with your hand.

#### Can I temporarily disable some of the rules in the config file?
Yes, If you don't like a certain set of options in the config, simply comment them out with a # at the beginning of the line, or delete the line entirely.
Again, if you want to restore the configuration to the defaults, or get the new default configs, if you get a new version of the mod, just delete or empty the config file, and restart the game.

#### How does it handle plants that you can walk through?
Almost all growing plants are considered 'fragile', and have a decent chance of breaking if you walk through them. You can trample flowers, grass, saplings, vines, etc. Doing this will drop their loot as items, the same way as breaking them with an open hand.  If you don't like certain plants breaking, comment the lines out in the config file.

#### Can you cheat with it?
Yes, of course.  It's fully configurable.  If you want netherite blocks by jumping on pink glazed terracotta, that's entirely up to you.
On the other hand, if you wanted to record a "Minecraft, but jumping on podzol is OP" video, this is the mod for you! xD

#### Will it work with other modded blocks?
Probably. Use F3 to determine the block id, and enter the MODNAME:BLOCKNAME into the config. You do NOT need to prefix vanilla minecraft blocks with "minecraft:" in the config, but you will for modded blocks.  I have not yet tested this feature, but I don't see why it shouldn't work.

#### Example images below:

![Living Adventure: Trails through a warped forest](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-warped.jpg?itok=uuAf9LtM "Living Adventure: Trails through a warped forest")

![Living Adventure: Trails through a crimson forest](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-crimson.jpg?itok=gQe-Y3QI "Living Adventure: Trails through a crimson forest")

![Living Adventure: Trails through the end](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-end.jpg?itok=PFHh4evj "Living Adventure: Trails through the end")

![Living Adventure: Trails through a basalt delta](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-basalt.jpg?itok=au0w0B-i "Living Adventure: Trails through a basalt delta")

![Living Adventure: Trails through a snowy biome](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-snow.jpg?itok=fkKzF02h "Living Adventure: Trails through a snowy biome")

![Living Adventure: Trails in a base with a stone floor](https://kupa-kona.com/sites/kupa-kona.com/files/styles/large/public/2024-01/living-adventure-trails-base.jpg?itok=SrkrMRRC "Living Adventure: Trails in a base with a stone floor")
