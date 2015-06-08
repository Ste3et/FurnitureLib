package de.Ste3et_C0st.FurnitureLib.main;

public class ObjectID {

	private String ObjectID;
	
	public String getID(){return this.ObjectID;}
	public void setID(String s){this.ObjectID = s;}
	public ObjectID(String name){
		try {
			this.ObjectID = name+RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
