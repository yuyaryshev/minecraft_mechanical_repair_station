package com.yymod.mechanicalrepairstation;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.yymod.mechanicalrepairstation.config.MRSConfigs;
import com.yymod.mechanicalrepairstation.registries.MRSBlockEntityTypes;
import com.yymod.mechanicalrepairstation.registries.MRSBlocks;
import com.yymod.mechanicalrepairstation.registries.MRSMenus;
import com.yymod.mechanicalrepairstation.registries.MRSPartialModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(YYMechanicalRepairStation.MOD_ID)
public class YYMechanicalRepairStation {

    public static final String MOD_ID = "yy_mechanical_repair_station";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public YYMechanicalRepairStation() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(eventBus);

        MRSConfigs.register(ModLoadingContext.get());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MRSPartialModels::init);

        MRSBlocks.register();
        MRSBlockEntityTypes.register();
        MRSMenus.register(eventBus);

        eventBus.addListener(YYMechanicalRepairStation::commonSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> YYMechanicalRepairStationClient.loadClient(eventBus));
    }

    public static ResourceLocation genRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> BlockStressValues.IMPACTS.register(
                MRSBlocks.MECHANICAL_REPAIR_STATION.get(),
                () -> MRSConfigs.common().mechanicalRepairStation.stressImpact.get()));
    }

}

