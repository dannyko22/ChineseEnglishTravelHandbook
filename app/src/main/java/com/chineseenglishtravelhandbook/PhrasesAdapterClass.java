package com.chineseenglishtravelhandbook;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Danny on 28/08/2016.
 */

public class PhrasesAdapterClass extends ArrayAdapter {

    Context context;
    ArrayList<TravelPhraseData> travelPhraseData;
    LayoutInflater inflater;
    TTSManager ttsManager = null;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.phrases_row, parent, false);
        final TextView travelPhrase = (TextView) row.findViewById(R.id.travelPhraseTextView);
        //travelPhrase.setTypeface(null, Typeface.BOLD);
        travelPhrase.setTextColor(Color.BLACK);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Boolean traditional_switch = preferences.getBoolean("traditional_switch", true);

        TextView homePhrase = (TextView) row.findViewById(R.id.homePhraseTextView);
        final TextView pronounciation = (TextView) row.findViewById(R.id.pronounciationTextView);


        // set color of txtviews
        pronounciation.setTextColor(Color.rgb(20, 99, 255));
        travelPhrase.setTextColor(Color.rgb(153, 26, 0));

        if (traditional_switch == true)
        {
            travelPhrase.setText("▶ " + (CharSequence) travelPhraseData.get(position).getTravelPhrase());
        } else
        {
            travelPhrase.setText("▶ " + (CharSequence) travelPhraseData.get(position).getTravelSimpPhrase());
        }

        homePhrase.setText((CharSequence) travelPhraseData.get(position).getHomePhrase());
        pronounciation.setText("▶ " + (CharSequence) travelPhraseData.get(position).getPronounciation());

        final ImageButton copyPhraseButton = (ImageButton) row.findViewById(R.id.copyImageButton);
        final ImageButton voicePhraseButton = (ImageButton) row.findViewById(R.id.voiceImageButton);

        //Outline
        ViewOutlineProvider viewOutlineProviderVoice = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = context.getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        voicePhraseButton.setOutlineProvider(viewOutlineProviderVoice);

        //Outline
        ViewOutlineProvider viewOutlineProviderCopy = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = context.getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        voicePhraseButton.setOutlineProvider(viewOutlineProviderCopy);

        final View topemptyview = (View) row.findViewById(R.id.topemptyview);
        final View bottomemptyview = (View) row.findViewById(R.id.bottomemptyview);

        final LinearLayout phrasesLayout = (LinearLayout) row.findViewById(R.id.phrasesLayout);

        ttsManager = new TTSManager();

        ttsManager.init(context);




        // set visible to off by default
        hideButtons(travelPhrase, pronounciation, voicePhraseButton, copyPhraseButton);

        //set click listener such that it expands when layout is clicked.
        phrasesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pronounciation.getVisibility() == View.GONE)
                {
                    showButtons(travelPhrase, pronounciation, voicePhraseButton, copyPhraseButton);
                }
                else
                {
                    hideButtons(travelPhrase, pronounciation, voicePhraseButton, copyPhraseButton);
                }

            }
        });


        // set click listener to copy phrases to the notebook.
        copyPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // this code is to go back up the hierarchy of phrases_row.xml such that we find the listview so that we can get the position index of where the user clicked.
                View parentLinearLayout = ((View) view.getParent());
                LinearLayout parentLinearview = ((LinearLayout) parentLinearLayout.getParent());
                LinearLayout parentLinearview2 = ((LinearLayout) parentLinearview.getParent());
                ListView parentparentListview = ((ListView) parentLinearview2.getParent());
                int position = parentparentListview.getPositionForView(view);

                String phrase = "";
                if (traditional_switch == true) {
                    phrase = "\n" + travelPhraseData.get(position).getHomePhrase() + "\n" + travelPhraseData.get(position).getPronounciation() + "\n" + travelPhraseData.get(position).getTravelPhrase();
                } else
                {
                    phrase = "\n" + travelPhraseData.get(position).getHomePhrase() + "\n" + travelPhraseData.get(position).getPronounciation() + "\n" + travelPhraseData.get(position).getTravelSimpPhrase();
                }
                Toast.makeText(context, "Copied to Notepad" + "\n" + phrase, Toast.LENGTH_SHORT).show();

                // insert phrase to notepad
                NotepadDatabaseHelper notepadDBHelper;
                notepadDBHelper = setupDatabaseHelper();

                if (traditional_switch == true) {
                    notepadDBHelper.insertNotepadData(travelPhraseData.get(position).getHomePhrase(), travelPhraseData.get(position).getTravelPhrase() + "\n" + travelPhraseData.get(position).getPronounciation(), new Date());
                }
                else
                {
                    notepadDBHelper.insertNotepadData(travelPhraseData.get(position).getHomePhrase(), travelPhraseData.get(position).getTravelSimpPhrase() + "\n" + travelPhraseData.get(position).getPronounciation(), new Date());
                }
                notepadDBHelper.close();
            }
        });

        // set click listener to speaker button.
        voicePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // this code is to go back up the hierarchy of phrases_row.xml such that we find the listview so that we can get the position index of where the user clicked.
                View parentLinearLayout = ((View) view.getParent());
                LinearLayout parentLinearview = ((LinearLayout) parentLinearLayout.getParent());
                LinearLayout parentLinearview2 = ((LinearLayout) parentLinearview.getParent());
                ListView parentparentListview = ((ListView) parentLinearview2.getParent());
                int position = parentparentListview.getPositionForView(view);



                String toSpeak =  travelPhraseData.get(position).getTravelPhrase();
                toSpeak = toSpeak.replaceAll("_", " ");
                ttsManager.initQueue(toSpeak);

            }
        });

        //return super.getView(position, convertView, parent);
        return row;
    }

    private void hideButtons(TextView travelPhrase, TextView pronounciation, ImageButton voicePhraseButton, ImageButton copyPhraseButton)
    {
        copyPhraseButton.setVisibility(View.GONE);
        voicePhraseButton.setVisibility(View.GONE);
        pronounciation.setVisibility(View.GONE);
        travelPhrase.setVisibility(View.GONE);

    }

    private void showButtons(TextView travelPhrase, TextView pronounciation, ImageButton voicePhraseButton, ImageButton copyPhraseButton)
    {
        copyPhraseButton.setVisibility(View.VISIBLE);
        //hide speaker button if google TTS not available
        if (!ttsManager.isGoogleTTSAvailable())
        {
            voicePhraseButton.setVisibility(View.GONE);
        } else {
            voicePhraseButton.setVisibility(View.VISIBLE);
        }
        pronounciation.setVisibility(View.VISIBLE);
        travelPhrase.setVisibility(View.VISIBLE);
    }



    public PhrasesAdapterClass(Context _context, ArrayList<TravelPhraseData> _travelPhraseData) {
        super(_context, R.layout.phrases_row, _travelPhraseData);

        this.context = _context;
        this.travelPhraseData = _travelPhraseData;

    }

    public NotepadDatabaseHelper setupDatabaseHelper()
    {
        NotepadDatabaseHelper notepadDBHelper = new NotepadDatabaseHelper(context);

        try {
            notepadDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            notepadDBHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        return notepadDBHelper;
    }

}
