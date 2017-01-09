package com.chineseenglishtravelhandbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CategoryPhrasesActivity extends AppCompatActivity {

    ArrayList<TravelPhraseData> travelList;
    ListView phrasesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category_phrases);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        travelList = getIntent().getParcelableArrayListExtra("phrases");
        String category = getIntent().getExtras().getString("Category");
        getSupportActionBar().setTitle(category);


        setupPhrasesListView();



    }


    public void setupPhrasesListView() {
        final Context context = this;
        phrasesListView = (ListView) findViewById(R.id.phrasesListView);
        populatePhrasesListView(travelList);


    }

    public void populatePhrasesListView(ArrayList travelPhrasesList) {
        PhrasesAdapterClass phrasesAdapter = new PhrasesAdapterClass(this, travelPhrasesList);
        phrasesListView.setAdapter(phrasesAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

}
