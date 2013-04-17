package name.bshelden.pouches.items

import java.util

import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.util.{StringTranslate, Icon}
import net.minecraft.world.World

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.{SideOnly, Side}

import name.bshelden.pouches.{Pouches, MCColor}
import net.minecraft.nbt.NBTTagCompound

/**
 * Item definition for the pouch
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class Pouch(id: Int) extends Item(id) {
  private var icons: Map[Int, Icon] = Map.empty

  hasSubtypes = true
  setMaxDamage(0)
  setMaxStackSize(1)
  setCreativeTab(CreativeTabs.tabMisc)
  setUnlocalizedName("pouch")

  def getPouchId(itemStack: ItemStack): Option[String] = {
    for {
      ct <- Option(itemStack.getTagCompound)
      if (itemStack.getItem == this)
      if (ct.hasKey(Pouches.NBT_POUCHID))
    } yield (ct.getString(Pouches.NBT_POUCHID))
  }

  override def onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
    if (itemStack.getItemDamage == Pouch.DAMAGE_END) {
      // End chest pouch
      if (Pouches.config.allowEndPouch) {
        player.displayGUIChest(player.getInventoryEnderChest)
      } else {
        if (FMLCommonHandler.instance().getEffectiveSide == Side.SERVER) {
          // Only do this once
          val msg = StringTranslate.getInstance().translateKeyFormat(
            Pouches.L_DISABLED_END_POUCH_MESSAGE,
            getItemDisplayName(itemStack) // Pick up the localization for the end pouch
          )
          player.addChatMessage(msg)
        }
      }
      itemStack
    } else {
      // Assign an ID if there isn't yet one
      val pct = Option(itemStack.getTagCompound).getOrElse(new NBTTagCompound("tag"))
      if (!pct.hasKey(Pouches.NBT_POUCHID)) {
        pct.setString(Pouches.NBT_POUCHID, java.util.UUID.randomUUID().toString)
      }

      player.openGui(Pouches, 0, world, 0, 0, 0)
      itemStack
    }
  }

  override def getMetadata(damageValue: Int): Int = damageValue

  override def getSubItems(itemId: Int, creativeTabs: CreativeTabs, rawList: util.List[_]) {
    val list = rawList.asInstanceOf[util.List[ItemStack]]

    // Colors
    MCColor.all foreach { case c =>
      val is = new ItemStack(itemId, 1, c.id)
      list.add(is)
    }

    // End pouch only if enabled
    if (Pouches.config.allowEndPouch) {
      val endIs = new ItemStack(itemId, 1, Pouch.DAMAGE_END)
      list.add(endIs)
    }
  }

  override def getUnlocalizedName(is: ItemStack): String = {
    is.getItemDamage match {
      case Pouch.DAMAGE_END => "item.endpouch"
      case _ => "item.pouch"
    }
  }

  @SideOnly(Side.CLIENT)
  override def getIconFromDamage(damageValue: Int): Icon = {
    icons.get(damageValue).getOrElse(icons(MCColor.White.id))
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(iconRegister: IconRegister) {
    MCColor.all foreach { c =>
      val icon = iconRegister.registerIcon("Pouches:pouch_%d".format(c.id))
      icons = icons.updated(c.id, icon)
    }

    val plumIcon = iconRegister.registerIcon("Pouches:pouch_plum")
    icons = icons.updated(Pouch.DAMAGE_PLUM, plumIcon)

    val endIcon = iconRegister.registerIcon("Pouches:pouch_ender")
    icons = icons.updated(Pouch.DAMAGE_END, endIcon)
  }
}
object Pouch {
  val DAMAGE_PLUM = 16
  val DAMAGE_END = 17
}