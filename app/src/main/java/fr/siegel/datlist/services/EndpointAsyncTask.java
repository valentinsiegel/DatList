package fr.siegel.datlist.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import fr.siegel.datlist.backend.datListApi.DatListApi;

/**
 * Created by Val on 23/08/15.
 */
public class EndpointAsyncTask {

    private static DatListApi mDatListEndpoint = null;

    public static DatListApi getApi() {
        if (mDatListEndpoint == null) { // Only do this once
            DatListApi.Builder builder = new DatListApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    //https://datlist-1035.appspot.com/_ah/api/
                    //http://10.0.3.2:8080/_ah/api/
                    .setRootUrl("https://datlist-1035.appspot.com/_ah/api/")
                            //.setRootUrl("http://169.254.80.80:8080/_ah/api/")
                            // .setRootUrl("http://10.0.3.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            mDatListEndpoint = builder.build();
        }

        return mDatListEndpoint;
    }
}
