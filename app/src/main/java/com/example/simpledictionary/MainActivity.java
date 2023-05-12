package com.example.simpledictionary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btnSearch, btnView;
    private EditText editText;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get input and button views
        editText = (EditText) findViewById(R.id.search_input);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnView = (Button) findViewById(R.id.btn_view);
        dbHelper = new DatabaseHelper(this);

        // Set up nav bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("SimpleDictionary");

        // Search for a word
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wordExists = false;
                String word = editText.getText().toString().toLowerCase();
                wordExists = dbHelper.checkIfWordExists(word);

                if (editText.length() == 0) {
                    Toast.makeText(MainActivity.this, "Must enter something in field", Toast.LENGTH_SHORT).show();
                } else {
                    if (wordExists == false) {
                        editText.setText("");
                        getNewWordData(word, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                openWord(word);
                            }
                        });
                    } else {
                        editText.setText("");
                        openWord(word);
                    }
                }
            }
        });

        // View saved words
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListWordsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void openWord(String word) {
        Intent viewWordIntent = new Intent(MainActivity.this, ViewWordActivity.class);
        viewWordIntent.putExtra("word", word);
        viewWordIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // navigation broken w/o
        startActivity(viewWordIntent);
    }

    public interface VolleyCallBack {
        void onSuccess();
    }

    public void getNewWordData(String word, final VolleyCallBack callBack) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://wordsapiv1.p.rapidapi.com/words/" + word;

        // Make API request object
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String def = null;
                String pos = null;
                String ex = null;
                String syns = "";
                boolean is_primary = false;

                // Add word to DB
                dbHelper.addWord(word);

                // Get ID of new word
                Cursor data = dbHelper.getWordId(word);
                data.moveToFirst();
                int wordId = data.getInt(0);

                // Get definition data
                try {
                    JSONArray results = response.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        // If first definition, set is_primary to true
                        if (i == 0) {
                            is_primary = true;
                        } else {
                            is_primary = false;
                        }

                        // Get definition and POS
                        def = results.getJSONObject(i).getString("definition");
                        pos = results.getJSONObject(i).getString("partOfSpeech");

                        // Get first example if examples exist
                        if (!results.getJSONObject(i).isNull("examples")) {
                            JSONArray tmpArray = results.getJSONObject(i).getJSONArray("examples");
                            ex = (String) tmpArray.get(0);
                        }

                        // If there are synonyms, create comma separated string of all syns
                        if (!results.getJSONObject(i).isNull("synonyms")) {
                            JSONArray tmpArray = results.getJSONObject(i).getJSONArray("synonyms");

                            // For each synonym
                            for (int j = 0; j < tmpArray.length(); j++) {
                                String synTmp = tmpArray.get(j).toString();

                                if (synTmp != null) {
                                    if (j != 0) {
                                        syns += ", ";
                                    }

                                    syns += synTmp;
                                }
                            }
                        }

                        // Add entry to Definitions table
                        dbHelper.addDef(wordId, pos, def, ex, syns, is_primary);

                        syns = ""; // reset synonym string for next definition
                        callBack.onSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(MainActivity.this, "API Error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            // Send request headers w/ API key
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com");
                headers.put("X-RapidAPI-Key", "KEY");
                return headers;
            }
        };

        requestQueue.add(req);
    }

    /* Close DB connection */
    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
