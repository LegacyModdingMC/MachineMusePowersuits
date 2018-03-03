package net.machinemuse.powersuits.item.module.environmental;


import net.machinemuse.item.powersuits.module.PowerModuleBase;
import net.machinemuse.numina.api.module.EnumModuleTarget;
import net.machinemuse.numina.api.module.IPlayerTickModule;
import net.machinemuse.numina.api.module.IToggleableModule;
import net.machinemuse.numina.api.module.ModuleManager;
import net.machinemuse.powersuits.api.constants.MPSModuleConstants;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.utils.MuseItemUtils;
import net.machinemuse.utils.ElectricItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Created by User: Andrew2448
 * 8:26 PM 4/25/13
 */
public class MobRepulsorModule extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    public static final String MOB_REPULSOR_ENERGY_CONSUMPTION = "Repulsor Energy Consumption";

    public MobRepulsorModule(String resourceDommain, String UnlocalizedName) {
        super(EnumModuleTarget.TORSOONLY, resourceDommain, UnlocalizedName);
        addBasePropertyDouble(MOB_REPULSOR_ENERGY_CONSUMPTION, 250);
        addBasePropertyDouble(MPSModuleConstants.WEIGHT, 2000);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.magnet, 1));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
    }

    @Override
    public String getCategory() {
        return MPSModuleConstants.CATEGORY_ENVIRONMENTAL;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (ElectricItemUtils.getPlayerEnergy(player) > ModuleManager.getInstance().computeModularPropertyInteger(item, MOB_REPULSOR_ENERGY_CONSUMPTION)) {
            if (player.world.getTotalWorldTime() % 20 == 0) {
                ElectricItemUtils.drainPlayerEnergy(player, ModuleManager.getInstance().computeModularPropertyInteger(item, MOB_REPULSOR_ENERGY_CONSUMPTION));
            }
            repulse(player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    public void repulse(World world, int i, int j, int k) {
        float distance = 5.0F;
        Entity entity;
        Iterator iterator;
        List list = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityFireball.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityPotion.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
    }

    private void push(Entity entity, int i, int j, int k) {
        if (!(entity instanceof EntityPlayer) && !(entity instanceof EntityDragon)) {
            double d = i - entity.posX;
            double d1 = j - entity.posY;
            double d2 = k - entity.posZ;
            double d4 = d * d + d1 * d1 + d2 * d2;
            d4 *= d4;
            if (d4 <= Math.pow(6.0D, 4.0D)) {
                double d5 = -(d * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                double d6 = -(d1 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                double d7 = -(d2 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                if (d5 > 0.0D) {
                    d5 = 0.22D;
                } else if (d5 < 0.0D) {
                    d5 = -0.22D;
                }
                if (d6 > 0.2D) {
                    d6 = 0.12D;
                } else if (d6 < -0.1D) {
                    d6 = 0.12D;
                }
                if (d7 > 0.0D) {
                    d7 = 0.22D;
                } else if (d7 < 0.0D) {
                    d7 = -0.22D;
                }
                entity.motionX += d5;
                entity.motionY += d6;
                entity.motionZ += d7;
            }
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.mobRepulsor;
    }
}