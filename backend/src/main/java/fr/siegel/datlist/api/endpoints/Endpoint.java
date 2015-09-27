/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package fr.siegel.datlist.api.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.api.model.Ingredient;
import fr.siegel.datlist.api.model.Recipe;
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
public class Endpoint {

    /* RECIPE RELATED METHODS
     *
     */

    /**
     * Insert a new recipe in the database.
     *
     * @param recipe   The recipe to insert into the database.
     * @param username The username from the user who created te recipe
     * @throws ConflictException
     */
    @ApiMethod(name = "createRecipe", httpMethod = HttpMethod.POST)
    public void createRecipe(Recipe recipe, @Named("userKey") String username) throws ConflictException {
        if (recipe.getName() != null) {
            recipe.setUserKey(username);
            if (findRecipe(recipe) != null) {
                throw new ConflictException("Recipe Already Exist");
            }
            ofy().save().entity(recipe).now();
        }
    }

    @ApiMethod(name = "getRecipe", httpMethod = HttpMethod.GET)
    public Recipe getRecipe(@Named("recipeName") String recipeName, @Named("username") String username) throws NotFoundException {
        Key<User> userKey = Key.create(User.class, username);
        Key<Recipe> recipeKey = Key.create(userKey, Recipe.class, recipeName);
        return ofy().load().type(Recipe.class).filterKey("=", recipeKey).first().now();

    }

    //UPDATE RECIPE
    @ApiMethod(name = "updateRecipe", httpMethod = HttpMethod.PUT)
    public Recipe updateRecipe(Recipe recipe, @Named("userKey") String userKey) throws NotFoundException {
        recipe.setUserKey(userKey);
        if (findRecipe(recipe) != null) {
            ofy().save().entity(recipe).now();
            return recipe;
        }
        throw new NotFoundException("Recipe not found");
    }

    //DELETE RECIPE
    @ApiMethod(name = "deleteRecipe", httpMethod = HttpMethod.DELETE)
    public void deleteRecipe(Recipe recipe, @Named("userKey") String userKey) throws NotFoundException {
        if (findRecipe(recipe) != null) {

            ofy().delete().type(Recipe.class).id(null).now();
        }
        throw new NotFoundException("Recipe not found");
    }

    //RETRIEVE RECIPES BY USER
    @ApiMethod(name = "retrieveRecipeByUser", httpMethod = HttpMethod.GET)
    public CollectionResponse<Recipe> retrieveRecipeByUser(@Named("username") String username, @Nullable @Named("cursor") String cursorString,
                                                           @Nullable @Named("count") Integer count) {
        Key<User> userKey = Key.create(User.class, username);
        Query<Recipe> query = ofy().load().type(Recipe.class).ancestor(userKey);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Recipe> ingredients = new ArrayList<Recipe>();
        QueryResultIterator<Recipe> iterator = query.iterator();
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
        return CollectionResponse.<Recipe>builder().setItems(ingredients).setNextPageToken(cursorString).build();
    }


    //CHECK IF RECIPE ALREADY EXIST
    private Recipe findRecipe(Recipe recipe) {
        Key<Recipe> recipeKey = Key.create(recipe);
        return ofy().load().type(Recipe.class).filterKey("=", recipeKey).first().now();
    }

    /*
        INGREDIENT RELATED METHODS
    */

    //INSERT INGREDIENT INTO BUY LIST
    @ApiMethod(name = "insertIngredient")
    public void insertIngredient(Ingredient ingredient, @Named("username") String username) throws ConflictException {
        ingredient.setUserKey(username);
        if (ingredient.getName() != null) {
            if (findIngredient(ingredient) != null) {
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
    public CollectionResponse<Ingredient> listQuote(@Named("username") String username, @Nullable @Named("cursor") String cursorString,
                                                    @Nullable @Named("count") Integer count) {
        Key<User> userKey = Key.create(User.class, username);
        Query<Ingredient> query = ofy().load().type(Ingredient.class).ancestor(userKey);
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

    private Ingredient findIngredient(Ingredient ingredient) {
        Key<Ingredient> ingredientKey = Key.create(ingredient);
        return ofy().load().type(Ingredient.class).filterKey("=", ingredientKey).first().now();
    }


    /*
    USER RELATED METHODS
     */

    @ApiMethod(name = "createUser", httpMethod = HttpMethod.POST)
    public User createUser(User user) throws ConflictException {
        if (findUser(user.getUsername()) == null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new ConflictException("Username not available");
    }

    @ApiMethod(name = "updateUser", httpMethod = HttpMethod.PUT)
    public User updateUser(User user) throws NotFoundException {
        if (findUser(user.getUsername()) != null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new NotFoundException("User not found");
    }

    @ApiMethod(name = "deleteUser", httpMethod = HttpMethod.DELETE)
    public void deleteUser(@Named("userId") String username) throws NotFoundException {
        if (findUser(username) != null) {
            ofy().delete().type(User.class).id(username).now();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    //RETRIEVE USER ID, SERVES AS A LOGIN METHOD
    @ApiMethod(name = "retrieveUserId", httpMethod = HttpMethod.GET)
    public User retrieveUserId(@Named("username") String username, @Named("password") String password) throws NotFoundException {
        User user = loginUser(username, password);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("Incorrect username or password");
    }

    //RETURNS USER DATA FOR A USER ID
    @ApiMethod(name = "retrieveUserById", httpMethod = HttpMethod.GET)
    public User retrieveUserById(@Named("userId") String username) throws NotFoundException {
        User user = findUser(username);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("User not found");
    }

    //LOGIN THE USER
    private User loginUser(String username, String password) {
        Key<User> userKey = Key.create(User.class, username);
        return ofy().load().type(User.class).filterKey("=", userKey).filter("password", password).first().now();
    }

    //CHECK IF USERNAME IS AVAILABLE
    private User findUser(String username) {
        return ofy().load().type(User.class).id(username).now();
    }


}

