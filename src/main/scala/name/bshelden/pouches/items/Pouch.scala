package name.bshelden.pouches.items

import java.util

import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.util.Icon
import net.minecraft.world.World

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
    // Assign an ID if there isn't yet one
    val pct = Option(itemStack.getTagCompound).getOrElse(new NBTTagCompound("tag"))
    if (!pct.hasKey(Pouches.NBT_POUCHID)) {
      pct.setString(Pouches.NBT_POUCHID, java.util.UUID.randomUUID().toString)
    }

//    player.openGui(Pouches, 0, world, player.posX.toInt, player.posY.toInt, player.posZ.toInt)
    player.openGui(Pouches, 0, world, 0, 0, 0)
    itemStack
  }

  override def getMetadata(damageValue: Int): Int = damageValue

  override def getSubItems(itemId: Int, creativeTabs: CreativeTabs, rawList: util.List[_]) {
    val list = rawList.asInstanceOf[util.List[ItemStack]]
    MCColor.all foreach { case c =>
      val is = new ItemStack(itemId, 1, c.id)
      list.add(is)
    }
//    val plumIs = new ItemStack(itemId, 1, 16)
//    list.add(plumIs)
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
    icons = icons.updated(16, plumIcon)
  }
}
