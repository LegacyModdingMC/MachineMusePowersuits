package net.machinemuse.general.gui;

import net.machinemuse.general.gui.frame.DetailedSummaryFrame;
import net.machinemuse.general.gui.frame.ItemSelectionFrame;
import net.machinemuse.general.gui.frame.ModuleSelectionFrame;
import net.machinemuse.general.gui.frame.ModuleTweakFrame;
import net.machinemuse.numina.geometry.Colour;
import net.machinemuse.numina.geometry.MusePoint2D;
import net.machinemuse.utils.render.MuseRenderer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

/**
 * The gui class for the TinkerTable block.
 *
 * @author MachineMuse
 */
public class GuiFieldTinker extends MuseGui {
    protected EntityClientPlayerMP player;
    protected ItemSelectionFrame itemSelectFrame;

    /**
     * Constructor. Takes a player as an argument.
     *
     * @param player
     */
    public GuiFieldTinker(EntityPlayer player) {
        this.player = (EntityClientPlayerMP) player;
        this.xSize = 256;
        this.ySize = 200;
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        itemSelectFrame = new ItemSelectionFrame(new MusePoint2D(absX(-0.95F), absY(-0.95F)), new MusePoint2D(absX(-0.78F), absY(0.95F)),
                Colour.LIGHTBLUE.withAlpha(0.8F), Colour.DARKBLUE.withAlpha(0.8F), player);
        frames.add(itemSelectFrame);

        DetailedSummaryFrame statsFrame = new DetailedSummaryFrame(player, new MusePoint2D(absX(0f), absY(-0.9f)), new MusePoint2D(absX(0.9f), absY(-0.05f)),
                Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8));
        frames.add(statsFrame);

        ModuleSelectionFrame moduleSelectFrame = new ModuleSelectionFrame(new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F),
                absY(0.95f)), Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame);
        frames.add(moduleSelectFrame);

        ModuleTweakFrame tweakFrame = new ModuleTweakFrame(player, new MusePoint2D(absX(0f), absY(0f)), new MusePoint2D(absX(0.9f), absY(0.9f)),
                Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame, moduleSelectFrame);
        frames.add(tweakFrame);
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        super.drawScreen(x, y, z);
        if (itemSelectFrame.hasNoItems()) {
            double centerx = absX(0);
            double centery = absY(0);
            MuseRenderer.drawCenteredString(StatCollector.translateToLocal("gui.noModulesFound.line1"), centerx, centery - 5);
            MuseRenderer.drawCenteredString(StatCollector.translateToLocal("gui.noModulesFound.line2"), centerx, centery + 5);
        }
    }
}
