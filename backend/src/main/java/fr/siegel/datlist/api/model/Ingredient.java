package fr.siegel.datlist.api.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * The object model for the data we are sending through endpoints
 */

@Entity
public class Ingredient {

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    Key<User> userKey;
    @Id
    private String name;

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public void setUserKey(String username) {
        this.userKey = Key.create(User.class, username);
    }
}