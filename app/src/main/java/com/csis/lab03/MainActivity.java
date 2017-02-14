package com.csis.lab03; //package we're in


//android imports
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

//PURE DATA IMPORTS

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher; //must declare this to use later, used to receive data from sendEvents
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//Mandatory
        setContentView(R.layout.activity_main);//Mandatory

        //For declaring and initialising XML items, Always of form OBJECT_TYPE VARIABLE_NAME = (OBJECT_TYPE) findViewById(R.id.ID_SPECIFIED_IN_XML);

        Button button1 = (Button) findViewById(R.id.button1); //findViewById uses the ids you specified in the xml!
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);


        Switch switch1 = (Switch) findViewById(R.id.switch1);//declared the switch here pointing to id onOffSwitch

        final EditText freqText = (EditText) findViewById(R.id.freqText);
        final EditText freqInd = (EditText) findViewById(R.id.freqInd);
        final EditText freqMod = (EditText) findViewById(R.id.freqMod);

        try { // try the code below, catch errors if things go wrong
            initPD(); //method is below to start PD
            loadPDPatch("synth.pd"); // This is the name of the patch in the zip
        } catch (IOException e) {
            e.printStackTrace(); // print error if init or load patch fails.
            finish(); // end program
        }

        //Check to see if switch1 value changes
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ? 1.0f : 0.0f; // value = (get value of isChecked, if true val = 1.0f, if false val = 0.0f)
                sendFloatPD("onOff", val); //send value to patch, receiveEvent names onOff

            }
        });

        //<------BUTTON1 CLICK LISTENER--------------->
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("freq", Float.parseFloat(freqText.getText().toString()));

            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("index", Float.parseFloat(freqInd.getText().toString()));

            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("mod", Float.parseFloat(freqMod.getText().toString()));

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send a float to PD("the name of the [receive] object in pd to
                //send to", the value to send);

                sendBangPD("button2");
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBangPD("button3");
            }
        });


        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBangPD("button4");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFloatPD("button6", 1.0f);
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFloatPD("button7", 1.0f);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    @Override //If screen is resumed
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override//If we switch to other screen
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }

    //METHOD TO SEND FLOAT TO PUREDATA PATCH
    public void sendFloatPD(String receiver, Float value)//REQUIRES (RECEIVEEVENT NAME, FLOAT VALUE TO SEND)
    {
        PdBase.sendFloat(receiver, value); //send float to receiveEvent
    }

    //METHOD TO SEND BANG TO PUREDATA PATCH
    public void sendBangPD(String receiver) {

        PdBase.sendBang(receiver); //send bang to receiveEvent
    }


    //<---THIS METHOD LOADS SPECIFIED PATCH NAME----->
    private void loadPDPatch(String patchName) throws IOException {
        File dir = getFilesDir(); //Get current list of files in directory
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.synth), dir, true); //extract the zip file in raw called synth
            File pdPatch = new File(dir, patchName); //Create file pointer to patch
            PdBase.openPatch(pdPatch.getAbsolutePath()); //open patch
        } catch (IOException e) {

        }
    }

    //<---THIS METHOD INITIALISES AUDIO SERVER----->
    private void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate(); //get sample rate from system
        PdAudio.initAudio(sampleRate, 0, 2, 8, true); //initialise audio engine

        dispatcher = new PdUiDispatcher(); //create UI dispatcher
        PdBase.setReceiver(dispatcher); //set dispatcher to receive items from puredata patches
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}