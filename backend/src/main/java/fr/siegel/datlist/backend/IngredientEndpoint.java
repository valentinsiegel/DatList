/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package fr.siegel.datlist.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import static fr.siegel.datlist.backend.OfyService.ofy;
import com.google.appengine.api.users.User;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "ingredientEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.datlist.siegel.fr",
                ownerName = "backend.datlist.siegel.fr",
                packagePath = ""


        )
)
public class IngredientEndpoint {



    public IngredientEndpoint() {
    }

    @ApiMethod(name = "insertIngredient")
    public Ingredient insertIngredient(Ingredient ingredient) throws ConflictException {
        if (ingredient.getId() != null) {
            if (findIngredient(ingredient.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        ofy().save().entity(ingredient).now();
        return ingredient;
    }

    @ApiMethod(name = "listIngredients")
    public CollectionResponse<Ingredient> listQuote(@Nullable @Named("cursor") String cursorString,
                                                    @Nullable @Named("count") Integer count) {

        Query<Ingredient> query = ofy().load().type(Ingredient.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        QueryResultIterator<Ingredient> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            ingredients.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<Ingredient>builder().setItems(ingredients).setNextPageToken(cursorString).build();
    }

    private Ingredient findIngredient(Long id) {
        return ofy().load().type(Ingredient.class).id(id).now();
    }
}
