package net.machinemuse.powersuits.item;

import com.google.common.collect.Multimap;
import net.machinemuse.api.ApiaristArmor;
import net.machinemuse.api.IArmorTraits;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.numina.geometry.Colour;
import net.machinemuse.powersuits.client.render.item.ArmorModelInstance;
import net.machinemuse.powersuits.client.render.item.IArmorModel;
import net.machinemuse.powersuits.common.Config;
import net.machinemuse.powersuits.powermodule.misc.InvisibilityModule;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseHeatUtils;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

/**
 * Describes the 4 different modular armor pieces - head, torso, legs, feet.
 *
 * @author MachineMuse
 *
 * Ported to Java by lehjr on 11/4/16.
 */
public abstract class ItemPowerArmor extends ItemElectricArmor implements ISpecialArmor, IArmorTraits {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[] {
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()};

    public ItemPowerArmor(int renderIndex, EntityEquipmentSlot entityEquipmentSlot) {
        super(ItemArmor.ArmorMaterial.IRON, renderIndex, entityEquipmentSlot);
        this.setMaxStackSize(1);
        this.setCreativeTab(Config.getCreativeTab());
    }

    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        int priority = 0;
        Label_0057: {
            if (source.isFireDamage()) {
                DamageSource overheatDamage = MuseHeatUtils.overheatDamage;
                if (source == null) {
                    if (overheatDamage == null) {
                        break Label_0057;
                    }
                }
                else if (source.equals(overheatDamage)) {
                    break Label_0057;
                }
                return new ISpecialArmor.ArmorProperties(priority, 0.25, (int)(25 * damage));
            }
        }
        if (ModuleManager.itemHasModule(armor, "Radiation Shielding") && (source.damageType.equals("electricity") || source.damageType.equals("radiation"))) {
            return new ISpecialArmor.ArmorProperties(priority, 0.25, (int)(25 * damage));
        }
        double armorDouble2;
        if (player instanceof EntityPlayer) {
            armorDouble2 = this.getArmorDouble((EntityPlayer)player, armor);
        }
        else {
            armorDouble2 = 2.0;
        }
        double armorDouble = armorDouble2;
        double absorbRatio = 0.04 * armorDouble;
        int absorbMax = (int)armorDouble * 75;
        if (source.isUnblockable()) {
            absorbMax = 0;
            absorbRatio = 0.0;
        }
        return new ISpecialArmor.ArmorProperties(priority, absorbRatio, absorbMax);
    }

    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, int layer) {
        return Config.BLANK_ARMOR_MODEL_PATH;
    }

    public int getColor(ItemStack stack) {
        Colour c = this.getColorFromItemStack(stack);
        return c.getInt();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        ModelBiped model = ArmorModelInstance.getInstance();
        ((IArmorModel)model).setVisibleSection(armorSlot);
        if (itemStack != null) {
            if (entityLiving instanceof EntityPlayer) {
                ItemStack armorChest = ((EntityPlayer)entityLiving).inventory.armorItemInSlot(2);

                if (armorChest != null) {
                    if (armorChest.getItem() instanceof ItemPowerArmor)
                        if (ModuleManager.itemHasActiveModule(armorChest, InvisibilityModule.MODULE_ACTIVE_CAMOUFLAGE)) ((IArmorModel)model).setVisibleSection(null);
                }
            }

            if (ModuleManager.itemHasActiveModule(itemStack, "Transparent Armor")) {
                ((IArmorModel)model).setVisibleSection(null);
            }

//            System.out.println("MuseItemUtils.getMuseRenderTag(itemStack, armorSlot).getKeySet().size(): " +
//                    MuseItemUtils.getMuseRenderTag(itemStack, armorSlot).getKeySet().size());
//            if (MuseItemUtils.getMuseRenderTag(itemStack, armorSlot).getKeySet().size() > 0) {
//                NBTTagCompound nbtThingy = MuseItemUtils.getMuseRenderTag(itemStack, armorSlot);
//
//
//                /*
//                    So far the only tag showing is "colours"
//
//                 */
//
//
//
//
//                for (String thingy : nbtThingy.getKeySet())
//                    System.out.println("NBTTagCompound tag key: " + thingy);
//
//
//
//            }




            ((IArmorModel)model).setRenderSpec(MuseItemUtils.getMuseRenderTag(itemStack, armorSlot));
        }
        return (ModelBiped)model;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == this.armorType)
        {
            multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getAttributeUnlocalizedName(), 0.25, 0));
        }

        return multimap;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean hasColor(ItemStack stack) {
        NBTTagCompound itemTag = MuseItemUtils.getMuseItemTag(stack);
        return ModuleManager.tagHasModule(itemTag, "Red Tint") || ModuleManager.tagHasModule(itemTag, "Green Tint") || ModuleManager.tagHasModule(itemTag, "Blue Tint");
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return (int)this.getArmorDouble(player, armor);
    }

    public double getHeatResistance(EntityPlayer player, ItemStack stack) {
        return MuseHeatUtils.getMaxHeat(stack);
    }

    @Override
    public double getArmorDouble(EntityPlayer player, ItemStack stack) {
        double totalArmor = 0.0;
        NBTTagCompound props = MuseItemUtils.getMuseItemTag(stack);
        double energy = ElectricItemUtils.getPlayerEnergy(player);
        double physArmor = ModuleManager.computeModularProperty(stack, MuseCommonStrings.ARMOR_VALUE_PHYSICAL);
        double enerArmor = ModuleManager.computeModularProperty(stack, MuseCommonStrings.ARMOR_VALUE_ENERGY);
        double enerConsum = ModuleManager.computeModularProperty(stack, MuseCommonStrings.ARMOR_ENERGY_CONSUMPTION);
        totalArmor += physArmor;
        if (energy > enerConsum) {
            totalArmor += enerArmor;
        }
        totalArmor = Math.min(Config.getMaximumArmorPerPiece(), totalArmor);
        return totalArmor;
    }

    /**
     * Inherited from ISpecialArmor, allows us to customize how the armor
     * handles being damaged.
     */
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        NBTTagCompound itemProperties = MuseItemUtils.getMuseItemTag(stack);
        if (entity instanceof EntityPlayer) {
            DamageSource overheatDamage = MuseHeatUtils.overheatDamage;
            if (source == null) {
                if (overheatDamage == null) {
                    return;
                }
            }
            else if (source.equals(overheatDamage)) {
                return;
            }
            if (source.isFireDamage()) {
                EntityPlayer player = (EntityPlayer)entity;
                if (!source.equals(DamageSource.onFire) || MuseHeatUtils.getPlayerHeat(player) < MuseHeatUtils.getMaxHeat(player)) {
                    MuseHeatUtils.heatPlayer(player, damage);
                }
            }
            else {
                double enerConsum = ModuleManager.computeModularProperty(stack, MuseCommonStrings.ARMOR_ENERGY_CONSUMPTION);
                double drain = enerConsum * damage;
                if (entity instanceof EntityPlayer) {
                    ElectricItemUtils.drainPlayerEnergy((EntityPlayer)entity, drain);
                }
                else {
                    this.drainEnergyFrom(stack, drain);
                }
            }
        }
    }

    @Optional.Method(modid = "Forestry")
    public boolean protectEntity(EntityLivingBase player, ItemStack armor, String cause, boolean doProtect) {
        return ApiaristArmor.getInstance().protectEntity(player, armor, cause, doProtect);
    }
}