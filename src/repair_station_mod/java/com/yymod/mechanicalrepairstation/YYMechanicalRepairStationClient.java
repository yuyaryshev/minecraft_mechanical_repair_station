package com.yymod.mechanicalrepairstation;

import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationScreen;
import com.yymod.mechanicalrepairstation.registries.MRSMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class YYMechanicalRepairStationClient {

    public static void loadClient(IEventBus modEventBus) {
        modEventBus.addListener(YYMechanicalRepairStationClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MRSMenus.MECHANICAL_REPAIR_STATION.get(), MechanicalRepairStationScreen::new));
    }
}

