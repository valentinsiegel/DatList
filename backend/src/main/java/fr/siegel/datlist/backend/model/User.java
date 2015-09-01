package fr.siegel.datlist.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Val on 01/09/15.
 */
@Entity
public class User {

    @Id
    private long id;
    private String username;
}
