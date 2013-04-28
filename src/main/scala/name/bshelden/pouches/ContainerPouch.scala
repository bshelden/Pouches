package name.bshelden.pouches

import net.minecraft.inventory.{Slot, IInventory, Container}
import net.minecraft.entity.player.{InventoryPlayer, EntityPlayer}
import net.minecraft.item.ItemStack
import net.minecraft.block.Block
import net.minecraft.world.World

/**
 * Container implementation for the pouch
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ContainerPouch(world: World, player: EntityPlayer, pouchInventory: InventoryPouch) extends Container {
  import ContainerPouch._

  layout()

  /** Returns the slot index containing the open pouch */
  def lockedSlotIndex(): Int = {
    player.inventory.currentItem
  }

  override def canInteractWith(player: EntityPlayer): Boolean = {
    this.player == player
  }

  override def transferStackInSlot(player: EntityPlayer, slotIndex: Int): ItemStack = {
    val iss = inventorySlots.asInstanceOf[java.util.List[Slot]]

    def mergeAct(stack: ItemStack): Boolean = {
      if (slotIndex < POUCH_NUMSLOTS) {
        // From pouch to player
        mergeItemStack(stack, POUCH_NUMSLOTS, this.inventorySlots.size(), true)
      } else {
        // From player to pouch
        if (Pouches.config.allowPouchInPouch || (stack.getItem != Pouches.pouch)) {
          mergeItemStack(stack, 0, POUCH_NUMSLOTS, false)
        } else {
          // No pouches in pouches!
          false
        }
      }
    }

    def transferAct(slot: Slot, stack: ItemStack): Option[ItemStack] = {
      val copy = stack.copy()

      if (mergeAct(stack)) {
        if (stack.stackSize == 0) {
          slot.putStack(null : ItemStack)
        } else {
          slot.onSlotChanged()
        }

        Some(copy)
      } else {
        None
      }
    }

    (for {
      slot  <- Option(iss.get(slotIndex))
      stack <- Option(slot.getStack)
      ret   <- transferAct(slot, stack)
    } yield ret).getOrElse(null : ItemStack)
  }


  override def detectAndSendChanges() {
    super.detectAndSendChanges()

    syncPouch()
    player.setCurrentItemOrArmor(0, pouchInventory.getPouch())
  }

  override def onCraftGuiClosed(player: EntityPlayer) {
    super.onCraftGuiClosed(player)

    syncPouch()
  }

  private def syncPouch() {
    val ci = player.inventory.getCurrentItem

    // Confirm we're still on the pouch
    val mSamePouch = for {
      ciId  <- Pouches.pouch.getPouchId(ci)
      invId <- Pouches.pouch.getPouchId(pouchInventory.getPouch())
    } yield (ciId == invId)

    if (mSamePouch.getOrElse(false)) {
      // We are, store it back in the current slot
      player.setCurrentItemOrArmor(0, pouchInventory.getPouch())
    }
  }

  private def layout() {
    // Lay out the pouch inventory
    for {
      row <- 0 to (POUCH_ROWS - 1)
      col <- 0 to (POUCH_COLUMNS - 1)
    } {
      val slot = new PouchSlot(
        pouchInventory,
        col + row * POUCH_COLUMNS,
        GUI_POUCH_INV_OFFSET_X + col * GUI_SLOT_WIDTH,
        GUI_POUCH_INV_OFFSET_Y + row * GUI_SLOT_HEIGHT)

      addSlotToContainer(slot)
    }

    // Lay out the player inventory
    for {
      row <- 0 to 2
      col <- 0 to 8
    } {
      val slot = new Slot(
        player.inventory,
        (col + row * 9) + 9,
        GUI_PLAYER_INV_OFFSET_X + col * GUI_SLOT_WIDTH,
        GUI_PLAYER_INV_OFFSET_Y + row * GUI_SLOT_HEIGHT)

      addSlotToContainer(slot)
    }

    // Lay out the hotbar
    for {
      col <- 0 to 8
//      if (col != player.inventory.currentItem)
    } {
      // Be sure to lock the slot that would contain the pouch!
      val slot: Slot = new PouchHotbarSlot(
        player.inventory,
        col,
        GUI_PLAYER_HOTBAR_OFFSET_X + col * GUI_SLOT_WIDTH,
        GUI_PLAYER_HOTBAR_OFFSET_Y,
        col == player.inventory.currentItem
      )

      addSlotToContainer(slot)
    }
  }
}
object ContainerPouch {
  val POUCH_COLUMNS = 9
  val POUCH_ROWS = 3

  val POUCH_NUMSLOTS = POUCH_COLUMNS * POUCH_ROWS

  val GUI_POUCH_INV_OFFSET_X = 8
  val GUI_POUCH_INV_OFFSET_Y = 18

  val GUI_PLAYER_INV_OFFSET_X = 8
  val GUI_PLAYER_INV_OFFSET_Y = 85

  val GUI_PLAYER_HOTBAR_OFFSET_X = 8
  val GUI_PLAYER_HOTBAR_OFFSET_Y = 143

  val GUI_SLOT_WIDTH = 18
  val GUI_SLOT_HEIGHT = 18

  class PouchSlot(inv: IInventory, slotIndex: Int, xPos: Int, yPos: Int) extends Slot(inv, slotIndex, xPos, yPos) {
    override def isItemValid(itemStack: ItemStack): Boolean = {
      Pouches.config.allowPouchInPouch || (itemStack.getItem() != Pouches.pouch)
    }
  }
  class PouchHotbarSlot(inv: IInventory, slotIndex: Int, xPos: Int, yPos: Int, val locked: Boolean) extends Slot(inv, slotIndex, xPos, yPos) {
    override def canTakeStack(p: EntityPlayer): Boolean = !locked
  }
}
