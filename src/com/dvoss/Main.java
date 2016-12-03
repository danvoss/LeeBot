package com.dvoss;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        Twitter twitter = new TwitterFactory().getSingleton();

        /* first test-drive:
        * */

//        Status status = twitter.updateStatus("i tweet robotic-lee.");
//        System.out.println("Tweeted successful-lee.");

        /* run script to search for latest tweets and retweet:
        * */

        Query query = new Query("lee konitz");
        query.setResultType(Query.ResultType.recent);
        query.setCount(27);
        List<Status> resultList = new ArrayList<>();
        QueryResult result = twitter.search(query);
        resultList.addAll(result.getTweets());

        for (int i = (resultList.size() - 1); i >=0; i--) {
            Status status = resultList.get(i);
//            twitter.retweetStatus(status.getId());
            System.out.println(status.getText());
            Thread.sleep(60 * 1000);
        }
    }
}
