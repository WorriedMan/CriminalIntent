package com.example.oegod.criminalintent.socket;

import android.util.Log;

import com.example.oegod.criminalintent.Crime;
import com.google.gson.Gson;
import com.oegodf.crime.CrimeBase;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class CriminalUtils {
    private CriminalUtils() throws Exception {
        throw new Exception();
    }

    static byte[] trimBytes(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Class c = Class.forName("com.oegodf.crime.CrimeBase");
        Log.d("DEBUG", "CLOS " + c);
        Object object = null;
        try {
            object = is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    static byte[] serialize(Crime crime) throws IOException {
        return serialize((Object) crime);
    }

    static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } finally {
            bos.close();
        }
    }

    static Crime readCrime(DataInputStream stream, ClientKeysUtils keysUtils) {
        try {
            byte[] lengthHeader = new byte[4];
            stream.read(lengthHeader);
            int dataSize = ByteBuffer.wrap(lengthHeader).getInt();
            byte[] body = new byte[dataSize];
            stream.read(body);
            if (keysUtils.isEnabled()) {
                body = keysUtils.decrypt(body);
            }
            String crimeString = new String(body, "UTF-8");
            CrimeBase crime = new Gson().fromJson(crimeString, CrimeBase.class);
            return new Crime(crime);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
