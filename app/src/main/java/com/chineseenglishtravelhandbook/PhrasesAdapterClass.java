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
import android.support.v7.widget.RecyclerView;
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

import com.squareup.picasso.Picasso;

import java.util.Locale;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Danny on 28/08/2016.
 */

public class PhrasesAdapterClass extends RecyclerView.Adapter<PhrasesAdapterClass.ViewHolder> {

    Context context;
    ArrayList<TravelPhraseData> travelPhraseData;
    TTSManager ttsManager = null;

    public PhrasesAdapterClass(Context _context, ArrayList<TravelPhraseData> _travelPhraseData) {
        this.context = _context;
        this.travelPhraseData = _travelPhraseData;
        ttsManager = new TTSManager();

        ttsManager.init(context);

    }

    @Override
    public PhrasesAdapterClass.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.phrases_row, parent, false);
        PhrasesAdapterClass.ViewHolder viewHolder = new PhrasesAdapterClass.ViewHolder(rowView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final PhrasesAdapterClass.ViewHolder holder, int position) {

        final int _position = position;

        // set color of txtviews
        holder.travelPhrase.setTextColor(Color.BLACK);
        holder.pronounciation.setTextColor(Color.rgb(20, 99, 255));
        holder.travelPhrase.setTextColor(Color.rgb(153, 26, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Boolean traditional_switch = preferences.getBoolean("traditional_switch", true);

        if (traditional_switch == true)
        {
            holder.travelPhrase.setText("▶ " + (CharSequence) travelPhraseData.get(position).getTravelPhrase());
        } else
        {
            holder.travelPhrase.setText("▶ " + (CharSequence) travelPhraseData.get(position).getTravelSimpPhrase());
        }

        holder.homePhrase.setText((CharSequence) travelPhraseData.get(position).getHomePhrase());
        holder.pronounciation.setText("▶ " + (CharSequence) travelPhraseData.get(position).getPronounciation());

        //Outline
        ViewOutlineProvider viewOutlineProviderVoice = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = context.getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        holder.voicePhraseButton.setOutlineProvider(viewOutlineProviderVoice);

        //Outline
        ViewOutlineProvider viewOutlineProviderCopy = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = context.getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        holder.voicePhraseButton.setOutlineProvider(viewOutlineProviderCopy);
        holder.voicePhraseButton.setOutlineProvider(viewOutlineProviderCopy);



        // set click listener to copy phrases to the notebook.
        holder.copyPhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phrase = "";
                if (traditional_switch == true) {
                    phrase = "\n" + travelPhraseData.get(_position).getHomePhrase() + "\n" + travelPhraseData.get(_position).getPronounciation() + "\n" + travelPhraseData.get(_position).getTravelPhrase();
                } else
                {
                    phrase = "\n" + travelPhraseData.get(_position).getHomePhrase() + "\n" + travelPhraseData.get(_position).getPronounciation() + "\n" + travelPhraseData.get(_position).getTravelSimpPhrase();
                }
                Toast.makeText(context, "Copied to Notepad" + "\n" + phrase, Toast.LENGTH_SHORT).show();

                // insert phrase to notepad
                NotepadDatabaseHelper notepadDBHelper;
                notepadDBHelper = setupDatabaseHelper();

                if (traditional_switch == true) {
                    notepadDBHelper.insertNotepadData(travelPhraseData.get(_position).getHomePhrase(), travelPhraseData.get(_position).getTravelPhrase() + "\n" + travelPhraseData.get(_position).getPronounciation(), new Date());
                }
                else
                {
                    notepadDBHelper.insertNotepadData(travelPhraseData.get(_position).getHomePhrase(), travelPhraseData.get(_position).getTravelSimpPhrase() + "\n" + travelPhraseData.get(_position).getPronounciation(), new Date());
                }
                notepadDBHelper.close();
            }
        });


        // set click listener to speaker button.
        holder.voicePhraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak =  travelPhraseData.get(_position).getTravelPhrase();
                toSpeak = toSpeak.replaceAll("_", " ");
                ttsManager.initQueue(toSpeak);
            }
        });

    }

    @Override
    public int getItemCount() {
        return travelPhraseData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView travelPhrase;
        public final TextView homePhrase;
        public final TextView pronounciation;
        public final ImageButton copyPhraseButton;
        public final ImageButton voicePhraseButton;
        public final View topemptyview;
        public final View bottomemptyview;
        public final LinearLayout phrasesLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            travelPhrase = (TextView)itemView.findViewById(R.id.travelPhraseTextView);
            homePhrase = (TextView)itemView.findViewById(R.id.homePhraseTextView);
            pronounciation = (TextView)itemView.findViewById(R.id.pronounciationTextView);
            copyPhraseButton = (ImageButton) itemView.findViewById(R.id.copyImageButton);
            voicePhraseButton = (ImageButton) itemView.findViewById(R.id.voiceImageButton);
            topemptyview = (View) itemView.findViewById(R.id.topemptyview);
            bottomemptyview = (View) itemView.findViewById(R.id.bottomemptyview);

            phrasesLayout = (LinearLayout) itemView.findViewById(R.id.phrasesLayout);
        }

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
