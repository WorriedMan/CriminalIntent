package com.example.oegod.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.oegod.criminalintent.socket.Connection;
import com.example.oegod.criminalintent.socket.ConnectionWorker;
import com.oegodf.crime.CrimesMap;

import org.reactivestreams.Subscription;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CrimeListActivity extends SingleFragmentActivity {

    private ConnectionWorker mConnectionWorker;
    private Observable<CrimesMap<Crime>> sObservable;
    private CrimeListFragment mFragment;
    private Disposable mDisposableWorker;
    private Connection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUG", "ON LIST ACTIVITY CREATE");
        if (mConnection == null || mConnection.getSocket().isClosed()) {
            createConnection();
        }
        if (mConnectionWorker == null) {
            mConnectionWorker = new ConnectionWorker();
            CrimeLab.get(mConnectionWorker);
        }
        if (sObservable == null) {
            sObservable = Observable.create(mConnectionWorker)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new CrimeListFragment();
        return mFragment;
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "ON LIST ACTIVITY RESUME");
        super.onResume();
        mConnectionWorker.setConnection(mConnection);
        mDisposableWorker = sObservable.subscribe(map -> {
            CrimeLab.get().setCrimes(map);
            if (mFragment.isAdded()) {
                mFragment.updateUI();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        sObservable.unsubscribeOn(Schedulers.io());
        mDisposableWorker.dispose();
        Log.d("DEBUG", "ON LIST ACTIVITY STOP");
    }

    ConnectionWorker getConnectionWorker() {
        return mConnectionWorker;
    }

    private void createConnection() {
        Observable<Connection> observable = Observable.fromCallable(new Callable<Connection>() {
            @Override
            public Connection call() throws InterruptedException {
                Connection connection = establishConnectionToServer("10.0.2.2", 8080);
                while (connection == null) {
                    TimeUnit.SECONDS.sleep(1);
                    connection = establishConnectionToServer("10.0.2.2", 8080);
                }
                return connection;
            }

            private Connection establishConnectionToServer(String address, int serverPort) throws InterruptedException {
                try {
                    InetAddress ipAddress = InetAddress.getByName(address);
                    Socket socket = new Socket(ipAddress, serverPort);
                    InputStream sin = socket.getInputStream();
                    OutputStream sout = socket.getOutputStream();
                    DataInputStream input = new DataInputStream(sin);
                    DataOutputStream output = new DataOutputStream(sout);
                    Log.d("DEBUG", "Connected to crimes server " + address + ":" + serverPort);
                    return new Connection(socket, input, output);
                } catch (Exception x) {
                    Log.d("DEBUG", "Unable to find server " + address + ":" + serverPort + ", trying again in 1 seconds...");
                    return null;
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(connection -> mConnection = connection);
    }

}
