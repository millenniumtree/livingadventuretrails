package com.millenniumtree.livingadventuretrails.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStepHeight {
//  @Inject(at = { @At("TAIL") }, method = "getStepHeight()F", cancellable = true)
//  public void getStepHeight(CallbackInfoReturnable<Float> cir) {
//    if(
//      this.getClass().getSimpleName() == "HorseEntity"
//    ) {
//      cir.setReturnValue(1.125F);
//    }
//  }
}
