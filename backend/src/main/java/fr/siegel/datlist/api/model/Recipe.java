package fr.siegel.datlist.api.model;


import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
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

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    Key<User> userKey;

    @Index
    String description;

    public Recipe() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setUserKey(String userKey) {
        this.userKey = Key.create(User.class, userKey);
    }
}
