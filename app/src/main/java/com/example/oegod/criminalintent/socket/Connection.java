package com.example.oegod.criminalintent.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by worried_man on 06.10.2017.
 */

public class Connection {
    private Socket mSocket;
    private DataInputStream mInputStream;
    private DataOutputStream mOutputStream;

    public Connection(Socket socket, DataInputStream input, DataOutputStream output) {
        mSocket = socket;
        mInputStream = input;
        mOutputStream = output;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public DataInputStream getInputStream() {
        return mInputStream;
    }

    public DataOutputStream getOutputStream() {
        return mOutputStream;
    }
}
