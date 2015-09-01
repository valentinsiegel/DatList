package fr.siegel.datlist.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * Created by Val on 01/09/15.
 */
@Api(
        name = "datListEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.datlist.siegel.fr",
                ownerName = "backend.datlist.siegel.fr",
                packagePath = ""


        )
)
public class UserEndpoint {
}
