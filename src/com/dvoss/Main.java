package com.dvoss;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Main {

    public static void main(String[] args) throws TwitterException {

        Twitter twitter = new TwitterFactory().getSingleton();
        Status status = twitter.updateStatus("i tweet robotic-lee.");
        System.out.println("Tweeted successful-lee.");
    }
}
