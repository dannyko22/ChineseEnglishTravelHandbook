package com.chineseenglishtravelhandbook;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
/**
 * Created by Danny on 08/05/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.chineseenglishtravelhandbook/databases/";

    private static String DB_NAME = "travel.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // table name
    private static final String TABLE_TRAVELPHRASES = "travelphrasesdb";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String CATEGORY = "Category";
    private static final String HOME_PHRASE = "homePhrase";
    private static final String TRAVEL_PHRASE = "travelPhrase";
    private static final String PRONOUNCIATION_PHRASE = "pronounciation";
    private static final String FILENAME = "Filename";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
//do nothing - database already exist
        }else{

//By calling this method and empty database will be created into the default system path
//of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                File f = new File(DB_PATH);
                if (!f.exists()) {
                    f.mkdir();
                }

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            File myFile = myContext.getDatabasePath(DB_NAME);
            //checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            checkDB = SQLiteDatabase.openDatabase(myFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){


        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

// Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

//Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

//transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

//Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

//Open the database
        String myPath = DB_PATH + DB_NAME;
        File myFile = myContext.getDatabasePath(DB_NAME);
        myDataBase = SQLiteDatabase.openDatabase(myFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        myContext.deleteDatabase(TABLE_TRAVELPHRASES);
    }

// Add your public helper methods to access and get content from the database.
// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
// to you to create adapters for your views.

    // Getting all categories and phrases in the database
    ArrayList<TravelPhraseData> getAllTravelPhraseData() {

        TravelPhraseData travelPhraseData;
        Cursor cursor;
        ArrayList<TravelPhraseData> items = new ArrayList<TravelPhraseData>();
        String selectQuery = "SELECT * FROM " + TABLE_TRAVELPHRASES;

        //SQLiteDatabase db = this.getReadableDatabase();
        cursor = myDataBase.query(TABLE_TRAVELPHRASES, new String[]{KEY_ID, CATEGORY, HOME_PHRASE, TRAVEL_PHRASE, PRONOUNCIATION_PHRASE, FILENAME}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                travelPhraseData = new TravelPhraseData();
                travelPhraseData.setID(Integer.parseInt(cursor.getString(0)));
                travelPhraseData.setCategory(cursor.getString(1));
                travelPhraseData.setHomePhrase(cursor.getString(2));
                travelPhraseData.setTravelPhrase(cursor.getString(3));
                travelPhraseData.setPronounciation(cursor.getString(4));
                travelPhraseData.setFilename(cursor.getString(5));
                // Adding contact to list
                items.add(travelPhraseData);
            } while (cursor.moveToNext());
        }
        return items;
    }

    // Getting all categories and phrases in the database
    ArrayList<TravelPhraseData> getTravelPhraseDatabyCategory(String _category) {

        TravelPhraseData travelPhraseData;
        Cursor cursor;
        ArrayList<TravelPhraseData> items = new ArrayList<TravelPhraseData>();
        //String selectQuery = "SELECT * FROM " + TABLE_TRAVELPHRASES;

        //SQLiteDatabase db = this.getReadableDatabase();
        cursor = myDataBase.query(TABLE_TRAVELPHRASES, new String[]{KEY_ID, CATEGORY, HOME_PHRASE, TRAVEL_PHRASE, PRONOUNCIATION_PHRASE, FILENAME}, CATEGORY + "= ?", new String[]{_category}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                travelPhraseData = new TravelPhraseData();
                travelPhraseData.setID(Integer.parseInt(cursor.getString(0)));
                travelPhraseData.setCategory(cursor.getString(1));
                travelPhraseData.setHomePhrase(cursor.getString(2));
                travelPhraseData.setTravelPhrase(cursor.getString(3));
                travelPhraseData.setPronounciation(cursor.getString(4));
                travelPhraseData.setFilename(cursor.getString(5));
                // Adding contact to list
                items.add(travelPhraseData);
            } while (cursor.moveToNext());
        }
        return items;
    }

    // Getting unique travel categories
    ArrayList<TravelCategoryData> getAllCategoryData() {

        TravelCategoryData travelCategoryData;
        Cursor cursor;
        Integer id = 0;
        ArrayList<TravelCategoryData> items = new ArrayList<TravelCategoryData>();
        //String selectQuery = "SELECT DISTINCT Category FROM " + TABLE_TRAVELPHRASES;

        //SQLiteDatabase db = this.getReadableDatabase();
        cursor = myDataBase.query(TABLE_TRAVELPHRASES, new String[]{KEY_ID, CATEGORY, HOME_PHRASE, TRAVEL_PHRASE, PRONOUNCIATION_PHRASE, FILENAME}, null, null, CATEGORY, null, null);

        if (cursor.moveToFirst()) {
            do {
                id = id+1;
                travelCategoryData = new TravelCategoryData();
                travelCategoryData.setID(id);
                travelCategoryData.setCategory(cursor.getString(1));
                travelCategoryData.setFilename(cursor.getString(5));
                // Adding contact to list
                items.add(travelCategoryData);
            } while (cursor.moveToNext());
        }
        // return contact

        return items;
    }



}
