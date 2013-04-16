package name.bshelden.pouches.client

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

import name.bshelden.pouches._

/**
 * Client side proxy class responsible for GUI handling
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ClientProxy extends CommonProxy {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    val curItem = player.getCurrentEquippedItem

    if (Option(curItem).flatMap(ci => Option(ci.getItem)).map(_ == Pouches.pouch).getOrElse(false)) {
      val pouchInventory = new InventoryPouch(curItem, 9*3)
      val pouchContainer = new ContainerPouch(world, player, pouchInventory)
      new GuiContainerPouch(player, pouchContainer, pouchInventory, curItem.getIconIndex)
    } else {
      null
    }
  }
}
