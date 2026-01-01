package com.yymod.mechanicalrepairstation.mixins;

import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    private void yyMechanicalRepairStation$applyUpgrade(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return;
        if (!tag.contains(MechanicalRepairStationBlockEntity.TAG_UPGRADE_LEVEL, Tag.TAG_INT))
            return;
        int level = tag.getInt(MechanicalRepairStationBlockEntity.TAG_UPGRADE_LEVEL);
        if (level <= 0)
            return;
        int base = tag.contains(MechanicalRepairStationBlockEntity.TAG_BASE_MAX_DAMAGE, Tag.TAG_INT)
                ? tag.getInt(MechanicalRepairStationBlockEntity.TAG_BASE_MAX_DAMAGE)
                : cir.getReturnValue();
        int bonus = Math.round(base * 0.1f * level);
        if (bonus <= 0)
            return;
        cir.setReturnValue(cir.getReturnValue() + bonus);
    }
}
