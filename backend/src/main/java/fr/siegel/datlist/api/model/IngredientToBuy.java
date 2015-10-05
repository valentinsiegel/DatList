package fr.siegel.datlist.api.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by Val on 29/09/15.
 */
@Entity
public class IngredientToBuy {

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    Key<User> userKey;
    @Id
    private String name;

    public IngredientToBuy() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserKey(String username) {
        this.userKey = Key.create(User.class, username);
    }

}
