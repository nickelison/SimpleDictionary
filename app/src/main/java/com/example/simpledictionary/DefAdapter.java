package com.example.simpledictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DefAdapter extends ArrayAdapter<DefList> {
    private LayoutInflater layoutInflater;
    private ArrayList<DefList> defs;
    private ArrayList<String > syns;
    private int viewRID;

    public DefAdapter(String word, Context context, int textViewResourceId, ArrayList<DefList> defs) {
        super(context, textViewResourceId, defs);
        this.defs = defs;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewRID = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parents) {
        convertView = layoutInflater.inflate(viewRID, null);
        DefList def = defs.get(position);

        if (def != null) {
            // Get view objects
            TextView Def = (TextView) convertView.findViewById(R.id.wordMeaning);
            TextView DefEx = (TextView) convertView.findViewById(R.id.wordExample);
            TextView Syns = (TextView) convertView.findViewById(R.id.wordSynonyms);

            // Set POS + definition
            if (Def != null) {
                Def.setText(("(" + def.getPos() + ") - " + def.getDef()));
            }

            // Set example
            if (DefEx != null && def.getEx() != null) {
                DefEx.setText((def.getEx()));
            } else {
                DefEx.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }

            // Set synonyms
            if (Syns != null && !def.getSyns().isEmpty()) {
                Syns.setText(("Synonyms: " + def.getSyns()));
            } else {
                Syns.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
        }

        return convertView;
    }
}
