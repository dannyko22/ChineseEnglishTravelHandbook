package com.chineseenglishtravelhandbook;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * Created by Danny on 08/05/2016.
 */
public class TravelPhraseData implements Parcelable {

    //private variables
    int _id;
    String category;
    String homePhrase;
    String travelPhrase;
    String travelSimpPhrase;
    String pronounciation;
    String filename;

    // constructor.  empty data.
    public TravelPhraseData(){
        this._id = -1;
        this.category = null;
        this.homePhrase = null;
        this.travelPhrase = null;
        this.travelSimpPhrase = null;
        this.pronounciation = null;
        this.filename = null;
    }

    // constructor
    public TravelPhraseData(int id, String category, String homePhrase, String travelPhrase, String travelSimpPhrase, String pronounciation, String filename){
        this._id = id;
        this.homePhrase = homePhrase;
        this.category = category;
        this.travelPhrase = travelPhrase;
        this.travelSimpPhrase = travelSimpPhrase;
        this.pronounciation = pronounciation;
        this.filename = filename;
    }


    protected TravelPhraseData(Parcel in) {
        _id = in.readInt();
        category = in.readString();
        homePhrase = in.readString();
        travelPhrase = in.readString();
        travelSimpPhrase = in.readString();
        pronounciation = in.readString();
        filename = in.readString();
    }

    public static final Creator<TravelPhraseData> CREATOR = new Creator<TravelPhraseData>() {
        @Override
        public TravelPhraseData createFromParcel(Parcel in) {
            return new TravelPhraseData(in);
        }

        @Override
        public TravelPhraseData[] newArray(int size) {
            return new TravelPhraseData[size];
        }
    };

    public void setID(Integer id)
    {
        _id = id;
    }

    public void setCategory(String _category)
    {
        this.category = _category;
    }

    public void setHomePhrase(String _homePhrase)
    {
        this.homePhrase = _homePhrase;
    }

    public void setTravelPhrase(String _travelPhrase)
    {
        this.travelPhrase = _travelPhrase;
    }

    public void setTravelSimpPhrase(String _travelSimpPhrase)
    {
        this.travelSimpPhrase = _travelSimpPhrase;
    }

    public void setFilename(String _filename) {this.filename = _filename;}

    public void setPronounciation(String _pronounciation) {this.pronounciation = _pronounciation;}

    public String getCategory()
    {
        return category;
    }

    public String getTravelPhrase()
    {
        return travelPhrase;
    }

    public String getTravelSimpPhrase()
    {
        return travelSimpPhrase;
    }

    public String getHomePhrase()
    {
        return homePhrase;
    }

    public String getPronounciation() { return pronounciation;}

    public String getFilename() {return filename;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(_id);
        parcel.writeString(category);
        parcel.writeString(homePhrase);
        parcel.writeString(travelPhrase);
        parcel.writeString(travelSimpPhrase);
        parcel.writeString(pronounciation);
        parcel.writeString(filename);
    }
}
