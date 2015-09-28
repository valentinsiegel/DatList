package fr.siegel.datlist.api.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

/**
 * Created by Val on 01/09/15.
 */
@Entity
public class User {

    @Id
    private String username;

    @Index
    private String password;

    private ArrayList<String> dictionary = new ArrayList<>();

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getDictionary() {
        return dictionary;
    }

    public void addToDictionary(String ingredient) {
        dictionary.add(ingredient);
    }
}
