package com.horriblenerd.antiquewaystones;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

@Mod(AntiqueWaystones.MODID)
public class AntiqueWaystones {
    public static final String MODID = "antiquewaystones";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final MarkerAPI markerAPI = AtlasAPI.getMarkerAPI();
    private static final ResourceLocation IMAGE_PATH = new ResourceLocation(MODID, "textures/gui/markers/waystone.png");
    private static final ResourceLocation IMAGE_ID = new ResourceLocation(MODID, "waystone");
    private static final String TOWERS_MODID = "towers_of_the_wild";
    private final boolean isTowersOfTheWildLoaded;

    public AntiqueWaystones() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        isTowersOfTheWildLoaded = ModList.get().isLoaded(TOWERS_MODID);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void initClient(FMLClientSetupEvent event) {
        LOGGER.debug("Init waystone marker");
        MarkerType markerType = new MarkerType(IMAGE_PATH);
        MarkerType.register(IMAGE_ID, markerType);
//        markerAPI.registerMarker(IMAGE_ID, markerType);
    }

    @SubscribeEvent
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        PlayerEntity player = event.getPlayer();
        IWaystone waystone = event.getWaystone();

        BlockPos pos = waystone.getPos();
        MarkerType markerType = MarkerType.REGISTRY.get(IMAGE_ID);
        if (isTowersOfTheWildLoaded && Config.USE_TOWER_ICON.get()) {
            if (player.level instanceof ServerWorld) {
                if (isTower((ServerWorld) player.level, pos)) {
                    LOGGER.debug("Found a tower at: " + pos);
                    markerType = MarkerType.REGISTRY.get(ResourceLocation.tryParse("antiqueatlas:tower"));
                }
            }

        }
        LOGGER.debug("Adding marker to player atlases: " + waystone.getName());
        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(player);
        for (int id : playerAtlases) {
            markerAPI.putMarker(player.level, true, id, markerType.getIcon(), new StringTextComponent(waystone.getName()), pos.getX(), pos.getZ());
        }
    }

    private boolean isTower(ServerWorld world, BlockPos pos) {
        LOGGER.debug("Checking for tower...");
        Structure<?> structure;
        BlockPos structurePos;

        for (String s : Arrays.asList("tower", "ice_tower", "jungle_tower", "derelict_tower", "derelict_grass_tower", "ocean_tower", "ocean_warm_tower")) {
            structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(ResourceLocation.tryParse(TOWERS_MODID + ":" + s));
            if (structure == null) {
                continue;
            }
            structurePos = world.findNearestMapFeature(structure, pos, 2, false);
            if (structurePos != null) {
                // Manhattan distance without Y coord
                float f = (float) Math.abs(pos.getX() - structurePos.getX());
                float f1 = (float) Math.abs(pos.getZ() - structurePos.getZ());
                if (f + f1 <= 30) {
                    return true;
                }
            }
        }

        return false;
    }

}
