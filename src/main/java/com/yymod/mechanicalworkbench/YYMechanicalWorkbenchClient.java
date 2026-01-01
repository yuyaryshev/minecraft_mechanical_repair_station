package com.yymod.mechanicalrepairstation;

import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationScreen;
import com.yymod.mechanicalrepairstation.registries.MWMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class YYmechanicalrepairstationClient {

    public static void loadClient(IEventBus modEventBus) {
        modEventBus.addListener(YYmechanicalrepairstationClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MWMenus.mechanical_repair_station.get(), mechanicalrepairstationScreen::new));
    }
}

