package sonar.logistics.integration.ic2;
/*FIXME
import com.google.base.collect.Lists;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import sonar.logistics.api.asm.ASMInfoRegistry;
import sonar.logistics.api.info.register.IInfoRegistry;
import sonar.logistics.api.info.register.IMasterInfoRegistry;
import sonar.logistics.api.info.register.RegistryType;

@ASMInfoRegistry(modid = "IC2")
public class IC2InfoRegistry implements IInfoRegistry {

	public void registerBaseReturns(IMasterInfoRegistry registry) {
		registry.registerValidReturn(IReactor.class);
	}

	public void registerBaseMethods(IMasterInfoRegistry registry) {
		registry.registerMethods(IReactorChamber.class, RegistryType.TILE, Lists.newArrayList("getReactorInstance"));
		registry.registerMethods(IReactor.class, RegistryType.TILE, Lists.newArrayList("getHeat", "getMaxHeat", "getReactorEUEnergyOutput"));		
	}

	public void registerAllFields(IMasterInfoRegistry registry) {	
		registry.registerFields(TileEntityNuclearReactorElectric.class, RegistryType.TILE, Lists.newArrayList("EmitHeat"));
	}

	public void registerAdjustments(IMasterInfoRegistry registry) {
		registry.registerInfoAdjustments(Lists.newArrayList("IReactor.getReactorEUEnergyOutput"), "", "EU/t");		
	}
}
*/