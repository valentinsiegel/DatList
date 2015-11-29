package fr.siegel.datlist.services;

import fr.siegel.datlist.model.User;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface UserEndpoint {

    @GET("retrieveUserId/{username}/{password}")
    Call<User> loginUser(@Path("username") String username, @Path("password") String password);

    @POST("createUser")
    Call<User> createUser(@Body User user);
}
