package name.bshelden.pouches

import cpw.mods.fml.common.{FMLLog, FMLCommonHandler, Mod}
import java.util.logging.Level
import net.minecraftforge.oredict.ShapedOreRecipe

//import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.Mod.{PostInit, Init, PreInit}
import cpw.mods.fml.common.network.{NetworkRegistry, NetworkMod}
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.registry.{GameRegistry, LanguageRegistry}
import cpw.mods.fml.relauncher.Side

import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.common.{Configuration, MinecraftForge}

import name.bshelden.pouches.items.Pouch

/**
 * Base class for the Pouches mod
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
@Mod(modid="Pouches", modLanguage="scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
object Pouches {
  private var _config: PouchesConfig = null
  private var _pouch: Pouch = null

  val NBT_POUCHID = "Pouches_PouchID"
  val NBT_INVENTORY = "Pouches_Inventory"

  val CFG_ID_POUCH_DEFAULT = 4800
  val CFG_ALLOW_POUCH_IN_POUCH_DEFAULT = false
  val CFG_ALLOW_END_POUCH_DEFAULT = true

  val L_DISABLED_END_POUCH_MESSAGE = "L_DISABLED_END_POUCH_MESSAGE"

  def pouch: Pouch = _pouch
  def config: PouchesConfig = _config

//  @SidedProxy(clientSide = "name.bshelden.pouches.client.ClientProxy", serverSide = "name.bshelden.pouches.CommonProxy")
//  var proxy: CommonProxy = null
  private val proxy: CommonProxy = findProxy()

  @PreInit
  def preInit(ev: FMLPreInitializationEvent) {
    _config = loadConfig(ev)
    _pouch = new Pouch(_config.pouchItemId)
  }

  @Init
  def init(ev: FMLInitializationEvent) {
    FMLLog.log(Level.INFO,"Pouches: Initialization")

    FMLLog.log(Level.FINE, "Pouches: Registering GUIs")
    NetworkRegistry.instance().registerGuiHandler(this, proxy)

    FMLLog.log(Level.FINE, "Pouches: Registering localizations")
    initTranslations()

    FMLLog.log(Level.FINE, "Pouches: Registering recipies")
    registerRecipies()
  }

  @PostInit
  def postInit(ev: FMLPostInitializationEvent) {
  }

  private def findProxy(): CommonProxy = {
    FMLCommonHandler.instance().getEffectiveSide match {
      case Side.CLIENT =>
        Class.forName("name.bshelden.pouches.client.ClientProxy").newInstance().asInstanceOf[CommonProxy]
      case _ =>
        new CommonProxy
    }
  }

  private def registerRecipies() {
    MCColor.all foreach { case color =>
      GameRegistry.addRecipe(new ShapedOreRecipe(
        new ItemStack(pouch, 1, color.id),
        " N ", "LCL", " L ",
        new Character('L'), new ItemStack(Item.leather),
        new Character('N'), new ItemStack(Item.goldNugget),
        new Character('C'), color.oreDict))
    }

    // Plum
    GameRegistry.addRecipe(
      new ItemStack(pouch, 1, Pouch.DAMAGE_PLUM),
      " N ", "LCL", " L ",
      new Character('L'), new ItemStack(Item.leather),
      new Character('N'), new ItemStack(Item.goldNugget),
      new Character('C'), new ItemStack(Item.netherStalkSeeds))

    // End chest pouch
    if (config.allowEndPouch) {
      GameRegistry.addRecipe(
        new ItemStack(pouch, 1, Pouch.DAMAGE_END),
        " N ", "LCL", " L ",
        new Character('L'), new ItemStack(Item.leather),
        new Character('N'), new ItemStack(Item.goldNugget),
        new Character('C'), new ItemStack(Item.eyeOfEnder))
    }
  }

  private def initTranslations() {
    val lr = LanguageRegistry.instance()

    LanguageRegistry.addName(pouch, "Pouch")
    LanguageRegistry.addName(new ItemStack(pouch, 1, Pouch.DAMAGE_END), "End Pouch")

    lr.addStringLocalization(L_DISABLED_END_POUCH_MESSAGE, "The %s has been disabled on this server.")
  }

  private def loadConfig(ev: FMLPreInitializationEvent): PouchesConfig = {
    FMLLog.log(Level.FINE, "Pouches: Loading configuration")

    val ITEM_ID_OFFSET = 256  // Because Minecraft, that's why

    val cfg = new Configuration(ev.getSuggestedConfigurationFile)

    try {
      cfg.load()

      val pouchId = cfg.getItem(
        "pouch",
        CFG_ID_POUCH_DEFAULT
      ).getInt(CFG_ID_POUCH_DEFAULT) - ITEM_ID_OFFSET

      val allowPouchInPouch = cfg.get(
        "options",
        "allowPouchInPouch",
        CFG_ALLOW_POUCH_IN_POUCH_DEFAULT,
        "Allow placing pouches into pouches.  Be warned that this enables infinite inventory capacity by chaining pouches!"
      ).getBoolean(CFG_ALLOW_POUCH_IN_POUCH_DEFAULT)

      val allowEndPouch = cfg.get(
        "options",
        "allowEndPouch",
        CFG_ALLOW_END_POUCH_DEFAULT,
        "Allow the crafting and use of End Pouches.  Note that setting this to false will disable existing end pouches, but will not delete them."
      ).getBoolean(CFG_ALLOW_END_POUCH_DEFAULT)

      PouchesConfig(pouchId, allowPouchInPouch, allowEndPouch)
    } catch {
      case e: Exception =>
        FMLLog.log(Level.SEVERE, e, "Exception caught when trying to load the configuration for Pouches!  Loading defaults.")
        PouchesConfig(CFG_ID_POUCH_DEFAULT, CFG_ALLOW_POUCH_IN_POUCH_DEFAULT, CFG_ALLOW_END_POUCH_DEFAULT)
    } finally {
      cfg.save()
    }
  }
}

case class PouchesConfig(pouchItemId: Int, allowPouchInPouch: Boolean, allowEndPouch: Boolean)
