<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.jsg.officials.Rating" %>
<%@ page import="com.jsg.officials.Query" %>
<%@ page import="com.jsg.officials.Answer" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <%-- <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/> --%>
</head>

<body>

<h1>Ratings</h1>
<%
    List<Rating> ratings = ObjectifyService.ofy().load().type(Rating.class).order("-date").list();
    if(ratings.isEmpty()) {
        %>
            <p>There are no ratings in the production data store currently.</p>
        <%
    } else { %>
    <table>
        <th>
            <td>Date</td>
            <td>Rating</td>
            <td>Question</td>
            <td>Answer</td>
        </th> <%
        for(Rating rating : ratings) {

            %>
                <tr>
                    <td><%= rating.date %></td>
                    <td><%= rating.rating %></td>
                    <td><%= rating.question %></td>
                    <td><%= rating.answer %></td>
                </tr>
            <%
        }
    %></table><%
    }
%>
<hr>
<h1>Queries</h1>
<%
    List<Query> queries = ObjectifyService.ofy().load().type(Query.class).order("-date").list();
    if(queries.isEmpty()) {
        %>
            <p>There are no queries in the production data store currently.</p>
        <%
    } else {
    %>
        <table>
            <th>
                <td>Date</td>
                <td>Question</td>
            </th> <%
        for(Query query : queries) {
           %>
               <tr>
                   <td><%= query.date %></td>
                   <td><%= query.question %></td>
               </tr>
           <%
        }
        %></table><%
    }
%>

</body>
</html>