package com.horriblenerd.antiquewaystones;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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
        LOGGER.info("Init waystone marker");
        MarkerType markerType = new MarkerType(IMAGE_PATH);
        MarkerType.register(IMAGE_ID, markerType);
//        markerAPI.registerMarker(IMAGE_ID, markerType);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == LogicalSide.CLIENT) return;
        BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());
        if (blockEntity instanceof WaystoneBlockEntityBase waystone) {
            LOGGER.info(waystone.getWaystone().getName());

            tryPutMarkerFromBlockEntity(event.getPlayer(), waystone);
        }

    }

    @SubscribeEvent
    public void onKnownWaystones(KnownWaystonesEvent event) {
        for (IWaystone waystone : event.getWaystones()) {
            LOGGER.info(waystone.getName());
        }
        Balm.getEvents();
    }

    @SubscribeEvent
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        LOGGER.info("Activated event");
        Player player = event.getPlayer();
        IWaystone waystone = event.getWaystone();

        tryPutMarker(player, waystone);

//        BlockPos pos = waystone.getPos();
////        MarkerType markerType = MarkerType.REGISTRY.getOrDefault(IMAGE_ID);
//        ResourceLocation ID = IMAGE_ID;
//        if (isTowersOfTheWildLoaded && Config.USE_TOWER_ICON.get()) {
//            if (player.level instanceof ServerLevel) {
//                if (isTower((ServerLevel) player.level, pos)) {
//                    LOGGER.info("Found a tower at: " + pos);
////                    markerType = MarkerType.REGISTRY.getOrDefault(ResourceLocation.tryCreate("antiqueatlas:tower"));
//                    ID = ResourceLocation.tryParse("antiqueatlas:tower");
//                }
//            }
//
//        }
//        LOGGER.info("Adding marker to player atlases: " + waystone.getName() + " with markerType: " + ID);
//        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(player);
//        for (int id : playerAtlases) {
//            markerAPI.putMarker(player.level, true, id, ID, new TextComponent(waystone.getName()), pos.getX(), pos.getZ());
//        }
    }

    private void tryPutMarkerFromBlockEntity(Player player, WaystoneBlockEntityBase waystone) {
        BlockPos pos = waystone.getBlockPos();
//        MarkerType markerType = MarkerType.REGISTRY.getOrDefault(IMAGE_ID);
        ResourceLocation ID = IMAGE_ID;
        if (isTowersOfTheWildLoaded && Config.USE_TOWER_ICON.get()) {
            if (player.level instanceof ServerLevel) {
                if (isTower((ServerLevel) player.level, pos)) {
                    LOGGER.info("Found a tower at: " + pos);
//                    markerType = MarkerType.REGISTRY.getOrDefault(ResourceLocation.tryCreate("antiqueatlas:tower"));
                    ID = ResourceLocation.tryParse("antiqueatlas:tower");
                }
            }

        }
        LOGGER.info("Adding marker to player atlases: " + waystone.getWaystone().getName() + " with markerType: " + ID);
        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(player);
        for (int id : playerAtlases) {
            markerAPI.putMarker(player.level, true, id, ID, new TextComponent(waystone.getWaystone().getName()), pos.getX(), pos.getZ());
        }
    }

    private void tryPutMarker(Player player, IWaystone waystone) {
        BlockPos pos = waystone.getPos();
//        MarkerType markerType = MarkerType.REGISTRY.getOrDefault(IMAGE_ID);
        ResourceLocation ID = IMAGE_ID;
        if (isTowersOfTheWildLoaded && Config.USE_TOWER_ICON.get()) {
            if (player.level instanceof ServerLevel) {
                if (isTower((ServerLevel) player.level, pos)) {
                    LOGGER.info("Found a tower at: " + pos);
//                    markerType = MarkerType.REGISTRY.getOrDefault(ResourceLocation.tryCreate("antiqueatlas:tower"));
                    ID = ResourceLocation.tryParse("antiqueatlas:tower");
                }
            }

        }
        LOGGER.info("Adding marker to player atlases: " + waystone.getName() + " with markerType: " + ID);
        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(player);
        for (int id : playerAtlases) {
            markerAPI.putMarker(player.level, true, id, ID, new TextComponent(waystone.getName()), pos.getX(), pos.getZ());
        }
    }

    private boolean isTower(ServerLevel level, BlockPos pos) {
        LOGGER.info("Checking for tower...");
        StructureFeature<?> structure;
        StructureStart<?> structurePos;

        for (String s : Arrays.asList("tower", "ice_tower", "jungle_tower", "derelict_tower", "derelict_grass_tower", "ocean_tower", "ocean_warm_tower")) {
            structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(ResourceLocation.tryParse(TOWERS_MODID + ":" + s));
            if (structure == null) {
                continue;
            }
            structurePos = level.structureFeatureManager().getStructureAt(pos, false, structure);
            if (structurePos.isValid()) {
                // Manhattan distance without Y coord
                float f = (float) Math.abs(pos.getX() - structurePos.getLocatePos().getX());
                float f1 = (float) Math.abs(pos.getZ() - structurePos.getLocatePos().getZ());
                if (f + f1 <= 30) {
                    return true;
                }
            }
        }

        return false;
    }

}
