package com.example.simpledictionary;

public class DefList {
    private int defId;
    private String pos;
    private String def;
    private String ex;
    private String syns;

    public DefList(int temp_def_id, String temp_pos, String temp_def, String temp_ex, String temp_syns) {
        defId = temp_def_id;
        pos = temp_pos;
        def = temp_def;
        ex = temp_ex;
        syns = temp_syns;
    }

    public String getDef() {
        return def;
    }

    public String getPos() {
        return pos;
    }

    public int getId() {
        return defId;
    }

    public String getEx() {
        return ex;
    }

    public String getSyns() {
        return syns;
    }
}

