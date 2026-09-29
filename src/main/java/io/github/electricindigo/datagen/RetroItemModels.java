package io.github.electricindigo.datagen;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.client.RiftRetroProperty;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public final class RetroItemModels
{
    private RetroItemModels() {}

    // Flat items: parent item/generated, one texture
    private static final List<String> FLAT_ITEMS = List.of(
            // Food and farming
            "apple", "baked_potato", "beef", "beetroot", "beetroot_seeds", "beetroot_soup", "bread", "cake",
            "carrot", "chicken", "chorus_fruit", "cocoa_beans", "cooked_beef", "cooked_chicken",
            "cooked_mutton", "cooked_porkchop", "cooked_rabbit", "cookie", "egg", "glistering_melon_slice",
            "golden_apple", "golden_carrot", "melon_seeds", "melon_slice", "mushroom_stew", "mutton",
            "poisonous_potato", "popped_chorus_fruit", "porkchop", "potato", "pumpkin_pie", "rabbit",
            "rabbit_stew", "rotten_flesh", "spider_eye", "sugar", "sugar_cane", "wheat",

            // Materials and everything else
            "armor_stand", "barrier", "bone_meal", "book", "bowl", "brewing_stand", "brick", "bucket",
            "cauldron", "charcoal", "chest_minecart", "clay_ball", "coal", "cod_bucket",
            "command_block_minecart", "comparator", "diamond", "diamond_horse_armor", "dragon_breath",
            "emerald", "enchanted_book", "end_crystal", "ender_eye", "ender_pearl", "experience_bottle",
            "fermented_spider_eye", "filled_map", "fire_charge", "firework_rocket", "flint",
            "flint_and_steel", "flower_pot", "furnace_minecart", "ghast_tear", "glass_bottle",
            "glowstone_dust", "gold_ingot", "gold_nugget", "golden_horse_armor", "gunpowder", "hopper",
            "hopper_minecart", "ink_sac", "iron_horse_armor", "iron_ingot", "item_frame", "knowledge_book",
            "lapis_lazuli", "lava_bucket", "leather", "magma_cream", "map", "milk_bucket", "minecart",
            "name_tag", "nether_brick", "nether_star", "painting", "paper", "phantom_membrane",
            "prismarine_crystals", "prismarine_shard", "pufferfish_bucket", "quartz", "rabbit_foot",
            "rabbit_hide", "redstone", "repeater", "saddle", "salmon_bucket", "shears", "shulker_shell",
            "slime_ball", "snowball", "spectral_arrow", "string", "structure_void", "tnt_minecart",
            "totem_of_undying", "tropical_fish_bucket", "water_bucket", "writable_book", "written_book",

            // Spawn eggs
            "allay_spawn_egg", "armadillo_spawn_egg", "axolotl_spawn_egg", "bat_spawn_egg", "bee_spawn_egg",
            "blaze_spawn_egg", "bogged_spawn_egg", "breeze_spawn_egg", "camel_spawn_egg", "cat_spawn_egg",
            "cave_spider_spawn_egg", "chicken_spawn_egg", "cod_spawn_egg", "cow_spawn_egg",
            "creaking_spawn_egg", "creeper_spawn_egg", "dolphin_spawn_egg", "donkey_spawn_egg",
            "drowned_spawn_egg", "elder_guardian_spawn_egg", "ender_dragon_spawn_egg", "enderman_spawn_egg",
            "endermite_spawn_egg", "evoker_spawn_egg", "fox_spawn_egg", "frog_spawn_egg", "ghast_spawn_egg",
            "glow_squid_spawn_egg", "goat_spawn_egg", "guardian_spawn_egg", "hoglin_spawn_egg",
            "horse_spawn_egg", "husk_spawn_egg", "iron_golem_spawn_egg", "llama_spawn_egg",
            "magma_cube_spawn_egg", "mooshroom_spawn_egg", "mule_spawn_egg", "ocelot_spawn_egg",
            "panda_spawn_egg", "parrot_spawn_egg", "phantom_spawn_egg", "pig_spawn_egg",
            "piglin_brute_spawn_egg", "piglin_spawn_egg", "pillager_spawn_egg", "polar_bear_spawn_egg",
            "pufferfish_spawn_egg", "rabbit_spawn_egg", "ravager_spawn_egg", "salmon_spawn_egg",
            "sheep_spawn_egg", "shulker_spawn_egg", "silverfish_spawn_egg", "skeleton_horse_spawn_egg",
            "skeleton_spawn_egg", "slime_spawn_egg", "sniffer_spawn_egg", "snow_golem_spawn_egg",
            "spider_spawn_egg", "squid_spawn_egg", "stray_spawn_egg", "strider_spawn_egg",
            "tadpole_spawn_egg", "trader_llama_spawn_egg", "tropical_fish_spawn_egg", "turtle_spawn_egg",
            "vex_spawn_egg", "villager_spawn_egg", "vindicator_spawn_egg", "wandering_trader_spawn_egg",
            "warden_spawn_egg", "witch_spawn_egg", "wither_skeleton_spawn_egg", "wither_spawn_egg",
            "wolf_spawn_egg", "zoglin_spawn_egg", "zombie_horse_spawn_egg", "zombie_spawn_egg",
            "zombie_villager_spawn_egg", "zombified_piglin_spawn_egg",

            // Explorer maps
            "abandoned_camp_map", "buried_ancient_city_map", "buried_mineshaft_map", "buried_treasure_map",
            "buried_trial_chambers_map", "desert_pyramid_map", "desert_village_map", "jungle_pyramid_map",
            "ocean_monument_map", "plains_village_map", "savanna_village_map", "snowy_village_map",
            "swamp_hut_map", "taiga_village_map", "warm_ocean_ruins_map", "woodland_mansion_map",

            // Dyes
            "black_dye", "blue_dye", "brown_dye", "cyan_dye", "gray_dye", "green_dye", "light_blue_dye",
            "light_gray_dye", "lime_dye", "magenta_dye", "orange_dye", "pink_dye", "purple_dye", "red_dye",
            "white_dye", "yellow_dye",

            // Boats, doors and signs
            "acacia_boat", "acacia_door", "acacia_sign", "birch_boat", "birch_door", "birch_sign",
            "dark_oak_boat", "dark_oak_door", "dark_oak_sign", "iron_door", "jungle_boat", "jungle_door",
            "jungle_sign", "oak_boat", "oak_door", "oak_sign", "spruce_boat", "spruce_door", "spruce_sign"
    );

    // Tools and weapons: parent item/handheld, one texture
    private static final List<String> HANDHELD_ITEMS = List.of(
            "diamond_axe", "diamond_hoe", "diamond_pickaxe", "diamond_shovel", "diamond_sword", "golden_axe",
            "golden_hoe", "golden_pickaxe", "golden_shovel", "golden_sword", "iron_axe", "iron_hoe",
            "iron_pickaxe", "iron_shovel", "iron_sword", "stone_axe", "stone_hoe", "stone_pickaxe",
            "stone_shovel", "stone_sword", "wooden_axe", "wooden_hoe", "wooden_pickaxe", "wooden_shovel",
            "wooden_sword"
    );

    // Full cubes with the same texture on every side (block/cube_all)
    private static final List<String> CUBE_BLOCK_ITEMS = List.of(
            "acacia_planks", "andesite", "bedrock", "birch_planks", "bricks", "chiseled_stone_bricks", "clay",
            "coal_block", "coal_ore", "cobblestone", "cracked_stone_bricks", "crying_obsidian",
            "dark_oak_planks", "dark_prismarine", "diamond_block", "diamond_ore", "diorite", "emerald_block",
            "emerald_ore", "end_stone", "end_stone_bricks", "glowstone", "gold_block", "gold_ore", "granite",
            "gravel", "ice", "iron_block", "iron_ore", "jungle_planks", "lapis_block", "lapis_ore",
            "mossy_cobblestone", "mossy_stone_bricks", "nether_bricks", "nether_quartz_ore",
            "nether_wart_block", "netherrack", "note_block", "oak_planks", "obsidian", "packed_ice",
            "polished_andesite", "polished_diorite", "polished_granite", "prismarine", "prismarine_bricks",
            "purpur_block", "red_nether_bricks", "red_sand", "redstone_block", "redstone_lamp",
            "redstone_ore", "sand", "smooth_stone", "soul_sand", "sponge", "spruce_planks", "stone",
            "stone_bricks", "terracotta", "wet_sponge"
    );

    // Every other simple block item: "<item> <model the item uses> <slot>=<texture> ..."
    // The retro model reuses the item's own vanilla model and only swaps each texture, so the shape always matches.
    // Generated from the real 26.3 models; only blocks whose every texture exists in Programmer Art are listed.
    private static final List<String> SHAPED_BLOCK_ITEMS = List.of(
            "acacia_button block/acacia_button_inventory texture=acacia_planks",
            "acacia_fence block/acacia_fence_inventory texture=acacia_planks",
            "acacia_fence_gate block/acacia_fence_gate texture=acacia_planks",
            "acacia_log block/acacia_log end=acacia_log_top side=acacia_log",
            "acacia_pressure_plate block/acacia_pressure_plate texture=acacia_planks",
            "acacia_slab block/acacia_slab bottom=acacia_planks side=acacia_planks top=acacia_planks",
            "acacia_stairs block/acacia_stairs bottom=acacia_planks side=acacia_planks top=acacia_planks",
            "acacia_wood block/acacia_wood end=acacia_log side=acacia_log",
            "andesite_slab block/andesite_slab bottom=andesite side=andesite top=andesite",
            "andesite_stairs block/andesite_stairs bottom=andesite side=andesite top=andesite",
            "andesite_wall block/andesite_wall_inventory wall=andesite",
            "birch_button block/birch_button_inventory texture=birch_planks",
            "birch_fence block/birch_fence_inventory texture=birch_planks",
            "birch_fence_gate block/birch_fence_gate texture=birch_planks",
            "birch_log block/birch_log end=birch_log_top side=birch_log",
            "birch_pressure_plate block/birch_pressure_plate texture=birch_planks",
            "birch_slab block/birch_slab bottom=birch_planks side=birch_planks top=birch_planks",
            "birch_stairs block/birch_stairs bottom=birch_planks side=birch_planks top=birch_planks",
            "birch_wood block/birch_wood end=birch_log side=birch_log",
            "bone_block block/bone_block end=bone_block_top side=bone_block_side",
            "bookshelf block/bookshelf end=oak_planks side=bookshelf",
            "brick_slab block/brick_slab bottom=bricks side=bricks top=bricks",
            "brick_stairs block/brick_stairs bottom=bricks side=bricks top=bricks",
            "brick_wall block/brick_wall_inventory wall=bricks",
            "brown_mushroom_block block/brown_mushroom_block_inventory all=brown_mushroom_block",
            "carved_pumpkin block/carved_pumpkin front=carved_pumpkin side=pumpkin_side top=pumpkin_top",
            "chain_command_block block/chain_command_block back=chain_command_block_back front=chain_command_block_front side=chain_command_block_side",
            "chiseled_quartz_block block/chiseled_quartz_block end=chiseled_quartz_block_top side=chiseled_quartz_block",
            "chiseled_red_sandstone block/chiseled_red_sandstone end=red_sandstone_top side=chiseled_red_sandstone",
            "chiseled_sandstone block/chiseled_sandstone end=sandstone_top side=chiseled_sandstone",
            "cobblestone_slab block/cobblestone_slab bottom=cobblestone side=cobblestone top=cobblestone",
            "cobblestone_stairs block/cobblestone_stairs bottom=cobblestone side=cobblestone top=cobblestone",
            "cobblestone_wall block/cobblestone_wall_inventory wall=cobblestone",
            "command_block block/command_block back=command_block_back front=command_block_front side=command_block_side",
            "crafting_table block/crafting_table down=oak_planks east=crafting_table_side north=crafting_table_front particle=crafting_table_front south=crafting_table_side up=crafting_table_top west=crafting_table_front",
            "cut_red_sandstone block/cut_red_sandstone end=red_sandstone_top side=cut_red_sandstone",
            "cut_red_sandstone_slab block/cut_red_sandstone_slab bottom=red_sandstone_top side=cut_red_sandstone top=red_sandstone_top",
            "cut_sandstone block/cut_sandstone end=sandstone_top side=cut_sandstone",
            "cut_sandstone_slab block/cut_sandstone_slab bottom=sandstone_top side=cut_sandstone top=sandstone_top",
            "dark_oak_button block/dark_oak_button_inventory texture=dark_oak_planks",
            "dark_oak_fence block/dark_oak_fence_inventory texture=dark_oak_planks",
            "dark_oak_fence_gate block/dark_oak_fence_gate texture=dark_oak_planks",
            "dark_oak_log block/dark_oak_log end=dark_oak_log_top side=dark_oak_log",
            "dark_oak_pressure_plate block/dark_oak_pressure_plate texture=dark_oak_planks",
            "dark_oak_slab block/dark_oak_slab bottom=dark_oak_planks side=dark_oak_planks top=dark_oak_planks",
            "dark_oak_stairs block/dark_oak_stairs bottom=dark_oak_planks side=dark_oak_planks top=dark_oak_planks",
            "dark_oak_wood block/dark_oak_wood end=dark_oak_log side=dark_oak_log",
            "dark_prismarine_slab block/dark_prismarine_slab bottom=dark_prismarine side=dark_prismarine top=dark_prismarine",
            "dark_prismarine_stairs block/dark_prismarine_stairs bottom=dark_prismarine side=dark_prismarine top=dark_prismarine",
            "diorite_slab block/diorite_slab bottom=diorite side=diorite top=diorite",
            "diorite_stairs block/diorite_stairs bottom=diorite side=diorite top=diorite",
            "diorite_wall block/diorite_wall_inventory wall=diorite",
            "dispenser block/dispenser front=dispenser_front side=furnace_side top=furnace_top",
            "dropper block/dropper front=dropper_front side=furnace_side top=furnace_top",
            "end_stone_brick_slab block/end_stone_brick_slab bottom=end_stone_bricks side=end_stone_bricks top=end_stone_bricks",
            "end_stone_brick_stairs block/end_stone_brick_stairs bottom=end_stone_bricks side=end_stone_bricks top=end_stone_bricks",
            "end_stone_brick_wall block/end_stone_brick_wall_inventory wall=end_stone_bricks",
            "furnace block/furnace front=furnace_front side=furnace_side top=furnace_top",
            "granite_slab block/granite_slab bottom=granite side=granite top=granite",
            "granite_stairs block/granite_stairs bottom=granite side=granite top=granite",
            "granite_wall block/granite_wall_inventory wall=granite",
            "hay_block block/hay_block end=hay_block_top side=hay_block_side",
            "heavy_weighted_pressure_plate block/heavy_weighted_pressure_plate texture=iron_block",
            "infested_chiseled_stone_bricks block/chiseled_stone_bricks all=chiseled_stone_bricks",
            "infested_cobblestone block/cobblestone all=cobblestone",
            "infested_cracked_stone_bricks block/cracked_stone_bricks all=cracked_stone_bricks",
            "infested_mossy_stone_bricks block/mossy_stone_bricks all=mossy_stone_bricks",
            "infested_stone block/stone all=stone",
            "infested_stone_bricks block/stone_bricks all=stone_bricks",
            "iron_trapdoor block/iron_trapdoor_bottom texture=iron_trapdoor",
            "jack_o_lantern block/jack_o_lantern front=jack_o_lantern side=pumpkin_side top=pumpkin_top",
            "jukebox block/jukebox side=jukebox_side top=jukebox_top",
            "jungle_button block/jungle_button_inventory texture=jungle_planks",
            "jungle_fence block/jungle_fence_inventory texture=jungle_planks",
            "jungle_fence_gate block/jungle_fence_gate texture=jungle_planks",
            "jungle_log block/jungle_log end=jungle_log_top side=jungle_log",
            "jungle_pressure_plate block/jungle_pressure_plate texture=jungle_planks",
            "jungle_slab block/jungle_slab bottom=jungle_planks side=jungle_planks top=jungle_planks",
            "jungle_stairs block/jungle_stairs bottom=jungle_planks side=jungle_planks top=jungle_planks",
            "jungle_wood block/jungle_wood end=jungle_log side=jungle_log",
            "light_weighted_pressure_plate block/light_weighted_pressure_plate texture=gold_block",
            "magma_block block/magma_block all=magma",
            "melon block/melon end=melon_top side=melon_side",
            "mossy_cobblestone_slab block/mossy_cobblestone_slab bottom=mossy_cobblestone side=mossy_cobblestone top=mossy_cobblestone",
            "mossy_cobblestone_stairs block/mossy_cobblestone_stairs bottom=mossy_cobblestone side=mossy_cobblestone top=mossy_cobblestone",
            "mossy_cobblestone_wall block/mossy_cobblestone_wall_inventory wall=mossy_cobblestone",
            "mossy_stone_brick_slab block/mossy_stone_brick_slab bottom=mossy_stone_bricks side=mossy_stone_bricks top=mossy_stone_bricks",
            "mossy_stone_brick_stairs block/mossy_stone_brick_stairs bottom=mossy_stone_bricks side=mossy_stone_bricks top=mossy_stone_bricks",
            "mossy_stone_brick_wall block/mossy_stone_brick_wall_inventory wall=mossy_stone_bricks",
            "mushroom_stem block/mushroom_stem_inventory all=mushroom_stem",
            "nether_brick_fence block/nether_brick_fence_inventory texture=nether_bricks",
            "nether_brick_slab block/nether_brick_slab bottom=nether_bricks side=nether_bricks top=nether_bricks",
            "nether_brick_stairs block/nether_brick_stairs bottom=nether_bricks side=nether_bricks top=nether_bricks",
            "nether_brick_wall block/nether_brick_wall_inventory wall=nether_bricks",
            "oak_button block/oak_button_inventory texture=oak_planks",
            "oak_fence block/oak_fence_inventory texture=oak_planks",
            "oak_fence_gate block/oak_fence_gate texture=oak_planks",
            "oak_log block/oak_log end=oak_log_top side=oak_log",
            "oak_pressure_plate block/oak_pressure_plate texture=oak_planks",
            "oak_slab block/oak_slab bottom=oak_planks side=oak_planks top=oak_planks",
            "oak_stairs block/oak_stairs bottom=oak_planks side=oak_planks top=oak_planks",
            "oak_trapdoor block/oak_trapdoor_bottom texture=oak_trapdoor",
            "oak_wood block/oak_wood end=oak_log side=oak_log",
            "petrified_oak_slab block/petrified_oak_slab bottom=oak_planks side=oak_planks top=oak_planks",
            "piston block/piston_inventory bottom=piston_bottom side=piston_side top=piston_top",
            "polished_andesite_slab block/polished_andesite_slab bottom=polished_andesite side=polished_andesite top=polished_andesite",
            "polished_andesite_stairs block/polished_andesite_stairs bottom=polished_andesite side=polished_andesite top=polished_andesite",
            "polished_diorite_slab block/polished_diorite_slab bottom=polished_diorite side=polished_diorite top=polished_diorite",
            "polished_diorite_stairs block/polished_diorite_stairs bottom=polished_diorite side=polished_diorite top=polished_diorite",
            "polished_granite_slab block/polished_granite_slab bottom=polished_granite side=polished_granite top=polished_granite",
            "polished_granite_stairs block/polished_granite_stairs bottom=polished_granite side=polished_granite top=polished_granite",
            "prismarine_brick_slab block/prismarine_brick_slab bottom=prismarine_bricks side=prismarine_bricks top=prismarine_bricks",
            "prismarine_brick_stairs block/prismarine_brick_stairs bottom=prismarine_bricks side=prismarine_bricks top=prismarine_bricks",
            "prismarine_slab block/prismarine_slab bottom=prismarine side=prismarine top=prismarine",
            "prismarine_stairs block/prismarine_stairs bottom=prismarine side=prismarine top=prismarine",
            "prismarine_wall block/prismarine_wall_inventory wall=prismarine",
            "purpur_pillar block/purpur_pillar end=purpur_pillar_top side=purpur_pillar_side",
            "purpur_slab block/purpur_slab bottom=purpur_block side=purpur_block top=purpur_block",
            "purpur_stairs block/purpur_stairs bottom=purpur_block side=purpur_block top=purpur_block",
            "quartz_block block/quartz_block end=quartz_block_top side=quartz_block_side",
            "quartz_pillar block/quartz_pillar end=quartz_pillar_top side=quartz_pillar_side",
            "quartz_slab block/quartz_slab bottom=quartz_block_top side=quartz_block_side top=quartz_block_top",
            "quartz_stairs block/quartz_stairs bottom=quartz_block_top side=quartz_block_side top=quartz_block_top",
            "red_mushroom_block block/red_mushroom_block_inventory all=red_mushroom_block",
            "red_nether_brick_slab block/red_nether_brick_slab bottom=red_nether_bricks side=red_nether_bricks top=red_nether_bricks",
            "red_nether_brick_stairs block/red_nether_brick_stairs bottom=red_nether_bricks side=red_nether_bricks top=red_nether_bricks",
            "red_nether_brick_wall block/red_nether_brick_wall_inventory wall=red_nether_bricks",
            "red_sandstone block/red_sandstone bottom=red_sandstone_bottom side=red_sandstone top=red_sandstone_top",
            "red_sandstone_slab block/red_sandstone_slab bottom=red_sandstone_bottom side=red_sandstone top=red_sandstone_top",
            "red_sandstone_stairs block/red_sandstone_stairs bottom=red_sandstone_bottom side=red_sandstone top=red_sandstone_top",
            "red_sandstone_wall block/red_sandstone_wall_inventory wall=red_sandstone",
            "repeating_command_block block/repeating_command_block back=repeating_command_block_back front=repeating_command_block_front side=repeating_command_block_side",
            "sandstone block/sandstone bottom=sandstone_bottom side=sandstone top=sandstone_top",
            "sandstone_slab block/sandstone_slab bottom=sandstone_bottom side=sandstone top=sandstone_top",
            "sandstone_stairs block/sandstone_stairs bottom=sandstone_bottom side=sandstone top=sandstone_top",
            "sandstone_wall block/sandstone_wall_inventory wall=sandstone",
            "smooth_quartz block/smooth_quartz all=quartz_block_bottom",
            "smooth_quartz_slab block/smooth_quartz_slab bottom=quartz_block_bottom side=quartz_block_bottom top=quartz_block_bottom",
            "smooth_quartz_stairs block/smooth_quartz_stairs bottom=quartz_block_bottom side=quartz_block_bottom top=quartz_block_bottom",
            "smooth_red_sandstone block/smooth_red_sandstone all=red_sandstone_top",
            "smooth_red_sandstone_slab block/smooth_red_sandstone_slab bottom=red_sandstone_top side=red_sandstone_top top=red_sandstone_top",
            "smooth_red_sandstone_stairs block/smooth_red_sandstone_stairs bottom=red_sandstone_top side=red_sandstone_top top=red_sandstone_top",
            "smooth_sandstone block/smooth_sandstone all=sandstone_top",
            "smooth_sandstone_slab block/smooth_sandstone_slab bottom=sandstone_top side=sandstone_top top=sandstone_top",
            "smooth_sandstone_stairs block/smooth_sandstone_stairs bottom=sandstone_top side=sandstone_top top=sandstone_top",
            "smooth_stone_slab block/smooth_stone_slab bottom=smooth_stone side=smooth_stone_slab_side top=smooth_stone",
            "snow_block block/snow_block all=snow",
            "spawner block/spawner all=spawner",
            "spruce_button block/spruce_button_inventory texture=spruce_planks",
            "spruce_fence block/spruce_fence_inventory texture=spruce_planks",
            "spruce_fence_gate block/spruce_fence_gate texture=spruce_planks",
            "spruce_log block/spruce_log end=spruce_log_top side=spruce_log",
            "spruce_pressure_plate block/spruce_pressure_plate texture=spruce_planks",
            "spruce_slab block/spruce_slab bottom=spruce_planks side=spruce_planks top=spruce_planks",
            "spruce_stairs block/spruce_stairs bottom=spruce_planks side=spruce_planks top=spruce_planks",
            "spruce_trapdoor block/spruce_trapdoor_bottom texture=spruce_trapdoor",
            "spruce_wood block/spruce_wood end=spruce_log side=spruce_log",
            "sticky_piston block/sticky_piston_inventory bottom=piston_bottom side=piston_side top=piston_top_sticky",
            "stone_brick_slab block/stone_brick_slab bottom=stone_bricks side=stone_bricks top=stone_bricks",
            "stone_brick_stairs block/stone_brick_stairs bottom=stone_bricks side=stone_bricks top=stone_bricks",
            "stone_brick_wall block/stone_brick_wall_inventory wall=stone_bricks",
            "stone_button block/stone_button_inventory texture=stone",
            "stone_pressure_plate block/stone_pressure_plate texture=stone",
            "stone_slab block/stone_slab bottom=stone side=stone top=stone",
            "stone_stairs block/stone_stairs bottom=stone side=stone top=stone",
            "stripped_dark_oak_log block/stripped_dark_oak_log end=stripped_dark_oak_log_top side=stripped_dark_oak_log",
            "stripped_dark_oak_wood block/stripped_dark_oak_wood end=stripped_dark_oak_log side=stripped_dark_oak_log",
            "tnt block/tnt bottom=tnt_bottom side=tnt_side top=tnt_top"
    );

    private static final List<String> CUSTOM_BLOCK_ITEMS = List.of(
            "deepslate block/deepslate end=deepslate_top side=deepslate",
            "infested_deepslate block/deepslate end=deepslate_top side=deepslate",
            "cobbled_deepslate block/cobbled_deepslate all=cobbled_deepslate",
            "cobbled_deepslate_slab block/cobbled_deepslate_slab bottom=cobbled_deepslate side=cobbled_deepslate top=cobbled_deepslate",
            "cobbled_deepslate_stairs block/cobbled_deepslate_stairs bottom=cobbled_deepslate side=cobbled_deepslate top=cobbled_deepslate",
            "cobbled_deepslate_wall block/cobbled_deepslate_wall_inventory wall=cobbled_deepslate",
            "polished_deepslate block/polished_deepslate all=polished_deepslate",
            "polished_deepslate_slab block/polished_deepslate_slab bottom=polished_deepslate side=polished_deepslate top=polished_deepslate",
            "polished_deepslate_stairs block/polished_deepslate_stairs bottom=polished_deepslate side=polished_deepslate top=polished_deepslate",
            "polished_deepslate_wall block/polished_deepslate_wall_inventory wall=polished_deepslate",
            "chiseled_deepslate block/chiseled_deepslate all=chiseled_deepslate",
            "deepslate_bricks block/deepslate_bricks all=deepslate_bricks",
            "cracked_deepslate_bricks block/cracked_deepslate_bricks all=cracked_deepslate_bricks",
            "deepslate_brick_slab block/deepslate_brick_slab bottom=deepslate_bricks side=deepslate_bricks top=deepslate_bricks",
            "deepslate_brick_stairs block/deepslate_brick_stairs bottom=deepslate_bricks side=deepslate_bricks top=deepslate_bricks",
            "deepslate_brick_wall block/deepslate_brick_wall_inventory wall=deepslate_bricks",
            "deepslate_tiles block/deepslate_tiles all=deepslate_tiles",
            "cracked_deepslate_tiles block/cracked_deepslate_tiles all=cracked_deepslate_tiles",
            "deepslate_tile_slab block/deepslate_tile_slab bottom=deepslate_tiles side=deepslate_tiles top=deepslate_tiles",
            "deepslate_tile_stairs block/deepslate_tile_stairs bottom=deepslate_tiles side=deepslate_tiles top=deepslate_tiles",
            "deepslate_tile_wall block/deepslate_tile_wall_inventory wall=deepslate_tiles",
            "deepslate_coal_ore block/deepslate_coal_ore all=deepslate_coal_ore",
            "deepslate_iron_ore block/deepslate_iron_ore all=deepslate_iron_ore",
            "deepslate_copper_ore block/deepslate_copper_ore all=deepslate_copper_ore",
            "deepslate_gold_ore block/deepslate_gold_ore all=deepslate_gold_ore",
            "deepslate_redstone_ore block/deepslate_redstone_ore all=deepslate_redstone_ore",
            "deepslate_emerald_ore block/deepslate_emerald_ore all=deepslate_emerald_ore",
            "deepslate_lapis_ore block/deepslate_lapis_ore all=deepslate_lapis_ore",
            "deepslate_diamond_ore block/deepslate_diamond_ore all=deepslate_diamond_ore",
            "reinforced_deepslate block/reinforced_deepslate bottom=reinforced_deepslate_bottom side=reinforced_deepslate_side top=reinforced_deepslate_top"
    );

    public static void generate(ItemModelGenerators itemModels)
    {
        FLAT_ITEMS.forEach(name -> flat(itemModels, vanillaItem(name), ModelTemplates.FLAT_ITEM));
        HANDHELD_ITEMS.forEach(name -> flat(itemModels, vanillaItem(name), ModelTemplates.FLAT_HANDHELD_ITEM));
        CUBE_BLOCK_ITEMS.forEach(name -> cubeBlock(itemModels, vanillaItem(name)));
        SHAPED_BLOCK_ITEMS.forEach(entry -> shapedBlock(itemModels, entry));
        CUSTOM_BLOCK_ITEMS.forEach(entry -> shapedBlock(itemModels, entry));
    }

    // Looks up minecraft:<name>. A typo stops datagen with a clear message instead of silently making air.
    private static Item vanillaItem(String name)
    {
        Identifier key = Identifier.withDefaultNamespace(name);
        if (!BuiltInRegistries.ITEM.containsKey(key))
        {
            throw new IllegalArgumentException("RetroItemModels: no vanilla item called " + key);
        }
        return BuiltInRegistries.ITEM.getValue(key);
    }

    // e.g. chronodynamics:item/retro/iron_ingot with layer0 = chronodynamics:retro/item/iron_ingot
    private static void flat(ItemModelGenerators itemModels, Item item, ModelTemplate template)
    {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        Identifier retroModel = template.create(
                id("item/retro/" + name),
                TextureMapping.layer0(new Material(id("retro/item/" + name))),
                itemModels.modelOutput);

        override(itemModels, item, retroModel, ModelLocationUtils.getModelLocation(item));
    }

    // e.g. chronodynamics:item/retro/oak_planks, parent minecraft:block/oak_planks, all = chronodynamics:retro/block/oak_planks
    private static void cubeBlock(ItemModelGenerators itemModels, Item item)
    {
        BlockItem blockItem = (BlockItem) item;
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        Identifier blockModel = ModelLocationUtils.getModelLocation(blockItem.getBlock());

        ModelTemplate sameShape = new ModelTemplate(Optional.of(blockModel), Optional.empty(), TextureSlot.ALL);
        Identifier retroModel = sameShape.create(
                id("item/retro/" + name),
                new TextureMapping().put(TextureSlot.ALL, new Material(id("retro/block/" + name))),
                itemModels.modelOutput);

        override(itemModels, item, retroModel, blockModel);
    }

    // e.g. "oak_log block/oak_log end=oak_log_top side=oak_log"
    //   -> chronodynamics:item/retro/oak_log, parent minecraft:block/oak_log, with end and side swapped to retro textures
    private static void shapedBlock(ItemModelGenerators itemModels, String entry)
    {
        String[] parts = entry.split(" ");
        Item item = vanillaItem(parts[0]);
        Identifier normalModel = Identifier.withDefaultNamespace(parts[1]);

        TextureMapping textures = new TextureMapping();
        TextureSlot[] slots = new TextureSlot[parts.length - 2];
        for (int i = 2; i < parts.length; i++)
        {
            String[] slotAndTexture = parts[i].split("=");
            TextureSlot slot = TextureSlot.create(slotAndTexture[0]);
            slots[i - 2] = slot;
            textures.put(slot, new Material(id("retro/block/" + slotAndTexture[1])));
        }

        Identifier retroModel = new ModelTemplate(Optional.of(normalModel), Optional.empty(), slots)
                .create(id("item/retro/" + parts[0]), textures, itemModels.modelOutput);

        override(itemModels, item, retroModel, normalModel);
    }

    // assets/minecraft/items/<item>.json: retro model while flickering, normal model otherwise
    private static void override(ItemModelGenerators itemModels, Item item, Identifier retroModel, Identifier normalModel)
    {
        itemModels.itemModelOutput.accept(item, ItemModelUtils.conditional(
                new RiftRetroProperty(),
                ItemModelUtils.plainModel(retroModel),
                ItemModelUtils.plainModel(normalModel)));
    }

    private static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, path);
    }
}
