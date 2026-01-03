package com.yymod.mechanicalrepairstation.content.events;

import com.yymod.mechanicalrepairstation.YYMechanicalRepairStation;
import com.yymod.mechanicalrepairstation.config.MRSConfigs;
import com.yymod.mechanicalrepairstation.config.MRSMechanicalRepairStationConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = YYMechanicalRepairStation.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MRSDeathDurabilityHandler {

    private static final String TAG_LAST_APPLIED = "MRSDeathDurabilityTick";

    @SubscribeEvent
    public static void onPlayerFatalDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.level().isClientSide)
            return;
        if (event.isCanceled())
            return;
        if (player.getHealth() - event.getAmount() > 0.0f)
            return;

        MRSMechanicalRepairStationConfig config = MRSConfigs.common().mechanicalRepairStation;
        if (!config.deathBreaksItems.get())
            return;

        int percent = Math.max(0, Math.min(100, config.deathDurabilityLossPercent.get()));
        if (percent <= 0)
            return;

        long gameTime = player.level().getGameTime();
        if (player.getPersistentData().getLong(TAG_LAST_APPLIED) == gameTime)
            return;
        player.getPersistentData().putLong(TAG_LAST_APPLIED, gameTime);

        applyLoss(player.getMainHandItem(), percent);
        applyLossToArmor(player, percent);
        applyLossToOffhand(player, percent);
    }

    private static void applyLossToArmor(Player player, int percent) {
        for (int slot = 0; slot < player.getInventory().armor.size(); slot++) {
            ItemStack stack = player.getInventory().armor.get(slot);
            applyLoss(stack, percent);
        }
    }

    private static void applyLossToOffhand(Player player, int percent) {
        for (int slot = 0; slot < player.getInventory().offhand.size(); slot++) {
            ItemStack stack = player.getInventory().offhand.get(slot);
            applyLoss(stack, percent);
        }
    }

    private static void applyLoss(ItemStack stack, int percent) {
        if (stack.isEmpty() || !stack.isDamageableItem())
            return;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0)
            return;
        int loss = (int) Math.ceil(maxDamage * (percent / 100.0));
        if (loss <= 0)
            return;
        int maxAllowedDamage = Math.max(0, maxDamage - 1);
        int newDamage = Math.min(maxAllowedDamage, stack.getDamageValue() + loss);
        if (newDamage != stack.getDamageValue())
            stack.setDamageValue(newDamage);
    }
}
