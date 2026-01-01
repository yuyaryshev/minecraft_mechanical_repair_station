package com.yymod.mechanicalworkbench;

import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchScreen;
import com.yymod.mechanicalworkbench.registries.MWMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class YYMechanicalWorkbenchClient {

    public static void loadClient(IEventBus modEventBus) {
        modEventBus.addListener(YYMechanicalWorkbenchClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MWMenus.MECHANICAL_WORKBENCH.get(), MechanicalWorkbenchScreen::new));
    }
}
