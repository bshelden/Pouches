package name.bshelden.pouches.client

import org.lwjgl.opengl.GL11

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{Icon, StatCollector}

import name.bshelden.pouches.{InventoryPouch, ContainerPouch}

/**
 * GUI for an open pouch
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class GuiContainerPouch(player: EntityPlayer,
                pouchContainer: ContainerPouch,
                pouchInventory: InventoryPouch,
                     pouchIcon: Icon)
    extends GuiContainer(pouchContainer) {
  private val BG_TEXTURE_PATH = "/gui/container.png"

  private val YSIZE_MAGIC = 114
  private val ROWS = 3
  private val FONT_COLOR = 0x404040 // Default grey

  allowUserInput = false
  ySize = YSIZE_MAGIC + ROWS * 18

  protected override def drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
    val uName = if (pouchInventory.isInvNameLocalized()) {
      pouchInventory.getInvName()
    } else {
      StatCollector.translateToLocal(pouchInventory.getInvName())
    }

    val lName = if (player.inventory.isInvNameLocalized()) {
      player.inventory.getInvName()
    } else {
      StatCollector.translateToLocal(player.inventory.getInvName())
    }

    fontRenderer.drawString(uName, 8, 6, FONT_COLOR)
    fontRenderer.drawString(lName, 8, 74, FONT_COLOR)

    val x = ContainerPouch.GUI_PLAYER_HOTBAR_OFFSET_X + (player.inventory.currentItem * ContainerPouch.GUI_SLOT_WIDTH)
    val y = ContainerPouch.GUI_PLAYER_HOTBAR_OFFSET_Y

/*
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F)
    GL11.glEnable(GL11.GL_BLEND)
//    GL11.glEnable(GL11.GL_ALPHA)  This causes a fit when rendering
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GL11.glDisable(GL11.GL_LIGHTING)
    this.mc.renderEngine.bindTexture("/gui/items.png")
    this.drawTexturedModelRectFromIcon(x, y, pouchIcon, 16, 16)
    GL11.glEnable(GL11.GL_LIGHTING)
*/
  }

  protected def drawGuiContainerBackgroundLayer(f: Float, i: Int, j: Int) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    mc.renderEngine.bindTexture(BG_TEXTURE_PATH)
    val x: Int = (width - xSize) / 2
    val y: Int = (height - ySize) / 2

    drawTexturedModalRect(x, y, 0, 0, xSize, ROWS * 18 + 17)
    drawTexturedModalRect(x, y + ROWS * 18 + 17, 0, 126, xSize, 96)
  }
}
