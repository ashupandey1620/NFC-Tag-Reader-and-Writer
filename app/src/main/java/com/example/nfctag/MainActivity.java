package com.example.nfctag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

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


        context = this;

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    if(myTag==null)
                    {
                        Toast.makeText(context,error_detected,Toast.LENGTH_LONG).show();
                    }
                    else {
                        write("PlainText|"+edit_Msg.getText().toString(),myTag);
                        Toast.makeText(context,success_on_writing,Toast.LENGTH_LONG).show();
                    }
                }
                catch(IOException e)
                {
                    Toast.makeText(context,error_on_writing,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                catch(FormatException e){
                    Toast.makeText(context,error_on_writing,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null)
        {
            Toast.makeText(this,"Mobile phone do not support NFC",Toast.LENGTH_LONG).show();
            finish();
        }


        readfromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        writeTagFilters = new IntentFilter[] {tagDetected} ;

    }
}