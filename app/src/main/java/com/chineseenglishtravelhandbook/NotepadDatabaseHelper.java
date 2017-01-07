package com.chineseenglishtravelhandbook;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Danny on 08/05/2016.
 */
public class NotepadDatabaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.chineseenglishtravelhandbook/databases/";

    private static String DB_NAME = "notepad.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // table name
    private static final String TABLE_NOTEPADTRAVEL = "notepaddb";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String TITLE = "Title";
    private static final String BODY = "Body";
    private static final String MODIFIEDDATE = "Modified_Date";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public NotepadDatabaseHelper(Context context) {

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
        myDataBase = SQLiteDatabase.openDatabase(myFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

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
        myContext.deleteDatabase(TABLE_NOTEPADTRAVEL);
    }

// Add your public helper methods to access and get content from the database.
// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
// to you to create adapters for your views.

    // Getting all categories and phrases in the database
    ArrayList<NotepadData> getAllNotepadData() {

        NotepadData notepadData;
        Cursor cursor;
        ArrayList<NotepadData> items = new ArrayList<NotepadData>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTEPADTRAVEL;

        //SQLiteDatabase db = this.getReadableDatabase();
        cursor = myDataBase.query(TABLE_NOTEPADTRAVEL, new String[]{KEY_ID, TITLE, BODY, MODIFIEDDATE}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                notepadData = new NotepadData();
                notepadData.setID(Integer.parseInt(cursor.getString(0)));
                notepadData.setTitle(cursor.getString(1));
                notepadData.setBody(cursor.getString(2));
                notepadData.setModifiedDate(new Date(cursor.getLong(3)));
                // Adding contact to list
                items.add(notepadData);
            } while (cursor.moveToNext());
        }
        return items;
    }

    public boolean updateNotepadData(Integer _id, String _title, String _body, String _date)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, _title);
        contentValues.put(BODY, _body);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - h:mm a");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        contentValues.put(MODIFIEDDATE, convertedDate.getTime());

        myDataBase.update(TABLE_NOTEPADTRAVEL, contentValues, "_id=?", new String[] { Integer.toString(_id) });
        return true;
    }

    public Integer insertNotepadData(String _title, String _body, Date _date)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, _title);
        contentValues.put(BODY, _body);
        contentValues.put(MODIFIEDDATE, _date.getTime());

        long _id = myDataBase.insert(TABLE_NOTEPADTRAVEL, null, contentValues);

        return (int) _id;
    }

    public int deleteNotepadData(Integer _id)
    {
        return myDataBase.delete(TABLE_NOTEPADTRAVEL, "_id=?", new String[] {Integer.toString(_id)});
    }


}
