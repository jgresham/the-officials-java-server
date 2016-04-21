package com.jsg.officials;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.lang.String;
import java.net.URI;
import java.util.Date;
import java.util.List;

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
public class Answer {
    @Id public Long id;

    public String text;
    public String focus;
    public String source;
    @Index public Date date;

    /**
     * Simple constructor just sets the date
     **/
    public Answer() {
        date = new Date();
    }

    /**
     * A convenience constructor
     **/
    public Answer(String text, String focus, String source) {
        this();
        this.text = text;
        this.focus = focus;
        this.source = source;
    }

}
