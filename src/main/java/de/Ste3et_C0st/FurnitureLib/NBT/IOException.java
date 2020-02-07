package de.Ste3et_C0st.FurnitureLib.NBT;

@SuppressWarnings("serial")
public class IOException extends RuntimeException {
    public IOException() {
        super();
    }

    public IOException(String message) {
        super(message);
    }

    public IOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IOException(Throwable cause) {
        super(cause);
    }

}