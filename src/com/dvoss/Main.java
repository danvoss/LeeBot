package com.dvoss;

import twitter4j.*;

public class Main {

    public static void main(String[] args) throws TwitterException {

        Twitter twitter = new TwitterFactory().getSingleton();

//        Status status = twitter.updateStatus("i tweet robotic-lee.");
//        System.out.println("Tweeted successful-lee.");

        Query query = new Query("lee konitz");

        QueryResult result = twitter.search(query);

        Status found = result.getTweets().get(0);

        System.out.println(found.getText());

        /* for retweet, grab id
         * problem: avoid retweeting the same status more than once
         */
    }
}
