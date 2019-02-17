package org.glavo.nbt.gui;

import org.controlsfx.dialog.ExceptionDialog;

public class NBTException extends RuntimeException {
    public NBTException() {
    }

    public NBTException(String message) {
        super(message);
    }

    public NBTException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTException(Throwable cause) {
        super(cause);
    }

    public NBTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public void showDialog() {
         new ExceptionDialog(this).showAndWait();
    }
}
