package fr.siegel.datlist.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import fr.siegel.datlist.backend.model.Ingredient;

/**
 * Created by Val on 23/08/15.
 */
public class OfyService {
    static {
        ObjectifyService.register(Ingredient.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
