package com.example.alex.help;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    final String LOG_TAG = "myLogs";
    final String FILENAME = "file";
    final String DIR_SD = "MissedNumbers";
    final String FILENAME_SD = "Number.csv";
    HashSet Number;
    String a;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            textView = (TextView) findViewById(R.id.edit);
            Number = new HashSet();
           // startService(new Intent(this, MyService.class));
        }
    public  void GetSMSList(){
        Uri uri = Uri.parse("content://sms/inbox");
        // Создаем объект Cursor, используя запрос без параметров
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(cursor);
        if(cursor.moveToFirst()){
            Number.add(cursor.getString(2));
            while (cursor.moveToNext()){
                Number.add(cursor.getString(2));
            }
            //cursor.close();
            Iterator iterator = Number.iterator();
            textView.setText(textView.getText() + "\n" + iterator.next().toString() + "\n");
           while (iterator.hasNext() ){
               textView.setText(textView.getText() + iterator.next().toString() + "\n");
           }

        }
    }
        private void getMissedNumber() {
            String MyName = "Name";
            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                MyName = managedCursor.getString(name);
                int dircode = Integer.parseInt(callType);
                if (dircode == CallLog.Calls.MISSED_TYPE && MyName==null) {
                    Number.add(phNumber);
                }
            }
           // managedCursor.close();

                }

    public void onClick(View view) {

       getMissedNumber();
        GetSMSList();
         writeFileSD();
    }
    void writeFileSD() {
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            Iterator iterator = Number.iterator();
             a = new String();
            a = iterator.next().toString() + "\n";
            while (iterator.hasNext()){
                a = a + iterator.next().toString() + "\n";
            }
            // пишем данные
            bw.write(a);

            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        a = savedInstanceState.getString("a2");
        textView.setText(a);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume ");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("a2",textView.getText().toString());
        Log.d(LOG_TAG, "onSaveInstanceState");

    }
}
