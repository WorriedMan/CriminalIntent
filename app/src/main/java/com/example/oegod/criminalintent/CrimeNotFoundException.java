package com.example.oegod.criminalintent;

/**
 * Created by oegod on 13.09.2017.
 */

public class CrimeNotFoundException extends Exception {
    public CrimeNotFoundException() { super(); }
    public CrimeNotFoundException(String message) { super(message); }
    public CrimeNotFoundException(String message, Throwable cause) { super(message, cause); }
    public CrimeNotFoundException(Throwable cause) { super(cause); }
}
