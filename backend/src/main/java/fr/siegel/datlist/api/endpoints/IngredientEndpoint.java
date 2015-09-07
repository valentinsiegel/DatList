/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package fr.siegel.datlist.api.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.api.model.Ingredient;
import fr.siegel.datlist.api.model.User;

import static fr.siegel.datlist.api.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "datListApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.datlist.siegel.fr",
                ownerName = "backend.datlist.siegel.fr",
                packagePath = ""
        )
)
public class IngredientEndpoint {
    @ApiMethod(name = "insertIngredient")
    public void insertIngredient(Ingredient ingredient) throws ConflictException {
        if (ingredient.getId() != null) {
            if (findIngredient(ingredient.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        ofy().save().entity(ingredient).now();
    }

    @ApiMethod(name = "getIngredientByName")
    public Ingredient ingredientByName(@Named("name") String name) throws ConflictException {
        if (name != null) {
            return ofy().load().type(Ingredient.class).filter("name", name).first().now();
        } else {
            throw new ConflictException("");
        }
    }

    @ApiMethod(name = "getIngredientById")
    public Ingredient ingredientById(@Named("id") Long id) throws ConflictException {
        if (id != null) {
            return ofy().load().type(Ingredient.class).id(id).now();
        } else {
            throw new ConflictException("");
        }
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


    /*
    USER RELATED METHODS
     */

    @ApiMethod(name = "createUser")
    public User createUser(User user) throws ConflictException {
        if (findUser(user.getUsername()) == null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new ConflictException("Username not available");
    }

    @ApiMethod(name = "updateUser")
    public User updateUser(User user) throws NotFoundException {
        if (findUser(user.getId()) != null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new NotFoundException("User not found");
    }

    @ApiMethod(name = "deleteUser")
    public void deleteUser(@Named("userId") long id) throws NotFoundException {
        if (findUser(id) != null) {
            ofy().delete().type(User.class).id(id).now();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    //RETRIEVE USER ID, SERVES AS A LOGIN METHOD
    @ApiMethod(name = "retrieveUserId")
    public User retrieveUserId(@Named("username") String username, @Named("password") String password) throws NotFoundException {
        User user = loginUser(username, password);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("Incorrect username or password");
    }

    //RETURNS USER DATA FOR A USER ID
    @ApiMethod(name = "retrieveUserById")
    public User retrieveUserById(@Named("userId") long id) throws NotFoundException {
        User user = findUser(id);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("User not found");
    }

    //LOGIN THE USER
    private User loginUser(String username, String password) {
        return ofy().load().type(User.class).filter("username", username).filter("password", password).first().now();
    }

    //CHECK IF USER EXIST
    private User findUser(long id) {
        return ofy().load().type(User.class).id(id).now();
    }

    //CHECK IF USERNAME IS AVAILABLE
    private User findUser(String username) {
        return ofy().load().type(User.class).filter("username", username).first().now();
    }


}

