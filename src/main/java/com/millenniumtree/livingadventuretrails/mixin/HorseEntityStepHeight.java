package com.millenniumtree.livingadventuretrails.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(HorseEntity.class)
public class HorseEntityStepHeight {
  @Inject(method = "<init>", at = @At("TAIL"))
  protected void onInit(EntityType<? extends HorseEntity> entityType, World world, CallbackInfo ci) {
    if(((HorseEntity) (Object) this).getStepHeight() == 1.0F) {
      // This allows horses to step up from a path block onto a carpet, or 2 snow layers
      ((HorseEntity) (Object) this).setStepHeight(1.125F);
    }
  }
}
