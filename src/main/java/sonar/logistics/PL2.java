package sonar.logistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mcmultipart.api.slot.IPartSlot;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.IInfoManager;
import sonar.logistics.api.PL2API;
import sonar.logistics.api.tiles.displays.EnumDisplayFaceSlot;
import sonar.logistics.commands.CommandResetInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.integration.MineTweakerIntegration;
import sonar.logistics.networking.ClientInfoHandler;
import sonar.logistics.networking.LogisticsNetworkHandler;
import sonar.logistics.networking.ServerInfoHandler;
import sonar.logistics.networking.cabling.CableConnectionHandler;
import sonar.logistics.networking.cabling.RedstoneConnectionHandler;
import sonar.logistics.networking.cabling.WirelessDataManager;
import sonar.logistics.networking.cabling.WirelessRedstoneManager;
import sonar.logistics.networking.displays.ChunkViewerHandler;
import sonar.logistics.networking.displays.ConnectedDisplayHandler;
import sonar.logistics.worldgen.SapphireOreGen;

@Mod(modid = PL2Constants.MODID, name = PL2Constants.NAME, dependencies = "required-after:sonarcore@[" + PL2Constants.SONAR_CORE + ",);" + "required-after:mcmultipart@[" + PL2Constants.MCMULTIPART + ",);", version = PL2Constants.VERSION)
public class PL2 {

	@SidedProxy(clientSide = "sonar.logistics.PL2Client", serverSide = "sonar.logistics.PL2Common")
	public static PL2Common proxy;

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(PL2Constants.MODID);

	@Instance(PL2Constants.MODID)
	public static PL2 instance;

	public LogisticsNetworkHandler networkManager = new LogisticsNetworkHandler();
	public WirelessDataManager wirelessDataManager = new WirelessDataManager();
	public WirelessRedstoneManager wirelessRedstoneManager = new WirelessRedstoneManager();
	public CableConnectionHandler cableManager = new CableConnectionHandler();
	public RedstoneConnectionHandler redstoneManager = new RedstoneConnectionHandler();
	public ConnectedDisplayHandler displayManager = new ConnectedDisplayHandler();
	public ServerInfoHandler serverManager = new ServerInfoHandler();
	public ClientInfoHandler clientManager = new ClientInfoHandler();

	public static CreativeTabs creativeTab = new CreativeTabs(PL2Constants.NAME) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(PL2Items.etched_plate);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Releasing the Kraken");
		if (!(Loader.isModLoaded("SonarCore") || Loader.isModLoaded("sonarcore"))) {
			logger.fatal("Sonar Core is not loaded");
		} else {
			logger.info("Successfully loaded with Sonar Core");
		}

		PL2API.init();
		logger.info("Initilised API");

		network = NetworkRegistry.INSTANCE.newSimpleChannel(PL2Constants.MODID);
		logger.info("Registered Network");

		PL2Common.registerPackets();
		logger.info("Registered Packets");

		PL2Config.initConfiguration(event);
		logger.info("Loaded Configuration");

		PL2Blocks.registerBlocks();
		logger.info("Loaded Blocks");

		PL2Items.registerItems();
		logger.info("Loaded Items");

		PL2Items.registerMultiparts();
		logger.info("Loaded Multiparts");

		if (PL2Config.sapphireOre) {
			GameRegistry.registerWorldGenerator(new SapphireOreGen(), 1);
			logger.info("Registered Sapphire World Generator");
		} else
			logger.info("Sapphire Ore Generation is disabled in the config");

		PL2ASMLoader.init(event);
		LogicInfoRegistry.INSTANCE.init();
		proxy.preInit(event);
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		logger.info("Breaking into the pentagon");
		PL2Crafting.addRecipes();
		logger.info("Registered Crafting Recipes");

		for (EnumDisplayFaceSlot slot : EnumDisplayFaceSlot.values())
			GameRegistry.findRegistry(IPartSlot.class).register(slot);

		OreDictionary.registerOre("oreSapphire", PL2Blocks.sapphire_ore);
		OreDictionary.registerOre("gemSapphire", PL2Items.sapphire);
		OreDictionary.registerOre("dustSapphire", PL2Items.sapphire_dust);
		logger.info("Registered OreDict");

		MinecraftForge.EVENT_BUS.register(new PL2Events());
		MinecraftForge.EVENT_BUS.register(ChunkViewerHandler.instance());
		logger.info("Registered Events");
		proxy.load(event);
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent evt) {
		logger.info("Please Wait: We are saving Harambe with a time machine");
		if (Loader.isModLoaded("MineTweaker3") || Loader.isModLoaded("MineTweaker3".toLowerCase())) {
			MineTweakerIntegration.init();
			logger.info("'Mine Tweaker' integration was loaded");
		}
		proxy.postLoad(evt);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandResetInfoRegistry());
	}

	@EventHandler
	public void serverLoad(FMLServerStartedEvent event) {

	}

	@EventHandler
	public void serverClose(FMLServerStoppedEvent event) {
		getWirelessDataManager().removeAll();
		getWirelessRedstoneManager().removeAll();
		getNetworkManager().removeAll();
		getCableManager().removeAll();
		getDisplayManager().removeAll();
		getClientManager().removeAll();
		getServerManager().removeAll();
	}

	public static LogisticsNetworkHandler getNetworkManager() {
		return PL2.instance.networkManager;
	}

	public static CableConnectionHandler getCableManager() {
		return PL2.instance.cableManager;
	}

	public static RedstoneConnectionHandler getRedstoneManager() {
		return PL2.instance.redstoneManager;
	}

	public static ConnectedDisplayHandler getDisplayManager() {
		return PL2.instance.displayManager;
	}

	public static WirelessDataManager getWirelessDataManager() {
		return PL2.instance.wirelessDataManager;
	}

	public static WirelessRedstoneManager getWirelessRedstoneManager() {
		return PL2.instance.wirelessRedstoneManager;
	}
	
	public static ServerInfoHandler getServerManager() {
		return PL2.instance.serverManager;
	}

	public static ClientInfoHandler getClientManager() {
		return PL2.instance.clientManager;
	}
	
	public static IInfoManager getInfoManager(boolean isRemote) {
		if(!isRemote){
			return getServerManager();
		}
		return getClientManager();
	}
}