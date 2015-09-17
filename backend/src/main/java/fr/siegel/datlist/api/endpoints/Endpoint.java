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
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.Key;

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

    /*
    RECIPE METHODS
     */

    //CREATES A RECIPE
    @ApiMethod(name = "createRecipe")
    public void createRecipe(Recipe recipe) throws ConflictException {
        if (recipe.getName() != null) {
            if (findRecipe(recipe.getName()) != null) {
                throw new ConflictException("Recipe Already Exist");
            }
            ofy().save().entity(recipe).now();
        }
    }

    //UPDATE RECIPE
    @ApiMethod(name = "updateRecipe")
    public Recipe updateRecipe(Recipe recipe) throws NotFoundException {
        if (findRecipe(recipe.getName()) != null) {
            ofy().save().entity(recipe).now();
            return recipe;
        }
        throw new NotFoundException("Recipe not found");
    }

    //DELETE RECIPE
    @ApiMethod(name = "deleteRecipe")
    public void deleteRecipe(@Named("recipeId") String name) throws NotFoundException {
        if (findRecipe(name) != null) {
            ofy().delete().type(Recipe.class).id(name).now();
        }
        throw new NotFoundException("Recipe not found");
    }

    //RETRIEVE RECIPES BY USER
    @ApiMethod(name = "retrieveRecipeByUser")
    public Recipe retrieveRecipeByUser(@Named("useKey") String useKey, @Nullable @Named("count") Integer count) {
        return ofy().load().type(Recipe.class).ancestor(useKey).first().now();
    }

    //CHECK IF RECIPE ALREADY EXIST
    private Recipe findRecipe(String name) {
        return ofy().load().type(Recipe.class).id(name).now();
    }

/*
    //INSERT INGREDIENT INTO BUY LIST
    @ApiMethod(name = "insertIngredientToBuy")
    public void insertIngredient(Ingredient ingredient) throws ConflictException {
        if (ingredient.getId() != null) {
            if (/lifindIngredient(ingredient.getId()) != null) {
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
    }*/


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
        if (findUser(user.getUsername()) != null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new NotFoundException("User not found");
    }

    @ApiMethod(name = "deleteUser")
    public void deleteUser(@Named("userId") String username) throws NotFoundException {
        if (findUser(username) != null) {
            ofy().delete().type(User.class).id(username).now();
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

