package com.chineseenglishtravelhandbook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.kobakei.ratethisapp.RateThisApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseHelper myDbHelper;
    ArrayList<TravelPhraseData> travelList;
    ArrayList<TravelCategoryData> travelCategoryList;
    RecyclerView categoryRecyclerView;
    CategoryAdapterClass categoryAdapterClass;
    LinearLayoutManager categoryLayoutManager;
    final Context context = this;
    private int MY_DATA_CHECK_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chinese Speak");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.clipboard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, activity_notepadrecycler.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        myDbHelper = new DatabaseHelper(this);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;
        }

        travelCategoryList = new ArrayList<TravelCategoryData>();
        travelCategoryList = myDbHelper.getAllCategoryData();

        setupCategoryListView();

        isStoragePermissionGranted();
        initializeAdNetwork();

        checkEngineExist(this);

        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.Config config = new RateThisApp.Config(1,1);
        config.setUrl("market://details?id=com.chineseenglishtravelhandbook");
        RateThisApp.init(config);
        RateThisApp.showRateDialogIfNeeded(this);

    }

    private void initializeAdNetwork() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void setupCategoryListView()
    {
        final Context context = this;

        categoryRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCategory);
        categoryRecyclerView.setHasFixedSize(true);
        //categoryLayoutManager = new LinearLayoutManager(this);
        categoryLayoutManager = new GridLayoutManager(this,2);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, convertDpToPx(10), true));
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //categoryRecyclerView.setLayoutManager(categoryLayoutManager);

        final GestureDetector mGestureDetector =
                new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        categoryRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {


            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {


                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int position = recyclerView.getChildLayoutPosition(child);
                    String category = travelCategoryList.get(position).getCategory();
                    Intent intent = new Intent(context, CategoryPhrasesActivity.class);
                    travelList = new ArrayList<TravelPhraseData>();
                    travelList = myDbHelper.getTravelPhraseDatabyCategory(category);

                    intent.putParcelableArrayListExtra("phrases", travelList);
                    intent.putExtra("Category", category);
                    startActivity(intent);


                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        populateCategoryListView(travelCategoryList);

    }

    public void populateCategoryListView(ArrayList travelCategoryList)
    {
        categoryAdapterClass = new CategoryAdapterClass(this, travelCategoryList);
        categoryRecyclerView.setAdapter(categoryAdapterClass);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ratemetoolbar) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //Try Google play
            intent.setData(Uri.parse("market://details?id=com.chineseenglishtravelhandbook"));
            if (!MyStartActivity(intent)) {
                //Market (Google play) app seems not installed, let's try to open a webbrowser
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.chineseenglishtravelhandbook"));
                if (!MyStartActivity(intent)) {
                    //Well if this also fails, we have run out of options, inform the user.
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.settingstoolbar)
        {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.notepad) {
            // Handle the notepad action
            Intent intent = new Intent(context, activity_notepadrecycler.class);
            startActivity(intent);
        } else if (id == R.id.settingsdrawer){
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.homeapps) {
            final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/developer?id=Danny%20Ko&hl=en"));
            startActivity(intent);
        } else if (id == R.id.rateme) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //Try Google play
            intent.setData(Uri.parse("market://details?id=com.chineseenglishtravelhandbook"));
            if (!MyStartActivity(intent)) {
                //Market (Google play) app seems not installed, let's try to open a webbrowser
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.chineseenglishtravelhandbook&hl=en"));
                if (!MyStartActivity(intent)) {
                    //Well if this also fails, we have run out of options, inform the user.
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(context, about_me.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }

    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            return false;
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    private void checkEngineExist(Context _context)
    {

        TextToSpeech tts = new TextToSpeech(_context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });

        String defaultEngine = tts.getDefaultEngine();

        Boolean engineExist = false;
        for (TextToSpeech.EngineInfo engines : tts.getEngines()) {
            if (engines.toString().equals("EngineInfo{name=com.google.android.tts}"))
            {
                engineExist = true;
            }
            Log.d("Engine Info " , engines.toString());
        }

        // if google tts doesn't exist, prompt a dialog box.
        if (!engineExist)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Google TTS Missing");
            alert.setMessage("Install Google Text-To-Speech app from the google play store to hear Chinese phrases");

            alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                }
            });

            alert.setNegativeButton("Play Store", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                    final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts&hl=en"));
                    startActivity(intent);

                }
            });

            alert.show();

        }
    };


}

