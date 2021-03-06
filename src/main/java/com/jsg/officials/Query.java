package com.jsg.officials;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * created on 4/4/16
 * The @Entity tells Objectify about our entity.  We also register it in {@link OfyHelper}
 * Our primary key @Id is set automatically by the Google Datastore for us.
 *
 * We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks.  Objectify, unlike the AppEngine library requires that you specify the fields you
 * want to index using @Index.  Only indexing the fields you need can lead to substantial gains in
 * performance -- though if not indexing your data from the start will require indexing it later.
 *
 * NOTE - all the properties are PUBLIC so that can keep the code simple.
 **/
@Entity
public class Query {
    @Id public Long id;

    public String question;
    public String focus;
    @Index public Date date;

    /**
     * Simple constructor just sets the date
     **/
    public Query() {
        date = new Date();
    }

    /**
     * A convenience constructor
     **/
    public Query(String question, String focus) {
        this();
        this.question = question;
        this.focus = focus;
    }

}
