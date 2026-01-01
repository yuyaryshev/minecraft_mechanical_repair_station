package com.yymod.mechanicalrepairstation.registries;

import com.yymod.mechanicalrepairstation.YYmechanicalrepairstation;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MWMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            YYmechanicalrepairstation.MOD_ID);

    public static final RegistryObject<MenuType<mechanicalrepairstationMenu>> mechanical_repair_station = MENUS.register(
            "mechanical_repair_station", () -> IForgeMenuType.create(mechanicalrepairstationMenu::fromNetwork));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

