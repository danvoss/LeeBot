package com.dvoss;

import twitter4j.*;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        Twitter twitter = new TwitterFactory().getSingleton();

        /* first test-drive:
        * */

//        Status status = twitter.updateStatus("i tweet robotic-lee.");
//        System.out.println("Tweeted successful-lee.");

        /* run script to search for latest tweets and retweet:
        * */

        while (true) {

            Query query = new Query("lee konitz");

            QueryResult result = twitter.search(query);

            for (int i = 27; i >= 0; i--) {
                Status found = result.getTweets().get(i);
//                twitter.retweetStatus(found.getId());
                System.out.println(found.getText());
                Thread.sleep(60 * 1000);
            }

            Thread.sleep((6 * 60 * 60 * 1000));
        }
    }
}
