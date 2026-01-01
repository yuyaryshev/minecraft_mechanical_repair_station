package com.yymod.mechanicalrepairstation;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.yymod.mechanicalrepairstation.config.MWConfigs;
import com.yymod.mechanicalrepairstation.registries.MWBlockEntityTypes;
import com.yymod.mechanicalrepairstation.registries.MWBlocks;
import com.yymod.mechanicalrepairstation.registries.MWMenus;
import com.yymod.mechanicalrepairstation.registries.MWPartialModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(YYmechanicalrepairstation.MOD_ID)
public class YYmechanicalrepairstation {

    public static final String MOD_ID = "yy_mechanical_repair_station";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public YYmechanicalrepairstation() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(eventBus);

        MWConfigs.register(ModLoadingContext.get());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MWPartialModels::init);

        MWBlocks.register();
        MWBlockEntityTypes.register();
        MWMenus.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> YYmechanicalrepairstationClient.loadClient(eventBus));
    }

    public static ResourceLocation genRL(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

