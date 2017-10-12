package com.example.oegod.criminalintent.socket;

import android.util.Log;

import com.example.oegod.criminalintent.Crime;
import com.google.gson.Gson;
import com.oegodf.crime.CrimeBase;
import com.oegodf.crime.CrimesMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import javax.crypto.Cipher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConnectionWorker implements ObservableOnSubscribe<CrimesMap<Crime>> {
    private DataInputStream mInputStream;
    private DataOutputStream mOutputStream;
    private CrimesMap<Crime> mCrimes;
    private ClientKeysUtils mClientKeys;
    private ObservableEmitter<CrimesMap<Crime>> mEmitter;
    private Socket mConnection;
    private boolean mSubscribed;

    public ConnectionWorker() {
        mClientKeys = new ClientKeysUtils();
    }

    private boolean isSubscribed() {
        return mSubscribed;
    }

    @Override
    public void subscribe(ObservableEmitter<CrimesMap<Crime>> emitter) {
        mSubscribed = true;
        Log.d("DEBUG", "SUBS");
        sendCommand("JSON"); // Устанавливаем, что в подключении будем использовать не сериализацию объекта, а JSON
        sendCommand("HELLO"); // Обмениваемся ключами
        mEmitter = emitter;
        while (isSubscribed()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            try {
                if (mInputStream.available() > 0) {
                    proceedMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void proceedMessage() throws IOException {
        byte[] commandByte = new byte[6];
        mInputStream.read(commandByte);
        commandByte = CriminalUtils.trimBytes(commandByte);
        String command = new String(commandByte, "UTF-8");
        if (!Objects.equals(command, "PING")) {
            Log.d("DEBUG", "Command REC: " + command);
        }
        try {
            proceedServerCommand(command);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void proceedServerCommand(String command) throws IOException, ClassNotFoundException {
        switch (command) {
            case "HELLOK":
                proceedKeysExchange();
                break;
            case "HELLO":
                Log.d("DEBUG", "Server said that encryption is disabled");
                sendCommand("CRIMES");
                break;
            case "KTEST":
                checkTestKeys();
                break;
            case "CRIMEN":
                getCrimesFromServer();
                break;
            case "CRIMES":
                getCrimesFromServer();
                break;
            case "CRIME":
                printCrime();
                break;
            case "CSEND":
                break;
            case "SEND":
                break;
            case "PING":
                sendCommand("PONG");
                break;
        }
    }

    private void checkTestKeys() throws IOException, ClassNotFoundException {
        byte[] lengthHeader = new byte[4];
        mInputStream.read(lengthHeader);
        int dataSize = ByteBuffer.wrap(lengthHeader).getInt();
        byte[] body = new byte[dataSize];
        mInputStream.read(body);
        body = mClientKeys.decrypt(body);
        String message = (String) CriminalUtils.deserialize(body);
        if (message != null && Objects.equals(message, "SUCCESS")) {
            Log.d("DEBUG", "Keys test success. Encryption enabled!");
            mClientKeys.setEnabled(true);
            sendCommand("CRIMES");
        } else {
            mClientKeys.setEnabled(false);
            Log.d("DEBUG", "Keys test failed. Encryption disabled.");
            sendCommand("ENCDIS");
        }
    }

    private void proceedKeysExchange() throws IOException, ClassNotFoundException {
        byte[] lengthHeader = new byte[4];
        mInputStream.read(lengthHeader);
        int dataSize = ByteBuffer.wrap(lengthHeader).getInt();
        byte[] body = new byte[dataSize];
        mInputStream.read(body);
        PublicKey serverPublicKey = (PublicKey) CriminalUtils.deserialize(body);
        mClientKeys.setServerPublicKey(serverPublicKey);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, mClientKeys.getServerPublicKey());
            byte[] cipherData = cipher.doFinal(mClientKeys.getKey().getEncoded());
            sendBytes("PKEY", cipherData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCrimesFromServer() throws IOException {
        mCrimes = new CrimesMap<>();
        byte[] commandByte = new byte[6];
        mInputStream.read(commandByte);
        commandByte = CriminalUtils.trimBytes(commandByte);
        String message = new String(commandByte, "UTF-8");
        while (!Objects.equals(message, "CSEND")) {
            printCrime();
            commandByte = new byte[6];
            mInputStream.read(commandByte);
            commandByte = CriminalUtils.trimBytes(commandByte);
            message = new String(commandByte, "UTF-8");
        }
        mEmitter.onNext(mCrimes);
    }

    private void printCrime() throws IOException {
        Crime crime = CriminalUtils.readCrime(mInputStream, mClientKeys);
        if (crime != null) {
            mCrimes.put(crime.getId(), crime);
        }
    }

    private void createCrime(CrimeBase crime) {
        sendCommand("ADD", crime);
    }

    private void deleteCrime(String arguments) {
        try {
            Integer crimeIndex = Integer.parseInt(arguments);
            Crime crime = (Crime) mCrimes.getCrimeByPosition(crimeIndex);
            if (crime != null) {
                sendCommand("DELETE", crime);
            }
        } catch (NumberFormatException e) {
            Log.d("DEBUG", "Please specify crime id");
        } catch (IndexOutOfBoundsException e) {
            Log.d("DEBUG", "Crime not found, did you asked crimes from server?");
        }
    }
    // Send command

    public void sendCommand(String command) {
        final byte[] bodyBytes;
        try {
            bodyBytes = command.getBytes("UTF-8");
            ByteBuffer headerBuffer = ByteBuffer.allocate(6).put(bodyBytes);
            byte[] message = headerBuffer.array();
            mOutputStream.write(message);
            mOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command, Crime crime) {
        sendCommand(command, (Object) crime);
    }

    private void sendCommand(String command, Object object) {
        try {
            byte[] crimeBytes = new Gson().toJson(object).getBytes("UTF-8");
            if (mClientKeys.isEnabled()) {
                sendBytes(command, mClientKeys.encrypt(crimeBytes));
            } else {
                sendBytes(command, crimeBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBytes(String command, byte[] bytes) {
        try {
            final byte[] bodyBytes = command.getBytes("UTF-8");
            ByteBuffer headerBuffer = ByteBuffer.allocate(6).put(bodyBytes);
            headerBuffer.position(0);
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4).putInt(bytes.length);
            lengthBuffer.position(0);
            byte[] header = headerBuffer.array();
            byte[] message = ByteBuffer.allocate(10 + bytes.length).put(header).put(lengthBuffer.array()).put(bytes).array();
            mOutputStream.write(message);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConnection(Connection connection) {
        mConnection = connection.getSocket();
        mInputStream = connection.getInputStream();
        mOutputStream = connection.getOutputStream();
    }
}
