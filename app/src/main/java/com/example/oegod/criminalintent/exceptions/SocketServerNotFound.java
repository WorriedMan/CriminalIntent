package com.example.oegod.criminalintent.exceptions;

/**
 * Created by oegod on 13.09.2017.
 */

public class SocketServerNotFound extends Exception {
    public SocketServerNotFound() { super(); }
    public SocketServerNotFound(String message) { super(message); }
    public SocketServerNotFound(String message, Throwable cause) { super(message, cause); }
    public SocketServerNotFound(Throwable cause) { super(cause); }
}
