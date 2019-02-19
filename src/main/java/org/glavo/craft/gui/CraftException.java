package org.glavo.craft.gui;

import org.controlsfx.dialog.ExceptionDialog;

public class CraftException extends RuntimeException {
    public CraftException() {
    }

    public CraftException(String message) {
        super(message);
    }

    public CraftException(String message, Throwable cause) {
        super(message, cause);
    }

    public CraftException(Throwable cause) {
        super(cause);
    }

    public CraftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public void showDialog() {
         new ExceptionDialog(this).showAndWait();
    }
}
