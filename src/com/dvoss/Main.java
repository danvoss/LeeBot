package com.dvoss;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void followBack(Twitter twitter) throws TwitterException {

        IDs followers = twitter.getFollowersIDs("lee_konitz_bot", -1);
        long[] followerIDs = followers.getIDs();
        ResponseList<Friendship> friendshipResponseList = twitter.lookupFriendships(followerIDs);
        for (Friendship f : friendshipResponseList) {
            if (!f.isFollowing()) {
                try {
                    twitter.createFriendship(f.getId());
                    System.out.println("Now following back " + f.getName());
                }
                catch (TwitterException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws TwitterException, InterruptedException {

        Twitter twitter = new TwitterFactory().getSingleton();

        /*
         * first test-drive:
         */

//        Status status = twitter.updateStatus("i tweet robotic-lee.");
//        System.out.println("Tweeted successful-lee.");

        /*
         * to search for latest tweets and retweet:
         */

        Query query = new Query("lee konitz");
        query.setResultType(Query.ResultType.recent);
        query.setCount(27);
        List<Status> resultList = new ArrayList<>();
        QueryResult result = twitter.search(query);
        resultList.addAll(result.getTweets());

        for (int i = (resultList.size() - 1); i >=0; i--) {
            Status status = resultList.get(i);
            try {
                twitter.retweetStatus(status.getId());
                System.out.println(status.getText());
                Thread.sleep(60 * 1000);
            }
            catch (TwitterException e) {
                if (e.getStatusCode() == 403) {
                    System.out.println("You've already retweeted this status: " + status.getId());
                }
                else {
                    e.printStackTrace();
                }
            }
        }

        followBack(twitter);

        System.out.println("Finished.");
    }
}
