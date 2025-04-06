package com.zeynalovv.Testing;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;

public class main {
    public static void main(String[] args) {
        ObjectMapper json = new ObjectMapper();
        HashMap<String, String> table = new HashMap<>();
        table.put("Salam", "salam");
        table.put("Nigger", "nigger");
        try{
            json.writeValue(new File("data.json"), table);
            table.put("nuga", "Nuga");
            json.writeValue(new File("data.json"), table);
        }catch (Exception e){

        }


    }
}
