package com.horriblenerd.antiquewaystones;

import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.api.MarkerAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(AntiqueWaystones.MODID)
public class AntiqueWaystones {
    public static final String MODID = "antiquewaystones";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final MarkerAPI markerAPI = AtlasAPI.getMarkerAPI();
    private static final ResourceLocation IMAGE_PATH = new ResourceLocation(MODID, "textures/gui/markers/waystone.png");
    private static final ResourceLocation IMAGE_ID = new ResourceLocation(MODID, "waystone");

    public AntiqueWaystones() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void initClient(FMLClientSetupEvent event) {
        LOGGER.debug("Init waystone marker");
        MarkerType markerType = new MarkerType(IMAGE_PATH);
        markerAPI.registerMarker(IMAGE_ID, markerType);
    }

    @SubscribeEvent
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        PlayerEntity player = event.getPlayer();
        IWaystone waystone = event.getWaystone();

        MarkerType markerType = MarkerType.REGISTRY.getOrDefault(IMAGE_ID);
        BlockPos pos = waystone.getPos();

        LOGGER.debug("Adding marker to player atlases: " + waystone.getName());
        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(player);
        for (int id : playerAtlases) {
            markerAPI.putMarker(player.world, true, id, markerType, new StringTextComponent(waystone.getName()), pos.getX(), pos.getZ());
        }
    }

}
