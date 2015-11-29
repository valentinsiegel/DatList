package fr.siegel.datlist.services;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class EndpointFactory {

    private static final String BASE_URL = "https://datlist-1035.appspot.com/_ah/api/datListApi/v1/";
    private UserEndpoint userEndpoint;

    public EndpointFactory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        userEndpoint = retrofit.create(UserEndpoint.class);

    }

    public UserEndpoint getUserEndpoint() {
        return userEndpoint;
    }

}