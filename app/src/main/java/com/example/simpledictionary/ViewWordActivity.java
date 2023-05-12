package com.example.simpledictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewWordActivity extends AppCompatActivity {
    //private Button btnDelete;
    private TextView word_item;
    private TextView pos_item;
    private TextView def_item;
    private String selectedWord;
    private String pos;
    private String def;
    ArrayList<DefList> defList;
    DefList defs;
    private ListView listView;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        listView = (ListView) findViewById(R.id.listView);

        word_item = (TextView) findViewById(R.id.word_item);

        dbHelper = new DatabaseHelper(this);

        // Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent receivedIntent = getIntent();
        selectedWord = receivedIntent.getStringExtra("word");

        word_item.setText(selectedWord);

        populateListView(selectedWord);
    }

    public void populateListView(String word) {
        defList = new ArrayList<>();
        Cursor defData = dbHelper.getAllDefData(word);
        int numRows = defData.getCount();

        // Add definitions to view
        if (numRows == 0) {
            Toast.makeText(this, "No definitions...", Toast.LENGTH_SHORT).show();
        } else {
            while (defData.moveToNext()) {
                defs = new DefList(defData.getInt(0), defData.getString(1), defData.getString(2), defData.getString(3), defData.getString(4));
                int dID = defData.getInt(0);
                // TODO - get synonyms
                defList.add(defs);
            }

            DefAdapter adapter = new DefAdapter(word,this, R.layout.word_adapter, defList);
            listView.setAdapter(adapter);
        }
    }

    /* Make nav bar button work */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
        } else {
            createInformationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_tr,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createInformationDialog() {
        ((TextView) new AlertDialog.Builder(this)
                .setTitle("Delete Word")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteWord(selectedWord);
                        Toast.makeText(ViewWordActivity.this, "Word deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage(Html.fromHtml("" +
                        "<p>Are you sure you wish to delete this word?</p>"
                ))
                .show()
                .findViewById(android.R.id.message)) // must call after show() for hyperlinks to work
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

}
