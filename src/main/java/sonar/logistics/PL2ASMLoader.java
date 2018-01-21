package sonar.logistics;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import sonar.core.helpers.ASMLoader;
import sonar.core.utils.Pair;
import sonar.logistics.api.asm.EntityInfoProvider;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.asm.LogicComparator;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.asm.NodeFilter;
import sonar.logistics.api.asm.TileInfoProvider;
import sonar.logistics.api.filters.INodeFilter;
import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.info.handlers.IEntityInfoProvider;
import sonar.logistics.api.info.handlers.ITileInfoProvider;
import sonar.logistics.api.info.register.IInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.logic.comparators.ILogicComparator;

public class PL2ASMLoader {

	public static LinkedHashMap<Integer, String> infoNames = Maps.newLinkedHashMap();
	public static LinkedHashMap<String, Integer> infoIds = Maps.newLinkedHashMap();
	public static LinkedHashMap<String, Class<? extends IInfo>> infoClasses = Maps.newLinkedHashMap();

	public static LinkedHashMap<Integer, String> comparatorNames = Maps.newLinkedHashMap();
	public static LinkedHashMap<String, Integer> comparatorIds = Maps.newLinkedHashMap();
	public static LinkedHashMap<String, ILogicComparator> comparatorClasses = Maps.newLinkedHashMap();
	
	//public static LinkedHashMap<Integer, String> monitoredValueNames = Maps.newLinkedHashMap();
	////public static LinkedHashMap<String, Integer> monitoredValueIds = Maps.newLinkedHashMap();
	//public static LinkedHashMap<String, Class<? extends IMonitoredValue>> monitoredValueClasses = Maps.newLinkedHashMap();

	// public static LinkedHashMap<Integer, String> infoNames = Maps.newLinkedHashMap();
	// public static LinkedHashMap<String, Integer> infoIds = Maps.newLinkedHashMap();
	public static LinkedHashMap<String, Class<? extends INodeFilter>> filterClasses = Maps.newLinkedHashMap();
	//public static LinkedHashMap<String, ILogicComparator> comparatorClasses = Maps.newLinkedHashMap();

	private PL2ASMLoader() {}

	public static void init(FMLPreInitializationEvent event) {
		ASMDataTable asmDataTable = event.getAsmData();
		PL2ASMLoader.loadInfoTypes(asmDataTable);
		PL2ASMLoader.loadComparatorTypes(asmDataTable);
		PL2ASMLoader.loadNodeFilters(asmDataTable);
		LogicInfoRegistry.INSTANCE.infoRegistries.addAll(PL2ASMLoader.getInfoRegistries(asmDataTable));
		LogicInfoRegistry.INSTANCE.tileProviders.addAll(PL2ASMLoader.getTileProviders(asmDataTable));
		LogicInfoRegistry.INSTANCE.entityProviders.addAll(PL2ASMLoader.getEntityProviders(asmDataTable));
	}

	public static List<IInfoRegistry> getInfoRegistries(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, InfoRegistry.class, IInfoRegistry.class, true, false);
	}

	public static List<ITileInfoProvider> getTileProviders(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, TileInfoProvider.class, ITileInfoProvider.class, true, false);
	}

	public static List<IEntityInfoProvider> getEntityProviders(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, EntityInfoProvider.class, IEntityInfoProvider.class, true, false);
	}

	public static void loadComparatorTypes(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends ILogicComparator>>> infoTypes = ASMLoader.getClasses(asmDataTable, LogicComparator.class, ILogicComparator.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends ILogicComparator>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("id");
			int hashCode = name.hashCode();
			comparatorNames.put(hashCode, name);
			comparatorIds.put(name, hashCode);
			try {
				comparatorClasses.put(name, info.b.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		PL2.logger.info("Loaded: " + comparatorIds.size() + " Comparator Types");
	}


	public static void loadInfoTypes(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends IInfo>>> infoTypes = ASMLoader.getClasses(asmDataTable, LogicInfoType.class, IInfo.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends IInfo>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("id");
			int hashCode = name.hashCode();
			infoNames.put(hashCode, name);
			infoIds.put(name, hashCode);
			infoClasses.put(name, info.b);
		}
		PL2.logger.info("Loaded: " + infoIds.size() + " Info Types");
	}

	public static void loadNodeFilters(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends INodeFilter>>> infoTypes = ASMLoader.getClasses(asmDataTable, NodeFilter.class, INodeFilter.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends INodeFilter>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("id");
			filterClasses.put(name, info.b);
		}
		PL2.logger.info("Loaded: " + filterClasses.size() + " Filters");
	}

}