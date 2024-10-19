package com.wanisp.militarydrones.Entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PowerfulTNTEntity extends TNTEntity {
    public PowerfulTNTEntity(EntityType<? extends TNTEntity> p_i50216_1_, World p_i50216_2_) {
        super(p_i50216_1_, p_i50216_2_);
    }

    public PowerfulTNTEntity(World p_i1730_1_, double p_i1730_2_, double p_i1730_4_, double p_i1730_6_, @Nullable LivingEntity p_i1730_8_) {
        super(p_i1730_1_, p_i1730_2_, p_i1730_4_, p_i1730_6_, p_i1730_8_);
    }

    @Override
    protected void explode() {
        float explosionPower = 8.0F;
        this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), explosionPower, true, Explosion.Mode.BREAK);
    }
}
