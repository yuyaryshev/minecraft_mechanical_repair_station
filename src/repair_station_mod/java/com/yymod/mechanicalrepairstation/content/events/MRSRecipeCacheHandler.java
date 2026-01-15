package com.yymod.mechanicalrepairstation.content.events;

import com.yymod.mechanicalrepairstation.YYMechanicalRepairStation;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationBlockEntity;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = YYMechanicalRepairStation.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MRSRecipeCacheHandler {

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        MechanicalRepairStationBlockEntity.clearInferredMaterialCache();
    }
}
