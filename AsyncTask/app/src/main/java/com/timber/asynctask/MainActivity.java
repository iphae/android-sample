package com.timber.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String a = "timber_a";
        long b = 20l;
        byte[] data = new byte[]{1,2,3,4};

        new AsyncTask<Object, byte[], byte[]>() {
            String a;
            long b;
            byte[] data;
            @Override
            protected byte[] doInBackground(Object... bytes) {
                a = (String)bytes[0];
                b = (long)bytes[1];
                data = (byte[])bytes[2];

                return data;
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                Log.i("AsyncTask-timber", "a:" + a);
                Log.i("AsyncTask-timber", "b:" + b);
                for (int i = 0; i < bytes.length; i++) {
                    Log.i("AsyncTask-timber", String.format("data in index_%d:%d", i, bytes[i]));
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{a,b,data});
    }
}
