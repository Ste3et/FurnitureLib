package de.Ste3et_C0st.FurnitureLib.Utilitis.exception;

public class WrongVersionException extends Exception {

    //this exception is called if you use an unsupported version
    public WrongVersionException(String errorMessage) {
        super(errorMessage);
    }

}