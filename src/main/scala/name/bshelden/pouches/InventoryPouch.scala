package name.bshelden.pouches

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}

/**
 * Inventory implementation for a pouch
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class InventoryPouch(private var pouch: ItemStack, size: Int) extends IInventory {
  private var contents: Map[Int, ItemStack] = loadContents()

  def getContents: Map[Int, ItemStack] = contents

  def getPouch(): ItemStack = {
    storeContents()
    pouch
  }

  def getSizeInventory: Int = size

  def getStackInSlot(slot: Int): ItemStack = contents.get(slot).getOrElse(null)

  def decrStackSize(slot: Int, amt: Int): ItemStack = {
    (contents.get(slot) flatMap { curStack =>
      if (curStack.stackSize > amt) {
        val newStack = curStack.splitStack(amt)
        if (curStack.stackSize <= 0) {
          contents -= slot
        }
        Some(newStack)
      } else {
        contents -= slot
        Some(curStack)
      }
    }).getOrElse(null)
  }

  def getStackInSlotOnClosing(slot: Int): ItemStack = {
    val is = contents.get(slot).getOrElse(null)
    contents -= slot
    is
  }

  def setInventorySlotContents(slot: Int, itemstack: ItemStack) {
    if (itemstack == null) {
      contents -= slot
    } else {
      contents += slot -> itemstack
    }
  }

  def getInvName: String = pouch.getDisplayName

  def isInvNameLocalized: Boolean = true

  def getInventoryStackLimit: Int = 64

  def onInventoryChanged() {
    storeContents()
  }

  def isUseableByPlayer(entityplayer: EntityPlayer): Boolean = true

  def openChest() {}

  def closeChest() {}

  def isStackValidForSlot(slotIndex: Int, itemStack: ItemStack): Boolean = true

  private def loadContents(): Map[Int, ItemStack] = {
    val storedItems = Option(pouch.getTagCompound).getOrElse(new NBTTagCompound()).getTagList(Pouches.NBT_INVENTORY)

    if (storedItems.tagCount() > 0) {
      var loaded: Map[Int, ItemStack] = Map.empty

      for {
        i <- 0 to (storedItems.tagCount() - 1)
      } {
        val tag = storedItems.tagAt(i)
        if (tag.isInstanceOf[NBTTagCompound]) {
          val cTag = tag.asInstanceOf[NBTTagCompound]
          Option(ItemStack.loadItemStackFromNBT(cTag)) foreach { itemStack =>
            loaded += (cTag.getInteger("Slot") -> itemStack)
          }
        }
      }

      loaded
    } else {
      Map.empty
    }
  }

  def storeContents() {
    val tagList = new NBTTagList()

    contents.toSeq.sortBy(_._1) map { case (slotIndex, itemStack) =>
      val itemTag = new NBTTagCompound()
      itemTag.setInteger("Slot", slotIndex)
      itemStack.writeToNBT(itemTag)
    } foreach tagList.appendTag

    // Ensure the pouch has NBT
    val ptc = Option(pouch.getTagCompound).getOrElse(new NBTTagCompound("tag"))

    // Store the item data
    ptc.setTag(Pouches.NBT_INVENTORY, tagList)
    pouch.setTagCompound(ptc)
  }
}
