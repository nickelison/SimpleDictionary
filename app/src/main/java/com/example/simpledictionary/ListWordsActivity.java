package com.example.simpledictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ListWordsActivity extends AppCompatActivity {
    ArrayList<WordList> wordList;
    WordList words;
    private boolean isFirstRun = true;
    private SharedPreferences prefs;
    DatabaseHelper dbHelper;
    private ListView listView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        listView = (ListView) findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);

        // Set up nav button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Add words to list view
        populateListView();
    }

    /* Refresh view when user comes from other activity */
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.word_list);
        listView = (ListView) findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);
        populateListView();
    }

    /* Make nav bar button work */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateListView() {
        wordList = new ArrayList<>();
        Cursor data = dbHelper.getData();
        int numRows = data.getCount();

        if (numRows == 0) {
            Toast.makeText(this, "No words in DB.", Toast.LENGTH_SHORT).show();
        } else {

            while (data.moveToNext()) {
                words = new WordList(data.getString(1));
                wordList.add(words);
            }

            Collections.sort(wordList, new Comparator<WordList>() {
                @Override
                public int compare(WordList wordList, WordList t1) {
                    return (wordList.getWord().compareTo(t1.getWord()));
                }
            });

            WordAdapter adapter = new WordAdapter(this, R.layout.word_list_adapter, wordList);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String word = ((TextView) view.findViewById(R.id.wordId)).getText().toString();
                Cursor data = dbHelper.getWordId(word);
                int wordId = -1;
                while (data.moveToNext()) {
                    wordId = data.getInt(0);
                }

                if (wordId != -1) {
                    Intent viewWordIntent = new Intent(ListWordsActivity.this, ViewWordActivity.class);
                    viewWordIntent.putExtra("word", word);
                    startActivity(viewWordIntent);
                }
            }
        });
    }


}