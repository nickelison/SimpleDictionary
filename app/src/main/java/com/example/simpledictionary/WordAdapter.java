package com.example.simpledictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<WordList> {
    private LayoutInflater layoutInflater;
    private ArrayList<WordList> words;
    private int viewRID;

    public WordAdapter(Context context, int textViewResourceId, ArrayList<WordList> words) {
        super(context, textViewResourceId, words);
        this.words = words;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewRID = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parents) {
        convertView = layoutInflater.inflate(viewRID, null);

        WordList word = words.get(position);

        if (word != null) {
            TextView Word = (TextView) convertView.findViewById(R.id.wordId);
            if (Word != null) {
                Word.setText((word.getWord()));
            }

        }

        return convertView;
    }
}
