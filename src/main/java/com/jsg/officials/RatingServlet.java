package com.jsg.officials;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Rating Servlet Handling
 * Stores the information and rating in the google app datastore.
 */
public class RatingServlet extends HttpServlet {

  // Process the http POST of the form
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp){
    StringBuffer data = new StringBuffer();
    try {
      BufferedReader reader = req.getReader();
      String line;
      while((line = reader.readLine()) != null) {
        data.append(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    JSONObject jsBodyData;
    try {
      jsBodyData = new JSONObject(data.toString());
      String question = jsBodyData.getString("question");
      String answer = jsBodyData.getString("answer");
      String ratingStatus = jsBodyData.getString("rating");

      Rating rating = new Rating(question, answer, ratingStatus);

      // Use Objectify to save the greeting and now() is used to make the call synchronously as we
      // will immediately get a new page using redirect and we want the data to be present.
      ObjectifyService.ofy().save().entity(rating).now();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
