package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Arrays;
import java.util.List;

import org.bukkit.util.EulerAngle;

public class Type {
	public enum BodyPart{
		HEAD("Head",11, new EulerAngle(0,0,0)), 
		BODY("Body",12, new EulerAngle(0,0,0)), 
		LEFT_ARM("Left_Arm",13, new EulerAngle(-10.0F, 0.0F, -10.0F)), 
		RIGHT_ARM("Right_Arm",14, new EulerAngle(-15.0F, 0.0F, 10.0F)), 
		LEFT_LEG("Left_Leg",15, new EulerAngle(-1.0F, 0.0F, -1.0F)), 
		RIGHT_LEG("Right_Leg",16, new EulerAngle(1.0F, 0.0F, 1.0F));
		
		String name;
		EulerAngle angle;
        int field;
        
        BodyPart(String name,int field,EulerAngle angle){
            this.name=name;
            this.field=field;
            this.angle = angle;
        }
        
        public String getName(){
            return name;
        }
        
        public int getField(){
            return field;
        }

        public EulerAngle getDefAngle(){
        	return angle;
        }
        
		public static List<BodyPart> getList() {
			List<BodyPart> parts = Arrays.asList(BodyPart.HEAD, BodyPart.BODY, BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG);
			return parts;
		}
	}
}
