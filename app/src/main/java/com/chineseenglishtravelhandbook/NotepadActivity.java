package com.chineseenglishtravelhandbook;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class NotepadActivity extends AppCompatActivity {

    NotepadData notepadData;
    EditText titleEditText;
    LinedEditText bodyEditText;
    Intent previousScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_notepad);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notepad");

        Bundle data = getIntent().getExtras();
        notepadData = data.getParcelable("notes");

        setupNotepad();

//        Toast toast = Toast.makeText(getApplicationContext(), "Hello Javatpoint", Toast.LENGTH_SHORT);
//        toast.setMargin(50, 50);
//        toast.show();

    }

    public void setupNotepad()
    {
            titleEditText = (EditText) findViewById(R.id.titleEditText);
            bodyEditText = (LinedEditText) findViewById(R.id.bodyEditText);
            titleEditText.setText(notepadData.getTitle());
            bodyEditText.setText(notepadData.getBody());
            titleEditText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            refreshData();
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        refreshData();
        finish();
    }

    protected void refreshData()
    {
        notepadData.setTitle(titleEditText.getText().toString());
        notepadData.setBody(bodyEditText.getText().toString());
        Calendar calendar = Calendar.getInstance();
        notepadData.setModifiedDate(calendar.getTime());
        previousScreen = new Intent(this, activity_notepadrecycler.class);
        previousScreen.putExtra("notepaddata", notepadData);
        setResult(RESULT_OK, previousScreen);
    }
}
