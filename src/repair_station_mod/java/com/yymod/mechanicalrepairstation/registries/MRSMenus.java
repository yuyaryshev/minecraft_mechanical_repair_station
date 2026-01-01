package com.yymod.mechanicalrepairstation.registries;

import com.yymod.mechanicalrepairstation.YYMechanicalRepairStation;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MRSMenus {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            YYMechanicalRepairStation.MOD_ID);

    public static final RegistryObject<MenuType<MechanicalRepairStationMenu>> MECHANICAL_REPAIR_STATION = MENUS.register(
            "mechanical_repair_station", () -> IForgeMenuType.create(MechanicalRepairStationMenu::fromNetwork));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

