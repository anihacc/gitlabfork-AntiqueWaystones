package com.horriblenerd.antiquewaystones;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Created by HorribleNerd on 28/04/2021
 */
public class Config {

    public static final String CATEGORY_GENERAL = "general";

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue USE_TOWER_ICON;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        USE_TOWER_ICON = COMMON_BUILDER.comment("Use the tower icon when Towers of the Wild is loaded").define("use_tower_icon", true);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

}
