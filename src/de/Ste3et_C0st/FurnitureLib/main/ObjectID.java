package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Random;

public class ObjectID {

	private String ObjectID;
	
	public String getID(){return this.ObjectID;}
	public ObjectID(Class<?> c){
	    String val = c.getName().substring(0,3);      
	    int ranChar = 65 + (new Random()).nextInt(90-65);
	    char ch = (char)ranChar;        
	    val += ch;      
	    Random r = new Random();
	    int numbers = 100000 + (int)(r.nextFloat() * 899900);
	    val += String.valueOf(numbers);
	    val += "-";
	    for(int i = 0; i<6;){
	        int ranAny = 48 + (new Random()).nextInt(90-65);
	        if(!(57 < ranAny && ranAny<= 65)){
	        char chr = (char)ranAny;      
	        val += chr;
	        i++;
	        }
	    }

	    this.ObjectID = val;
	}
}
