package fr.siegel.datlist.api.model;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by Val on 14/09/15.
 */
@Entity
public class Recipe {

    @Id
    String name;

    @Parent
    Key<User> userKey;

    @Index
    String description;

    public Recipe() {
    }

    public Recipe(String name, Key<User> userKey, String description) {
        this.name = name;
        this.userKey = userKey;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Key<User> getUserKey() {
        return userKey;
    }
}
