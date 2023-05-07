package com.example.nfctag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    public static final String error_detected = "NFC tag not detected";
    public static final String success_on_writing = "Success on writing Text";
    public static final String error_on_writing = "Error occurred while writing. Try Again!";

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    IntentFilter writingTagFilter;

    boolean writeMode;

    Tag myTag;
    Context context;

    TextView edit_Msg;
    TextView nfc_Content;

    Button write_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TextViews
        edit_Msg = (TextView) findViewById(R.id.editMsg);
        nfc_Content = (TextView) findViewById(R.id.nfcContent);
        //Button
        write_button = findViewById(R.id.writeButton);




    }
}