package com.yymod.mechanicalworkbench;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.yymod.mechanicalworkbench.config.MWConfigs;
import com.yymod.mechanicalworkbench.registries.MWBlockEntityTypes;
import com.yymod.mechanicalworkbench.registries.MWBlocks;
import com.yymod.mechanicalworkbench.registries.MWMenus;
import com.yymod.mechanicalworkbench.registries.MWPartialModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(YYMechanicalWorkbench.MOD_ID)
public class YYMechanicalWorkbench {

    public static final String MOD_ID = "yy_mechanical_workbench";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public YYMechanicalWorkbench() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(eventBus);

        MWConfigs.register(ModLoadingContext.get());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MWPartialModels::init);

        MWBlocks.register();
        MWBlockEntityTypes.register();
        MWMenus.register(eventBus);

        eventBus.addListener(YYMechanicalWorkbench::commonSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> YYMechanicalWorkbenchClient.loadClient(eventBus));
    }

    public static ResourceLocation genRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> BlockStressValues.IMPACTS.register(
                MWBlocks.MECHANICAL_WORKBENCH.get(),
                () -> MWConfigs.common().mechanicalWorkbench.stressImpact.get()));
    }
}
