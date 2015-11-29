package fr.siegel.datlist.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("dictionary")
    private ArrayList<String> dictionary;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        dictionary = new ArrayList<>();
        dictionary.add("");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(ArrayList<String> dictionary) {
        this.dictionary = dictionary;
    }
}
