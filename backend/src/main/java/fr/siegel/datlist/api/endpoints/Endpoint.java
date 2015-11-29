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
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.api.model.Ingredient;
import fr.siegel.datlist.api.model.IngredientToBuy;
import fr.siegel.datlist.api.model.IngredientToBuyList;
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

    public Endpoint() {

    }


    @ApiMethod(name = "buyIngredient", httpMethod = HttpMethod.POST)
    public void buyIngredient(final IngredientToBuy ingredientToBuy, @Named("username") final String username) throws NotFoundException {

        ofy().transact(new VoidWork() {

            @Override
            public void vrun() {

                Ingredient ingredient = new Ingredient();

                ingredient.setUserKey(username);
                ingredientToBuy.setUserKey(username);

                ingredient.setName(ingredientToBuy.getName());

                ofy().delete().entity(ingredientToBuy).now();
                ofy().save().entity(ingredient);
            }
        });
    }
    /*
    INGREDIENT TO BUY RELATED METHODS
     */

    /**
     * Add an Item to buy into the database
     *
     * @param ingredientToBuyList
     * @param username
     * @throws ConflictException
     */
    @ApiMethod(name = "addIngredientToBuy")
    public void addIngredientToBuy(@Named("username") final String username, final IngredientToBuyList ingredientToBuyList) {

        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {

                for (int i = 0; i < ingredientToBuyList.ingredientToBuyList.size(); i++) {
                    IngredientToBuy ingredientToBuy = new IngredientToBuy();
                    ingredientToBuy.setUserKey(username);
                    ingredientToBuy.setName(ingredientToBuyList.ingredientToBuyList.get(i).getName());
                    ofy().save().entity(ingredientToBuy);
                }
            }
        });
    }

    /**
     * Retrieve the list of ingredient the user has registered
     *
     * @param username
     * @param cursorString
     * @param count
     * @return
     */
    @ApiMethod(name = "listIngredientToBuy", httpMethod = HttpMethod.GET)
    public CollectionResponse<IngredientToBuy> listIngredientToBuy(@Named("username") String username, @Nullable @Named("cursor") String cursorString,
                                                                   @Nullable @Named("count") Integer count) {
        Key<User> userKey = Key.create(User.class, username);
        Query<IngredientToBuy> query = ofy().load().type(IngredientToBuy.class).ancestor(userKey);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<IngredientToBuy> ingredientToBuyList = new ArrayList<>();
        QueryResultIterator<IngredientToBuy> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            ingredientToBuyList.add(iterator.next());
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
        return CollectionResponse.<IngredientToBuy>builder().setItems(ingredientToBuyList).setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "generateBuyList", httpMethod = HttpMethod.POST)
    public void generateBuyList(Recipe recipe, @Named("username") String username) {
        List<IngredientToBuy> ingredientToBuys = new ArrayList<>();

    }
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
            List<Ingredient> strings = recipe.getIngredientList();
            for (int i = 0; i < recipe.getIngredientList().size(); i++) {
                saveIngredientInDictionary(recipe.getIngredientList().get(i), username);
            }
            ofy().save().entity(recipe).now();
        }
    }

    /**
     * Return a Recipe from a user.
     *
     * @param recipeName The name of the recipe
     * @param username   The username from the user who created te recipe
     * @return Recipe
     * @throws NotFoundException
     */
    @ApiMethod(name = "getRecipe", httpMethod = HttpMethod.GET)
    public Recipe getRecipe(@Named("recipeName") String recipeName, @Named("username") String username) throws NotFoundException {
        Key<User> userKey = Key.create(User.class, username);
        Key<Recipe> recipeKey = Key.create(userKey, Recipe.class, recipeName);
        return ofy().load().type(Recipe.class).filterKey("=", recipeKey).first().now();

    }

    /**
     * Update the recipe in the database and returns it.
     *
     * @param recipe   The Recipe to update
     * @param username The username of user who created te recipe
     * @return Updated Recipe
     * @throws NotFoundException
     */
    //UPDATE RECIPE
    @ApiMethod(name = "updateRecipe", httpMethod = HttpMethod.PUT)
    public Recipe updateRecipe(Recipe recipe, @Named("userKey") String username) throws NotFoundException {
        recipe.setUserKey(username);
        if (findRecipe(recipe) != null) {
            ofy().save().entity(recipe).now();
            return recipe;
        }
        throw new NotFoundException("Recipe not found");
    }

    /**
     * Delete a recipe from the datatbase
     *
     * @param recipe   The recipe to delete
     * @param username The username of user who created te recipe
     * @throws NotFoundException
     */
    @ApiMethod(name = "deleteRecipe", httpMethod = HttpMethod.DELETE)
    public void deleteRecipe(Recipe recipe, @Named("userKey") String username) throws NotFoundException {
        if (findRecipe(recipe) != null) {

            ofy().delete().type(Recipe.class).id(null).now();
        }
        throw new NotFoundException("Recipe not found");
    }

    /**
     * Retrieves all of the recipes created by a user
     *
     * @param username     The username of the user who created the recipe
     * @param cursorString
     * @param count        An optional Integer used to limit the number of recipes returned
     * @return A CollectionResponse of recipes, usable as a List
     */
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


    /**
     * Private method used by the endpoint to check whether or not the user already created a recipe of the same name.
     *
     * @param recipe the recipe to check
     * @return The recipe passed as a parameter if it already exist in the database
     */
    private Recipe findRecipe(Recipe recipe) {
        Key<Recipe> recipeKey = Key.create(recipe);
        return ofy().load().type(Recipe.class).filterKey("=", recipeKey).first().now();
    }

    /*
        INGREDIENT RELATED METHODS
    */

    /**
     * Insert an ingredient into the database.
     *
     * @param ingredient The ingredient object to add to the database
     * @param username   The username of the user who owns the ingredient
     * @throws ConflictException In case the ingredient already exist
     */
    @ApiMethod(name = "insertIngredient", httpMethod = HttpMethod.PUT)
    public void insertIngredient(Ingredient ingredient, @Named("username") String username) throws ConflictException {
        ingredient.setUserKey(username);
        saveIngredientInDictionary(ingredient, username);
        if (ingredient.getName() != null) {
            if (findIngredient(ingredient) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        ofy().save().entity(ingredient).now();
    }

    @ApiMethod(name = "deleteIngredient", httpMethod = HttpMethod.DELETE)
    public void deleteIngredient(@Named("username") String username, @Named("ingredientName") String ingredient) throws NotFoundException {
        Key<User> userKey = Key.create(User.class, username);
        Key<Ingredient> ingredientKey = Key.create(userKey, Ingredient.class, ingredient);
        Ingredient ingredientToDelete = ofy().load().type(Ingredient.class).filterKey("=", ingredientKey).first().now();
        ofy().delete().entity(ingredientToDelete).now();
    }

    /**
     * Save the name of a recipe into the user's dictionary so that he can type its name faster in an edit text.
     * Private method of the endpoint
     *
     * @param ingredient The ingredient's name to save
     * @param username   The username of the user
     */
    private void saveIngredientInDictionary(Ingredient ingredient, @Named("username") String username) {
        Key<User> userKey = Key.create(User.class, username);
        User user = ofy().load().type(User.class).filterKey("=", userKey).first().now();
        List<String> dictionary = user.getDictionary();
        if (!dictionary.contains(ingredient.getName())) {
            user.addToDictionary(ingredient.getName());
        }
        ofy().save().entity(user).now();
    }

    /**
     * Gets an ingredient with its name
     *
     * @param name Name of the ingredient to return
     * @return Ingredient
     * @throws NotFoundException In case the ingredient doesn't exist
     */
    @ApiMethod(name = "getIngredientByName", httpMethod = HttpMethod.GET)
    public Ingredient ingredientByName(@Named("name") String name) throws NotFoundException {
        if (name != null) {
            return ofy().load().type(Ingredient.class).filter("name", name).first().now();
        } else {
            throw new NotFoundException("Ingredient Not Found");
        }
    }

    /**
     * List all of the ingredient from a user
     *
     * @param username
     * @param cursorString
     * @param count        An integer used to limit the number of ingredient returned
     * @return A CollectionResponse of ingredient which can be used as a List
     */
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

    /**
     * Create a new User
     *
     * @param user A User object
     * @return the just created user
     * @throws ConflictException In case the user already exist
     */
    @ApiMethod(name = "createUser", httpMethod = HttpMethod.POST)
    public User createUser(User user) throws ConflictException {
        if (findUser(user.getUsername()) == null) {

            ofy().save().entity(user).now();
            return user;
        }
        throw new ConflictException("Username not available");
    }

    /**
     * Update a User
     *
     * @param user the user to update
     * @return the updated user
     * @throws NotFoundException In case the user doesn't exist
     */
    @ApiMethod(name = "updateUser", httpMethod = HttpMethod.PUT)
    public User updateUser(User user) throws NotFoundException {
        if (findUser(user.getUsername()) != null) {
            ofy().save().entity(user).now();
            return user;
        }
        throw new NotFoundException("User not found");
    }

    /**
     * Delete a user
     *
     * @param username the username of the user to delete
     * @throws NotFoundException In case the user doesn't exist
     */
    @ApiMethod(name = "deleteUser", httpMethod = HttpMethod.DELETE)
    public void deleteUser(@Named("userId") String username) throws NotFoundException {
        if (findUser(username) != null) {
            ofy().delete().type(User.class).id(username).now();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    /**
     * Retrieve a User object with a username and a password, serves as a login method
     *
     * @param username
     * @param password
     * @return the corresponding user
     * @throws NotFoundException In case there's no match for the username/password
     */
    @ApiMethod(name = "retrieveUserId", httpMethod = HttpMethod.GET)
    public User retrieveUserId(@Named("username") String username, @Named("password") String password) throws NotFoundException {
        User user = loginUser(username, password);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("Incorrect username or password");
    }

    /**
     * Retrieve a User object using using its username, serves as a way to retrieve a user from the cloud after the client as been killed
     *
     * @param username The username of the user
     * @return a user Object
     * @throws NotFoundException In case the user doesn't exist
     */
    @ApiMethod(name = "retrieveUserById", httpMethod = HttpMethod.GET)
    public User retrieveUserById(@Named("userId") String username) throws NotFoundException {
        User user = findUser(username);
        if (user != null) {
            return user;
        }
        throw new NotFoundException("User not found");
    }

    /**
     * Private method used by the endpoint to check if a user uses the username/password couple
     *
     * @param username
     * @param password
     * @return A User
     */
    private User loginUser(String username, String password) {
        Key<User> userKey = Key.create(User.class, username);
        return ofy().load().type(User.class).filterKey("=", userKey).filter("password", password).first().now();
    }

    /**
     * Private method used by the endpoint to check whether or not he username has already been taken in the database
     *
     * @param username
     * @return Returns a user object in case the username has already been taken
     */
    private User findUser(String username) {
        return ofy().load().type(User.class).id(username).now();
    }


}

