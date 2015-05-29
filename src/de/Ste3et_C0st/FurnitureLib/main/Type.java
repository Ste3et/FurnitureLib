package de.Ste3et_C0st.FurnitureLib.main;

public class Type {
	public enum BodyPart{
		HEAD("Head",11), 
		BODY("Body",12), 
		LEFT_ARM("Left_Arm",13), 
		RIGHT_ARM("Right_Arm",14), 
		LEFT_LEG("Left_Leg",15), 
		RIGHT_LEG("Right_Leg",16);
		
		String name;
        int field;
        
        BodyPart(String name,int field){
            this.name=name;
            this.field=field;
        }
        
        public String getName(){
            return name;
        }
        
        public int getField(){
            return field;
        }
	}
}
