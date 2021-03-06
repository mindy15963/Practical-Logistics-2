package sonar.logistics.base.utils.worlddata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.base.ServerInfoHandler;
import sonar.logistics.core.tiles.displays.tiles.connected.ConnectedDisplay;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ConnectedDisplayData extends WorldSavedData {

	public static final String IDENTIFIER = "sonar.logistics.base.connections.gsiMap";
	public static final Map<Integer, NBTTagCompound> unloadedDisplays = new HashMap<>();
	
	public ConnectedDisplayData(String name) {
		super(name);
	}

	public ConnectedDisplayData() {
		super(IDENTIFIER);
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagList tag = nbt.getTagList("base", NBT.TAG_COMPOUND);
		
		for (int t = 0; t < tag.tagCount(); t++) {
			NBTTagCompound screenTag = tag.getCompoundTagAt(t);
			int registryID = screenTag.getInteger("registryID");
			unloadedDisplays.put(registryID, screenTag);
			/*
			ConnectedDisplay display = new ConnectedDisplay(world, registryID);
			display.setGSI(new DisplayGSI(display, world, registryID));
			display.readData(screenTag, SyncType.SAVE);
			display.registryID = registryID;
			ServerInfoHandler.instance().connectedDisplays.put(registryID, display);
			*/
		}
		

	}

	@Nonnull
    @Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (Entry<Integer, ConnectedDisplay> display : ServerInfoHandler.instance().connectedDisplays.entrySet()) {
			if (display.getValue() != null) {
				NBTTagCompound screenTag = new NBTTagCompound();
				screenTag.setInteger("registryID", display.getKey());
				display.getValue().writeData(screenTag, SyncType.SAVE);
				list.appendTag(screenTag);
			}
		}
		for (Entry<Integer, NBTTagCompound> display : unloadedDisplays.entrySet()) {
			NBTTagCompound screenTag = display.getValue();			
			screenTag.setInteger("registryID", display.getKey());
			list.appendTag(screenTag);
		}
		
		compound.setTag("base", list);
		//unloadedDisplays.clear();
		return compound;
	}

	public boolean isDirty() {
		return true;
	}
}