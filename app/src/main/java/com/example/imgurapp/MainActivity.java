package com.example.imgurapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imgurapp.Network.NetworkConnection;
import com.example.imgurapp.model.ImgurData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private List<ImgurData> imgurData;
    private ImgurAdapter imgurAdapter;
    private MenuItem switchItem;
    FloatingActionButton fab;
    ProgressBar progressBar;
    RelativeLayout searchView;
    EditText editTextSearch;
    TextView randomText;
    private boolean isGrid = true;
    String API = "https://api.imgur.com/3/gallery/search/{{top}}/{{week}}/{{page}}?q=";
    String[] headers = {"Authorization", "Client-ID 95b2cdf00bdc991", "Content-Type", "application/json"};   // Client ID for Imgur Authentication


    /**
     * The onCreate() is the first function that is called
     * when you launch your app.
     *
     * @param savedInstanceState savedInstanceState of the onCreate()
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imgurData = new ArrayList<>();
        progressBar = findViewById(R.id.progress);
        recyclerView = findViewById(R.id.recyclerview);
        searchView = findViewById(R.id.searchView);
        randomText = findViewById(R.id.randomText);
        fab = findViewById(R.id.fab);
        fab.hide();
        progressBar.setVisibility(View.GONE);
        String[] texts = {"The longest recorded flight of a chicken is thirteen seconds.",
                "Live, Laugh, Meme",
                "A flamingo can only eat with its head upside down.",
                "The world's oldest cat lived to be 38 years old.",
                "A cat has a flexible spine and can rotate its ears 180 degrees."};


        EditText editTextSearch = findViewById(R.id.search_bar);
        editTextSearch.requestFocus();
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(!NetworkConnection.isOnline(MainActivity.this) || !NetworkConnection.isConnecting(MainActivity.this)){
                        internet_check();    // check internet connection
                    }

                    /*
                    * Showing Random Facts while loading images
                    * */
                    Random random = new Random();
                    int index = random.nextInt(texts.length);
                    String someText = texts[index];
                    randomText.setText(someText);

                    progressBar.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    recyclerView.setVisibility(View.INVISIBLE);

                    String query = editTextSearch.getText().toString();
                   // Log.i("query", query);

                    String apiUrl = API + query;

                    /**
                     * This function is used when making a new request
                     * to the api for new search query
                     * @param q the search query that is the endpoint of the api.
                     */

                    new GetRequestTask().execute(new Object[]{apiUrl, headers});
                    return true;
                }
                return false;
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrid = !isGrid;
                if (isGrid) {
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    imgurAdapter.setIsGrid(true);
                    fab.setImageResource(R.drawable.list);
                    Log.i("Grid View", "Grid View Shown");
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    fab.setImageResource(R.drawable.grid);
                    imgurAdapter.setIsGrid(false);


                    Log.i("List View", "List View Shown");

                }
                recyclerView.setAdapter(imgurAdapter);

            }
        });
        imgurAdapter = new ImgurAdapter(imgurData,MainActivity.this,true);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this, imgurData.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*
         * Hiding Fab on Scrolling of Recyclerview List
         */

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();      //Hide Fab
                } else {
                    fab.show();      //Show Fab
                }
            }
        });

        /*
        * Adding Space for last item in recyclerView
        */

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == state.getItemCount() - 1) {
                    outRect.bottom = 16;
                }
            }
        });

    }



    /**
     * This function checks for the internet connectivity function
     * to avoid any crashes...
     *
     * @return void
     */
    private void internet_check() {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this)
                .setTitle("Oops, No Internet Connection")
                .setMessage("Please check your internet connection and try again later.")
                .setCancelable(false)
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        // Start the WiFi settings activity
                        startActivity(intent);
//                        System.exit(1);   //non-zero states that the JVM has to be killed.
                    }
                });

        AlertDialog alertDialog = alerBuilder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    }

    /**
     * The api calling is done with this function where the query is passed
     * as the argument and the api-key is passed as authentication.
     */

    public class GetRequestTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            String urlString = (String)params[0];
            String[] headers = (String[]) params[1];

            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                con.setRequestMethod("GET");

                // Set the headers
                for (int i = 0; i < headers.length; i += 2) {
                    con.setRequestProperty(headers[i], headers[i + 1]);
                }

                // Send the request and read the response
                int responseCode = con.getResponseCode();
                Log.d("GetRequestTask", "Response code: " + responseCode);


                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String apiResponseString = response.toString();
                try {
                    JSONObject jresponse = new JSONObject(apiResponseString);
                    boolean success = jresponse.getBoolean("success");
                    int status = jresponse.getInt("status");
                    if (success && status == 200) {
                        JSONArray dataArray = jresponse.getJSONArray("data");
                        imgurData.clear();
                        System.out.println(dataArray);
                        if (dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                JSONArray imagesArray = dataObject.getJSONArray("images");

                                String datetime = dataObject.getString("datetime");
                                String images_count = dataObject.getString("images_count");
                                String title = dataObject.getString("title");


                                if (imagesArray.length() > 0) {

                                    JSONObject imageObject = imagesArray.getJSONObject(0);
                                    String link = imageObject.getString("link");                                         // First Image Url from all images

                                    /*
                                     * Skipping the Data if it contains.mp4
                                     * and only focusing on images
                                     * (can show videos too if my client wants).
                                     */

                                    if (link.endsWith(".mp4")) {
                                        continue;                                                                           // skip
                                    }else{
                                        ImgurData listData = new ImgurData(title,datetime,link,images_count);               // adding data to Modal class
                                        imgurData.add(listData);

                                    }


//                                    Log.i("Title-", title);
//                                    Log.i("ImagesCOunt", images_count);
//                                    Log.i("DateTime", String.valueOf(datetime));                                           //checking if data was correct or not
//                                    Log.i("image url", link);

                                }
                            }
                        }


                    }
                } catch (JSONException e) {
                    // handle JSON parsing exception
                }

                // Return the response as a string
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            if (!imgurData.isEmpty()){
                recyclerView.setVisibility(View.VISIBLE);
                Collections.reverse(imgurData);            // Sorting Results in Reverse Chronological Order
                recyclerView.setAdapter(imgurAdapter);    // Setting Adapter to Recyclerview
                imgurAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                fab.show();
             }else {
                Toast.makeText(MainActivity.this, "Try again with different query.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }


            super.onPostExecute(s);
        }
    }


}
