package name.bshelden.pouches

import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 * Proxy common to client and server
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class CommonProxy extends IGuiHandler {
  def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    val curItem = player.getCurrentEquippedItem

    if (Option(curItem).flatMap(ci => Option(ci.getItem)).map(_ == Pouches.pouch).getOrElse(false)) {
      val pouchInventory = new InventoryPouch(curItem, 9*3)
      new ContainerPouch(world, player, pouchInventory)
    } else {
      null
    }
  }

  def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = null
}
object CommonProxy {
  val POUCH_PNG_FORMAT: String = "/mods/Pouches/textures/items/pouch_%d.png"
  val POUCH_OPEN_PNG_FORMAT: String = "/mods/Pouches/textures/items/pouchopen_%d.png"
}
