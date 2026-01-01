package com.yymod.mechanicalworkbench.registries;

import com.yymod.mechanicalworkbench.YYMechanicalWorkbench;
import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MWMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            YYMechanicalWorkbench.MOD_ID);

    public static final RegistryObject<MenuType<MechanicalWorkbenchMenu>> MECHANICAL_WORKBENCH = MENUS.register(
            "mechanical_workbench", () -> IForgeMenuType.create(MechanicalWorkbenchMenu::fromNetwork));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
