package de.Ste3et_C0st.FurnitureLib.LimitationManager;

public class LimitationInforamtion { 
	
	private final String type;
	private final int max, amount;
	
	public LimitationInforamtion(String type, int max, int amount) {
		this.type = type;
		this.max = max;
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public int getMax() {
		return max;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isCanceld() {
		if(isInfinite()) return false;
		return amount + 1 > max;
	}
	
	public boolean isInfinite() {
		return max < 1;
	}
	
}
