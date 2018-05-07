package sonar.logistics.info.types;

import sonar.core.network.sync.BaseSyncListPart;
import sonar.core.network.sync.ICheckableSyncPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.info.register.LogicPath;
import sonar.logistics.api.lists.IMonitoredValue;
import sonar.logistics.api.lists.IMonitoredValueInfo;
import sonar.logistics.api.lists.values.InfoMonitoredValue;

import java.util.List;

/** typical implementation of IMonitorInfo which has a sync parts list for all the Info things it also has the required constructor which required empty constructor */
public abstract class BaseInfo<T extends IInfo> extends BaseSyncListPart implements IInfo<T>, ISyncableListener, IMonitoredValueInfo<T> {

	private LogicPath path;
	//public boolean setInfoRenderSize = false;

	public BaseInfo() {}

	public LogicPath getPath() {
		return path;
	}

	public T setPath(LogicPath path) {
		this.path = path;
		return (T) this;
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	public void onInfoStored() {}

	public boolean equals(Object object) {
		if (object instanceof IInfo) {
			IInfo info = (IInfo) object;
			return (info.isHeader() && isHeader()) || (this.isMatchingType(info) && isMatchingInfo((T) info) && isIdenticalInfo((T) info));
		}
		return false;
	}
	
	@Override
	public void identifyChanges(T newInfo) {
		List<ISyncPart> parts = syncList.getStandardSyncParts();
		List<ISyncPart> infoParts = syncList.getStandardSyncParts();
		for (int i = 0; i < parts.size(); i++) {
			ISyncPart toCheck = infoParts.get(i);
			if (toCheck instanceof ICheckableSyncPart) {
				if (!((ICheckableSyncPart) parts.get(i)).equalPart(toCheck)) {
					toCheck.getListener().markChanged(toCheck);
				}
			} else {
				toCheck.getListener().markChanged(toCheck);
			}
		}
	}

	@Override
	public IMonitoredValue<T> createMonitoredValue() {
		return new InfoMonitoredValue(this);
	}
}
