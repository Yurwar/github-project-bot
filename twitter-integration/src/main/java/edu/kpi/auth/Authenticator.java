package edu.kpi.auth;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Authenticator {
    public void sendUser(HttpRequest request) throws TwitterException {
// Creates the main object
        Twitter twitter = new TwitterFactory().getInstance();
// Ask for a request token
        RequestToken requestToken = twitter.getOAuthRequestToken();
// Store the token in session
        request.getSession().setAttribute("rt", requestToken);
// Extract the authentication URL
        String authUrl = requestToken.getAuthenticationURL();
// Send the Twitter authentication page to the page to create a popup from there
        request.setAttribute("auth", authUrl);
    }

    public void receivePin(HttpRequest request) throws TwitterException {
// Read the PIN
        String pin = request.getParameter("pin");
// Retrieve the request token
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("rt");
// Creates the main object
        Twitter twitter = new TwitterFactory().getInstance();
// Ask for an access token
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pin);
// Store the token in session
        request.getSession().setAttribute("at", accessToken);
// Remove the access token from session
        request.getSession().removeAttribute("rt");
// Set the access token on the twitter instance
        twitter.setOAuthAccessToken(accessToken);
// Now, we can ask for whatever we want!
        ResponseList statuses = twitter.getUserTimeline();
    }
}
