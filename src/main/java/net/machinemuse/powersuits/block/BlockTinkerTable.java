package net.machinemuse.powersuits.block;

import net.machinemuse.powersuits.common.Config;
import net.machinemuse.powersuits.common.ModularPowersuits;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This is the tinkertable block. It doesn't do much except look pretty
 * (eventually) and provide a way for the player to access the TinkerTable GUI.
 *
 * @author MachineMuse
 *
 *
 * Ported to Java by lehjr on 10/21/16.
 */
public class BlockTinkerTable extends Block {
    public static final String name = "tinkerTable";

    public BlockTinkerTable() {
        super(Material.IRON);
        this.setHardness(1.5F);
        this.setResistance(1000.0F);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(Config.getCreativeTab());
        this.setSoundType(SoundType.METAL);
        this.setLightOpacity(0);
        this.setLightLevel(0.4f);
        this.setTickRandomly(false);
        setUnlocalizedName(ModularPowersuits.MODID + "." + name);
        setRegistryName(new ResourceLocation(ModularPowersuits.MODID, name));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking())
            return false;
        if (worldIn.isRemote)
            playerIn.openGui(ModularPowersuits.getInstance(), 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL; // TODO: fix this. The static rendered setup is horrible, such as hte translucent stuff doesn't work, the texures have to be resized, the glow doesnt work, the animation needs stupid undocumented code to make work
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTinkerTable();
    }
}