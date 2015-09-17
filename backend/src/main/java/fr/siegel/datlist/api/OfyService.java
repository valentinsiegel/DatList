package fr.siegel.datlist.api;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import fr.siegel.datlist.api.model.Ingredient;
import fr.siegel.datlist.api.model.Recipe;
import fr.siegel.datlist.api.model.User;

/**
 * Created by Val on 23/08/15.
 */
public class OfyService {
    static {
        ObjectifyService.register(Ingredient.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Recipe.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
