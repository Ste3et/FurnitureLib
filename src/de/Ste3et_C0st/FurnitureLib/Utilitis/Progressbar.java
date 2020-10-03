package de.Ste3et_C0st.FurnitureLib.Utilitis;

public class Progressbar {
	
	private int max = 0, count = 0;
	private double d = 0;
	
	public Progressbar(int max){
		this.max = max;
		System.out.print("Progress: ");
	}
	
	public void increase() {
		this.count++;
	}
	
	public void increaseAndPrint() {
		this.increase();
		double d = ((double) count / (double) max);
		d = Math.round(d * 10d) / 10d;
		if(this.d != d) {
			this.d = d;
			System.out.print("|");
		}
	}
}
