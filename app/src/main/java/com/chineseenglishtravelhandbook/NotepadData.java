package com.chineseenglishtravelhandbook;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Danny on 08/05/2016.
 */
public class NotepadData implements Parcelable {

    //private variables
    private int _id;
    private String title;
    private String body;
    Date modifiedDate;

    // constructor.  empty data.
    public NotepadData(){
        this._id = -1;
        this.title = "";
        this.body = "";
        this.modifiedDate = Calendar.getInstance().getTime();
    }

    // constructor
    public NotepadData(int id, String title, String body, Date modifiedDate){
        this._id = id;
        this.title = title;
        this.body = body;
        this.modifiedDate = modifiedDate;
    }


    protected NotepadData(Parcel in) {
        _id = in.readInt();
        title = in.readString();
        body = in.readString();
        modifiedDate = new Date(in.readLong());
    }

    public static final Creator<NotepadData> CREATOR = new Creator<NotepadData>() {
        @Override
        public NotepadData createFromParcel(Parcel in) {
            return new NotepadData(in);
        }

        @Override
        public NotepadData[] newArray(int size) {
            return new NotepadData[size];
        }
    };

    public void setID(Integer id)
    {
        _id = id;
    }

    public void setTitle(String _title)
    {
        this.title = _title;
    }

    public void setBody(String _body)
    {
        this.body = _body;
    }

    public void setModifiedDate(Date _modifiedDate)
    {
        this.modifiedDate = _modifiedDate;
    }

    public Integer getID() { return _id; }

    public String getTitle()
    {
        return title;
    }

    public String getBody()
    {
        return body;
    }

    public String getModifiedDate()
    {
        return getReadableModifiedDate();
    }

    public Date getUnModifiedDate()
    {
        return modifiedDate;
    }

    public String getReadableModifiedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault());
        String displayDate = sdf.format(modifiedDate);
        return displayDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(_id);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeLong(modifiedDate.getTime());
    }
}
