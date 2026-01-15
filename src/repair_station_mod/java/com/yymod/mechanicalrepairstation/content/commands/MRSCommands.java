package com.yymod.mechanicalrepairstation.content.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.yymod.mechanicalrepairstation.YYMechanicalRepairStation;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationBlockEntity;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationMenu;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = YYMechanicalRepairStation.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MRSCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("repairstation")
                        .then(Commands.literal("whyNotRepair")
                                .executes(context -> executeWhyNotRepair(context.getSource(), null))
                                .then(Commands.argument("optionalMaterial", StringArgumentType.string())
                                        .executes(context -> executeWhyNotRepair(context.getSource(),
                                                StringArgumentType.getString(context, "optionalMaterial")))))
        );
    }

    private static int executeWhyNotRepair(CommandSourceStack source, String materialSpec) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player-only command."));
            return 0;
        }
        if (!(player.containerMenu instanceof MechanicalRepairStationMenu menu)) {
            source.sendFailure(Component.literal("Open the Repair Station UI first."));
            return 0;
        }
        MechanicalRepairStationBlockEntity station = menu.getStation();
        if (station == null) {
            source.sendFailure(Component.literal("Repair Station not found."));
            return 0;
        }
        station.armWhyNotRepairDebug(player, materialSpec);
        source.sendSuccess(() -> Component.literal("Now try to repair problematic item..."), false);
        return 1;
    }
}
