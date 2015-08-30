package fr.siegel.datlist.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The object model for the data we are sending through endpoints
 */
@Entity
public class Ingredient {

    @Id
    private Long id;

    @Index
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}