package com.jsg.officials;

import com.googlecode.objectify.ObjectifyService;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * created 4/4/16
 * Query Servlet Handling
 * Stores the information and rating in the google app datastore.
 */
public class QueryServlet extends HttpServlet {

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        StringBuffer data = new StringBuffer();
        try {
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsBodyData = new JSONObject();
        String question;
        try {
            jsBodyData = new JSONObject(data.toString());
            question = jsBodyData.getString("question");
        } catch (Exception e) {
            e.printStackTrace();

            try {
                resp.sendError(resp.SC_BAD_REQUEST, "Incorrect JSON format in data.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return;
        }

        String answerText = "";

        String userId = "osu_student27";
        String password = "wUKaIh05";
        try {
            URL watsonURL = new URL("https://watson-wdc01.ihost.com/instance/501/deepqa/v1/question");
            HttpURLConnection conn = (HttpURLConnection) watsonURL.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-SyncTimeout", "30");
            String auth = new String(Base64.encodeBase64((userId + ":" + password).getBytes()));
            conn.setRequestProperty("Authorization", "Basic " + auth);
            conn.setDoOutput(true);
            conn.getOutputStream().write(("{\"question\" : {\"questionText\":\"" + question + "\"}}").getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                answerText = answerText.concat(inputLine);
            }
            in.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject watsonAnswerResponse = new JSONObject();
        try {
            watsonAnswerResponse = new JSONObject(answerText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Process and format answers
        JSONObject responseData = new JSONObject();
        JSONArray responseAnswers = new JSONArray();
        try {
            JSONArray rawAnswers = watsonAnswerResponse.getJSONObject("question").getJSONArray("evidencelist");

            int count = 0, i = 0;
            while (count < 3 && i < rawAnswers.length()) {
                JSONObject rawAnswer = rawAnswers.getJSONObject(i);
                JSONObject metadataMap = new JSONObject();
                Boolean isOfficialsDoc = false;
                if(rawAnswer.has("metadataMap")) {
                    metadataMap = rawAnswer.getJSONObject("metadataMap");
                    isOfficialsDoc = metadataMap.optString("originalfile").contains("TheOfficials");
                }
                if (rawAnswer.has("text") &&  isOfficialsDoc) {
                    count++;
                    responseAnswers.put(formatAnswer(rawAnswer, question));
                }
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //related&suggested queries

        // Congregate response data into responseData
        try {
            if (responseAnswers.length() > 0) {
                responseData.put("answers", responseAnswers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                resp.sendError(resp.SC_BAD_REQUEST, "Incorrect JSON format in data.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        // Write response body data
        try {
            resp.setContentType("text/x-json;charset=UTF-8");
            responseData.write(resp.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                resp.sendError(resp.SC_BAD_REQUEST, "Incorrect JSON format in data.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        Answer[] answers = new Answer[responseAnswers.length()];
        for(int i = 0; i < responseAnswers.length(); i++) {
            JSONObject responseAnswer = responseAnswers.optJSONObject(i);
            if(responseAnswer != null) {
                Answer answer = new Answer(responseAnswer.optString("text"), responseAnswer.optString("focus"), responseAnswer.optString("source"));
                ObjectifyService.ofy().save().entity(answer).now();
                answers[i] = answer;
            }

        }
        Query query = new Query(question, "");

        // Use Objectify to save the greeting and now() is used to make the call synchronously as we
        // will immediately get a new page using redirect and we want the data to be present.
        ObjectifyService.ofy().save().entity(query).now();
    }

    private JSONObject formatAnswer(JSONObject rawAnswer, String question) throws JSONException {
        JSONObject formattedAnswer = new JSONObject();

        String rawText = rawAnswer.optString("text");
        String rawTitle = rawAnswer.optString("title");
        formattedAnswer.put("text", rawText);
        formattedAnswer.put("title", rawTitle);
        if (formattedAnswer.optString("value").length() > 3) {
            formattedAnswer.put("conf", rawAnswer.optString("value").substring(0, 4));
        } else {
            formattedAnswer.put("conf", rawAnswer.optString("value"));
        }

        String focus = "None";
        String[] sentences = rawText.split(Pattern.quote(". "));
        int sentScore = 0, maxScore = -1;
        for (String sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            sentScore = 0;

            for (String token : sentence.split("\\s+")) {

                if (question.contains(token) & token.length() > 3) {
                    sentScore++;
                }

                if (sentScore > maxScore) {
                    focus = sentence;
                    maxScore = sentScore;
                }
            }
        }
        formattedAnswer.put("focus", focus);

        return formattedAnswer;
    }
}
