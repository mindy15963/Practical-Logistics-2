package sonar.logistics.base.data.holders;

import sonar.logistics.base.data.api.IData;
import sonar.logistics.base.data.api.IDataGenerator;
import sonar.logistics.base.data.api.IDataHolder;
import sonar.logistics.base.data.api.IDataWatcher;
import sonar.logistics.base.data.sources.IDataSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DataHolderEmpty implements IDataHolder {

    public final List<IDataWatcher> dataWatchers;
    public final IDataSource source;
    public IData data;

    public DataHolderEmpty(IDataSource source, IData freshData, int tickRate){
        this.dataWatchers = new ArrayList<>();
        this.source = source;
        this.data = freshData;
    }

    @Override
    public boolean isValid(){return false;};

    @Override
    public List<IDataWatcher> getDataWatchers() {
        return dataWatchers;
    }

    @Nonnull
    @Override
    public IData getData() {
        return data;
    }

    @Nonnull
    @Override
    public IDataSource getSource() {
        return source;
    }

    @Override
    public IDataGenerator getGenerator() {
        return null;
    }

    @Override
    public boolean canUpdateData() {
        return false;
    }

    @Override
    public void setData(IData value) {
        data = value;
    }

    @Override
    public boolean hasWatchers() {
        return false;
    }

    @Override
    public void setWatchers(boolean hasListeners) {}

    @Override
    public void tick() {}

    @Override
    public void setTick(int tick) {}
}
