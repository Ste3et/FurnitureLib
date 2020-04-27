package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.text.SimpleDateFormat;

public class ExecuteTimer {

	private final long start;
	
	public ExecuteTimer() {
		this.start = System.currentTimeMillis();
	}

	public long getStart() {
		return start;
	}
	
	public long difference() {
		return System.currentTimeMillis() - getStart();
	}
	
	public String getDifference() {
		SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
		return time.format(difference());
	}
}
