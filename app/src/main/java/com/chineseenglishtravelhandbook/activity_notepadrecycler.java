package com.chineseenglishtravelhandbook;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import java.io.IOException;
import java.util.ArrayList;

public class activity_notepadrecycler extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    NotepadDatabaseHelper notepadDBHelper;
    ArrayList<NotepadData> notepadDataList;
    notepadRecyclerAdapter mAdapter;
    final Integer resultCode = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_notepadrecycler);
        final Context context = this;



        FloatingActionButton notepadfab = (FloatingActionButton) findViewById(R.id.notepadfab);
        notepadfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(context, NotepadActivity.class);
                NotepadData _notepadData = new NotepadData();
                intent.putExtra("notes",_notepadData);
                startActivityForResult(intent, resultCode);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notepad");



        setupRecyclerList();
    }

    public void setupRecyclerList()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.note_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        final GestureDetector mGestureDetector =
//                new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
//                    @Override
//                    public boolean onSingleTapUp(MotionEvent e) {
//                        return true;
//                    }
//                });
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//
//
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
//
//
//                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
//
//
//                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
//
//                    int position = recyclerView.getChildLayoutPosition(child);
//                    NotepadData selectedNoteItem = notepadDataList.get(position);
//                    Intent intent = new Intent(context, NotepadActivity.class);
//
//                    intent.putExtra("notes", selectedNoteItem);
//                    startActivityForResult(intent, resultCode );
//
//
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });

        setupDatabaseHelper();
        notepadDataList = new ArrayList<NotepadData>();
        notepadDataList = notepadDBHelper.getAllNotepadData();
        setupNotepadList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        NotepadData _notepadData = (NotepadData) data.getParcelableExtra("notepaddata");

        // if this is a new entry...
        if (_notepadData.getID().equals(-1))
        {
            if (!_notepadData.getTitle().trim().isEmpty() || !_notepadData.getBody().trim().isEmpty()) {
                _notepadData.setID(notepadDBHelper.insertNotepadData(_notepadData.getTitle(), _notepadData.getBody(), _notepadData.getUnModifiedDate()));
                notepadDataList.add(_notepadData);
            }
        }
        // if this is an existing entry, modify it.
        else {
            notepadDataList.get(_notepadData.getID() - 1).setTitle(_notepadData.getTitle());
            notepadDataList.get(_notepadData.getID() - 1).setBody(_notepadData.getBody());
            notepadDataList.get(_notepadData.getID() - 1).setModifiedDate(_notepadData.getUnModifiedDate());
            notepadDBHelper.updateNotepadData(_notepadData.getID(), _notepadData.getTitle(), _notepadData.getBody(), _notepadData.getModifiedDate());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setupNotepadList()
    {
        final Context context = this;
        mAdapter = new notepadRecyclerAdapter(notepadDataList, this, new notepadRecyclerAdapter.MyAdapterListener(){
            @Override
            public void buttonViewOnClick(View v,  int position)
            {
                Log.w("", "button" + position);
                NotepadData _data = notepadDataList.get(position);
                notepadDBHelper.deleteNotepadData(_data.getID());
                notepadDataList.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void otherViewOnClick(View v, int position)
            {
                Log.w("", "other" + position);
                    NotepadData selectedNoteItem = notepadDataList.get(position);
                    Intent intent = new Intent(context, NotepadActivity.class);

                    intent.putExtra("notes", selectedNoteItem);
                    startActivityForResult(intent, resultCode );
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setupDatabaseHelper()
    {
        notepadDBHelper = new NotepadDatabaseHelper(this);

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

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
