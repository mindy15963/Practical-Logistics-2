package sonar.logistics.api.signals;

import sonar.logistics.api.Info;
import net.minecraft.nbt.NBTTagCompound;

public abstract class SignallerStatement {

	public final static int number = 0;
	public final static int string = 1;
	public boolean invert;

	public abstract int getType();

	public abstract boolean canSignal(Info info);

	public void invertSignal(){
		invert=!invert;
	}
	
	public static void writeToNBT(NBTTagCompound tag, SignallerStatement statement) {
		tag.setInteger("t", statement.getType());
		tag.setBoolean("in", statement.invert);
		switch (statement.getType()) {
		case number:
			IntegerStatement integer = (IntegerStatement) statement;
			tag.setInteger("emit", integer.emitType.getInt());
			tag.setInteger("target", integer.target.getInt());
			break;
		case string:
			StringStatement string = (StringStatement) statement;
			tag.setString("word", string.target.getString());
			break;
		}
	}

	public static SignallerStatement readFromNBT(NBTTagCompound tag) {
		int type = tag.getInteger("t");
		SignallerStatement statement = null;
		switch (type) {
		case number:
			statement = new IntegerStatement(tag.getInteger("emit"), tag.getInteger("target"));
			break;
		case string:
			statement = new StringStatement(tag.getString("word"));
			break;
		}
		if (statement != null)
			statement.invert = tag.getBoolean("in");
		return statement;
	}

}