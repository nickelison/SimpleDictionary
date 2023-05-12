package com.example.simpledictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, "Words", null, 1);
        this.context = context;
    }

    /* Create table */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createWordTable = "CREATE TABLE Words(wID INTEGER PRIMARY KEY AUTOINCREMENT, word text)";
        String createDefinitionTable = "CREATE TABLE Definitions(dID INTEGER PRIMARY KEY AUTOINCREMENT, wID int, pos text, def text, ex text, syns text, is_primary bool, FOREIGN KEY(wID) REFERENCES Words(wID))";
        sqLiteDatabase.execSQL(createWordTable);
        sqLiteDatabase.execSQL(createDefinitionTable);
    }

    /* Drop table (if exists) */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Words");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Definitions");
        onCreate(sqLiteDatabase);
    }

    /* Add word to Words table */
    public boolean addWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("word", word);

        long res = db.insert("Words", null, cv);

        if (res == -1) {
            return false;
        } else {
            return true;
        }
    }

    /* Add new definition to Definitions table */
    public boolean addDef(int wID, String pos, String def, String ex, String syns, boolean is_primary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("wID", wID);
        cv.put("pos", pos);
        cv.put("def", def);
        cv.put("ex", ex);
        cv.put("syns", syns);
        cv.put("is_primary", is_primary);

        long res = db.insert("Definitions", null, cv);

        if (res == -1) {
            return false;
        } else {
            return true;
        }
    }

    /* Check if word exists in DB */
    public boolean checkIfWordExists(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        String query = "SELECT word FROM Words WHERE word='" + word + "'";
        data = db.rawQuery(query, null);

        if (data.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /* Get all table data */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Words";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /* Get word ID given word */
    public Cursor getWordId(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT wID FROM Words WHERE word = '" + word + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /* Get definition ID given word ID and definition */
    public int getDefId(int wID, String def) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT dID FROM Definitions WHERE def = '" + def + "' and wID = " + Integer.toString(wID);
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        int defId = data.getInt(0);
        return defId;
    }

    /* Get definition data */
    public Cursor getAllDefData(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT dID, pos, def, ex, syns FROM Definitions WHERE wID IN (SELECT wID FROM Words WHERE word = '" + word + "')";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /* Delete word */
    public void deleteWord(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM Words WHERE word ='" + name + "'";
        db.execSQL(query);
    }
}