package com.cobnet.exception;

public class RomanNumberSupportFormatException extends Exception {

    public RomanNumberSupportFormatException(int num) {

        super("Roman format is not supported" + num);
    }
}
