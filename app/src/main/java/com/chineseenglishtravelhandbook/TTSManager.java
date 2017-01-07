package com.chineseenglishtravelhandbook;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TTSManager {

    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener, "com.google.android.tts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isGoogleTTSAvailable()
    {
        Boolean engineExist = false;
        for (TextToSpeech.EngineInfo engines : mTts.getEngines()) {
            if (engines.toString().equals("EngineInfo{name=com.google.android.tts}"))
            {
                engineExist = true;
            }
            Log.d("Engine Info " , engines.toString());
        }

        return engineExist;
    }

    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(new Locale("zh-CN"));
                //mTts.setPitch(Float.valueOf("1.2"));
                mTts.setSpeechRate(Float.valueOf("0.7"));
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                }
            } else {
                Log.e("error", "Initialization Failed!");
            }
        }
    };

    public void shutDown() {
        mTts.shutdown();
    }

    public void addQueue(String text) {
        if (isLoaded)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
            } else {
                mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        else
            Log.e("error", "TTS Not Initialized");
    }

    public void initQueue(String text) {

        if (isLoaded)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
            } else {
                mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        else
            Log.e("error", "TTS Not Initialized");
    }
}