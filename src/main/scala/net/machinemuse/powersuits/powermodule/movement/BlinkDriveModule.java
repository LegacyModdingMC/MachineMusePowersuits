package net.machinemuse.powersuits.powermodule.movement;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IRightClickModule;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseItemUtils;
import net.machinemuse.utils.MusePlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundSetupEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BlinkDriveModule extends PowerModuleBase implements IRightClickModule {
    public static final String MODULE_BLINK_DRIVE = "Blink Drive";
    public static final String BLINK_DRIVE_ENERGY_CONSUMPTION = "Blink Drive Energy Consuption";
    public static final String BLINK_DRIVE_RANGE = "Blink Drive Range";

    public BlinkDriveModule(List<IModularItem> validItems) {
        super(validItems);
        addBaseProperty(BLINK_DRIVE_ENERGY_CONSUMPTION, 1000, "J");
        addBaseProperty(BLINK_DRIVE_RANGE, 5, "m");
        addTradeoffProperty("Range", BLINK_DRIVE_ENERGY_CONSUMPTION, 3000);
        addTradeoffProperty("Range", BLINK_DRIVE_RANGE, 59);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.ionThruster, 1));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));
    }

//    @Override
//    public String getTextureFile() {
//        return "alien";
//    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_MOVEMENT;
    }

    @Override
    public String getDataName() {
        return MODULE_BLINK_DRIVE;
    }

    @Override
    public String getUnlocalizedName() { return "blinkDrive";
    }

    @Override
    public String getDescription() {
        return "Get from point A to point C via point B, where point B is a fold in space & time.";
    }

    @Override
    public void onRightClick(EntityPlayer player, World world, ItemStack itemStack) {
        SoundEvent enderman_portal =  SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport"));
        double range = ModuleManager.computeModularProperty(itemStack, BLINK_DRIVE_RANGE);
        double energyConsumption = ModuleManager.computeModularProperty(itemStack, BLINK_DRIVE_ENERGY_CONSUMPTION);
        if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
            NuminaPlayerUtils.resetFloatKickTicks(player);
            ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
            world.playSound(player, player.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
            // MuseLogger.logDebug("Range: " + range);
            RayTraceResult hitMOP = MusePlayerUtils.doCustomRayTrace(player.worldObj, player, true, range);
            // MuseLogger.logDebug("Hit:" + hitMOP);
            MusePlayerUtils.teleportEntity(player, hitMOP);
            world.playSound(player, player.getPosition(), enderman_portal, SoundCategory.PLAYERS, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));
        }

    }

    @Override
    public void onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

    }

    @Override
    public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int par4) {
    }
}
