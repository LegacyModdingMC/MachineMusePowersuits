package net.machinemuse.powersuits.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.machinemuse.general.gui.MuseIcon;
import net.machinemuse.powersuits.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockLuxCapacitor extends Block {
    public static int assignedBlockID;
    protected int renderType;

    public BlockLuxCapacitor setRenderType(int id) {
        this.renderType = id;
        return this;
    }

    public BlockLuxCapacitor() {
        super(Material.circuits);
        setCreativeTab(Config.getCreativeTab());
        setHardness(0.05F);
        setResistance(10.0F);
        setStepSound(Block.soundTypeMetal);
        setLightOpacity(0);
        setLightLevel(1.0f);
        setTickRandomly(false);
        GameRegistry.registerTileEntity(TileEntityLuxCapacitor.class, "luxCapacitor");
        setBlockName("luxCapacitor");
    }

    private static float bbMin(double offset) {
        return (offset > 0 ? 13 : offset < 0 ? 0 : 1) / 16.0f;
    }

    private static float bbMax(double offset) {
        return (offset > 0 ? 16 : offset < 0 ? 3 : 15) / 16.0f;
    }


    public static AxisAlignedBB createAABBForSide(ForgeDirection dir, double x, double y, double z) {
        double x1 = bbMin(dir.offsetX);
        double y1 = bbMin(dir.offsetY);
        double z1 = bbMin(dir.offsetZ);
        double x2 = bbMax(dir.offsetX);
        double y2 = bbMax(dir.offsetY);
        double z2 = bbMax(dir.offsetZ);
        return AxisAlignedBB.getBoundingBox(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(MuseIcon.ICON_PREFIX + "bluelight");
    }

    /**
     * returns some value from 0 to 30 or so for different models. Since we're
     * using a custom renderer, we pass in a completely different ID: the
     * assigned block ID. It won't conflict with other mods, since Forge looks
     * it up in a table anyway, but it's still best to have different internal
     * IDs.
     */
    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityLuxCapacitor();

    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityLuxCapacitor) {
            ForgeDirection side = ((TileEntityLuxCapacitor) te).side;
            return createAABBForSide(side, x, y, z);
        }
        return null;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityLuxCapacitor) {
            ForgeDirection side = ((TileEntityLuxCapacitor) te).side;
            return createAABBForSide(side, x, y, z);
        }
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z) {
        TileEntity te = par1IBlockAccess.getTileEntity(x, y, z);
        if (te instanceof TileEntityLuxCapacitor) {
            ForgeDirection side = ((TileEntityLuxCapacitor) te).side;
            float x1 = bbMin(side.offsetX);
            float y1 = bbMin(side.offsetY);
            float z1 = bbMin(side.offsetZ);
            float x2 = bbMax(side.offsetX);
            float y2 = bbMax(side.offsetY);
            float z2 = bbMax(side.offsetZ);
            this.setBlockBounds(x1, y1, z1, x2, y2, z2);
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random) {
        return 0;
    }
}