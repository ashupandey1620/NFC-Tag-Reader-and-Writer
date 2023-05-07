package com.example.nfctag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {


    public static final String error_detected = "NFC tag not detected";
    public static final String success_on_writing = "Success on writing Text";
    public static final String error_on_writing = "Error occurred while writing. Try Again!";

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    IntentFilter writingTagFilters[];

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


        context = this;

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (myTag == null) {
                        Toast.makeText(context, error_detected, Toast.LENGTH_LONG).show();
                    } else {
                        write("PlainText|" + edit_Msg.getText().toString(), myTag);
                        Toast.makeText(context, success_on_writing, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, error_on_writing, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, error_on_writing, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Mobile phone do not support NFC", Toast.LENGTH_LONG).show();
            finish();
        }


        readfromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        writingTagFilters = new IntentFilter[]{tagDetected};

    }

    private void readfromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

         String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        nfc_Content.setText("NFC CONTENT : "+text);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readfromIntent(intent);
        if(nfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        writeModeOff();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        writeModeOn();
    }

    //Enabling Write function
    private void writeModeOn()
    {
        writeMode=true;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);
    }

    //Disabling write function
    private void writeModeOff(){
        writeMode=false;
        nfcAdapter.disableForegroundDispatch(this);
    }



    private void write(String text,Tag tag) throws IOException, FormatException{
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new  NdefMessage(records);
        Ndef ndef  = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();

    }


}