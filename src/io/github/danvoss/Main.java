package io.github.danvoss;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        Twitter twitter = new TwitterFactory().getSingleton();

        // First test-drive:
//        Status status = twitter.updateStatus("i tweet robotic-lee.");
//        System.out.println("Tweeted successful-lee.");


        // To search for latest tweets and retweet:
        final long LEEBOT_ID = twitter.showUser("lee_konitz_bot").getId();

        Query query = new Query("lee konitz");
        query.setResultType(Query.ResultType.recent);
        query.setCount(12);
        List<Status> resultList = new ArrayList<>();
        QueryResult result = twitter.search(query);
        resultList.addAll(result.getTweets());

        for (int i = (resultList.size() - 1); i >=0; i--) {
            Status status = resultList.get(i);
            User u = status.getUser();
            Relationship r = twitter.showFriendship(LEEBOT_ID, u.getId());
            try {
                twitter.retweetStatus(status.getId());
                System.out.println(status.getText());

                // Follow verified users:
                if (!r.isSourceFollowingTarget() && u.isVerified()) {
                    try {
                        twitter.createFriendship(u.getId());
                        System.out.println("Now following " + u.getName());
                    }
                    catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(2 * 60 * 1000);
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

    private static void followBack(Twitter twitter) throws TwitterException {

        // Replace commented-out code below with the following. Use pagable response list of followers instead of IDs to
        // avoid query limit exception.

        PagableResponseList<User> listOfFollowers = twitter.getFollowersList("lee_konitz_bot", -1);
        String[] arrayOfFollowers = new String[listOfFollowers.size()];

        int count=0;
        for (User user : listOfFollowers) {
            arrayOfFollowers[count] = user.getScreenName();
            count++;
        }
        ResponseList<Friendship> listOfFriendships = twitter.lookupFriendships(arrayOfFollowers);

        for (Friendship friendship : listOfFriendships) {
            if (!friendship.isFollowing()) {
                try {
                    twitter.createFriendship(friendship.getId());
                    System.out.println("Now following back " + friendship.getName());
                }
                catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done following back.");

//        IDs followers = twitter.getFollowersIDs("lee_konitz_bot", -1);
//        long[] followerIDs = followers.getIDs();
//        ResponseList<Friendship> friendshipResponseList = twitter.lookupFriendships(followerIDs);
//        for (Friendship f : friendshipResponseList) {
//            if (!f.isFollowing()) {
//                try {
//                    twitter.createFriendship(f.getId());
//                    System.out.println("Now following back " + f.getName());
//                }
//                catch (TwitterException e){
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}
