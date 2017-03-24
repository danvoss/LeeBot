package io.github.danvoss;

import twitter4j.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws TwitterException, InterruptedException {

        Twitter twitter = new TwitterFactory().getSingleton();

        searchAndRetweet(twitter);
        followBack(twitter);
        //unfollowNonfollowers(twitter);

        System.out.println("Finished.");
    }

    private static void searchAndRetweet(Twitter twitter) throws TwitterException, InterruptedException {

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

                // Follow verified accounts:
                if (!r.isSourceFollowingTarget() && u.isVerified()) {
                    try {
                        twitter.createFriendship(u.getId());
                        System.out.println("Now following " + u.getName());
                    }
                    catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(1 * 60 * 1000);
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

    private static void unfollowNonfollowers(Twitter twitter) throws TwitterException {

        if (LocalDateTime.now().getDayOfWeek() == DayOfWeek.FRIDAY) {

            long cursor = -1;
            PagableResponseList<User> listOfFriends;
            List<User> friendsArrayList = new ArrayList<>();
            while (cursor!= 0) {
                listOfFriends = twitter.getFriendsList("lee_konitz_bot", cursor);
                friendsArrayList.addAll(listOfFriends);
                cursor = listOfFriends.getNextCursor();
            }
            // getFriendsList will only return 20 latest elements, so trying to loop over next cursors to get all friends.
            // But the code above still returns a rate limit exception statusCode=49, code=88
            // see https://dev.twitter.com/rest/reference/get/friends/list and https://dev.twitter.com/overview/api/cursoring

            String[] arrayOfFriends = new String[friendsArrayList.size()];

            int count = 0;
            for (User user : friendsArrayList) {
                arrayOfFriends[count] = user.getScreenName();
                count++;
            }

            ResponseList<Friendship> listOfFriendships = twitter.lookupFriendships(arrayOfFriends);

            for (Friendship friendship : listOfFriendships) {
                if (!friendship.isFollowedBy() && !twitter.showUser(friendship.getId()).isVerified()) {
                    try {
                        twitter.destroyFriendship(friendship.getId());
                        System.out.println("Unfollowed " + friendship.getName());
                    }
                    catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Done unfollowing non-followers.");
        }
    }
}
