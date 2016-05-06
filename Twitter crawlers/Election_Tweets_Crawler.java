
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author malmukay
 */

public class Election_Tweets_Crawler {

	public static int fileid = 1000;
	public final static int max_file_size = 10000000; // 10MB
	public static int numTweet = 0;
	public static int arg1 = 0;

	public static void main(final String[] args) throws TwitterException {
		final List<URLThread> tList = new ArrayList<URLThread>();

		// arg1 = 50000;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("");
		cb.setOAuthConsumerSecret("");
		cb.setOAuthAccessToken("");
		cb.setOAuthAccessTokenSecret("");
		cb.setJSONStoreEnabled(true);
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			public void onStatus(Status status) {
				constructTweet(status);
			}

			public void constructTweet(Status status) {
				
				numTweet++;
				JSONObject objTweet = new JSONObject();
				try {

					

						objTweet.put("UserScreenName", status.getUser().getScreenName());
						objTweet.put("UserName", status.getUser().getName());
						objTweet.put("isVerified", status.getUser().isVerified());
						objTweet.put("UserAccountDate", status.getUser().getCreatedAt());
						objTweet.put("UserID", status.getUser().getId());
						objTweet.put("UserDescription", status.getUser().getDescription());
						objTweet.put("UserFavouritesCount", status.getUser().getFavouritesCount());
						objTweet.put("UserFollowersCount", status.getUser().getFollowersCount());
						objTweet.put("UserFriendsCount", status.getUser().getFriendsCount());
						objTweet.put("UserListedCount", status.getUser().getListedCount());
						objTweet.put("UserStatusesCount", status.getUser().getStatusesCount());
						objTweet.put("UserUtcOffset", status.getUser().getUtcOffset());
						objTweet.put("UserIsContributorsEnabled", status.getUser().isContributorsEnabled());
						objTweet.put("UserIsDefaultProfile", status.getUser().isDefaultProfile());
						objTweet.put("UserIsDefaultProfileImage", status.getUser().isDefaultProfileImage());
						objTweet.put("UserIsGeoEnabled", status.getUser().isGeoEnabled());
						objTweet.put("UserIsProfileBackgroundTiled", status.getUser().isProfileBackgroundTiled());
						objTweet.put("UserIsProfileUseBackgroundImage", status.getUser().isProfileUseBackgroundImage());
						objTweet.put("UserIsProtected", status.getUser().isProtected());
						objTweet.put("UserIsShowAllInlineMedia", status.getUser().isShowAllInlineMedia());
						objTweet.put("UserIsTranslator", status.getUser().isTranslator());
						objTweet.put("UserLocation", status.getUser().getLocation());

						// tweet info:
						objTweet.put("TweetID", status.getId());
						objTweet.put("TweetCreatedAt", status.getCreatedAt());
						objTweet.put("Text", status.getText());
						// objTweet.put("TweetCreatedAt",
						// status.getMediaEntities());

						if (status.getGeoLocation() != null) {
							objTweet.put("Latitude", status.getGeoLocation().getLatitude());
							objTweet.put("Longitude", status.getGeoLocation().getLongitude());
						} else {
							objTweet.put("Latitude", -1);
							objTweet.put("Longitude", -1);
						}

						objTweet.put("FavoriteCount", status.getFavoriteCount());
						objTweet.put("Lang", status.getLang());
						objTweet.put("CurrentUserRetweetId", status.getCurrentUserRetweetId());
						objTweet.put("InReplyToScreenName", status.getInReplyToScreenName());
						objTweet.put("InReplyToStatusId", status.getInReplyToStatusId());

						objTweet.put("InReplyToUserId", status.getInReplyToUserId());

						objTweet.put("QuotedStatusId", status.getQuotedStatusId());
						objTweet.put("RetweetCount", status.getRetweetCount());
						objTweet.put("isRetweet", status.isRetweet());
						objTweet.put("isTruncated", status.isTruncated());

						if (status.getPlace() != null) {
							objTweet.put("Place", status.getPlace().getFullName());
						} else {
							objTweet.put("Place", "unknown");
						}

						objTweet.put("Source", status.getSource());
						objTweet.put("isPossiblySensitive", status.isPossiblySensitive());

						if (status.isRetweet()) {
							objTweet.put("RetweetedStatusID", status.getRetweetedStatus().getId());
						} else {
							objTweet.put("RetweetedStatusID", -1);
						}

						JSONArray hashtagList = new JSONArray();
						HashtagEntity hashtags[] = status.getHashtagEntities();

						if (hashtags.length > 0) {
							for (int i = 0; i < hashtags.length; i++) {
								String hashText = hashtags[i].getText();
								JSONObject objHashtag = new JSONObject();
								try {
									objHashtag.put("Text", hashText);
									hashtagList.put(objHashtag);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							objTweet.put("Hashtags", hashtagList);
						} else {
							objTweet.put("Hashtags", hashtagList);
						}

						JSONArray mentionList = new JSONArray();
						UserMentionEntity mentions[] = status.getUserMentionEntities();

						if (mentions.length > 0) {
							for (int i = 0; i < mentions.length; i++) {
								UserMentionEntity mention = mentions[i];
								// System.out.println(mention);
								JSONObject objMention = new JSONObject();
								try {
									objMention.put("ID", mention.getId());
									objMention.put("Name", mention.getName());
									objMention.put("ScreenName", mention.getScreenName());
									mentionList.put(objMention);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							objTweet.put("Mentions", mentionList);
						} else {
							objTweet.put("Mentions", mentionList);
						}

						JSONArray mediaList = new JSONArray();
						MediaEntity[] medias = status.getMediaEntities();

						if (medias.length > 0) {
							for (int i = 0; i < medias.length; i++) {
								MediaEntity media = medias[i];
								// System.out.println(mention);
								JSONObject objMedia = new JSONObject();
								try {
									objMedia.put("ID", media.getId());
									objMedia.put("ExpandedURL", media.getExpandedURL());
									objMedia.put("DisplayURL", media.getDisplayURL());
									mediaList.put(objMedia);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							objTweet.put("Medias", mediaList);
						} else {
							objTweet.put("Medias", mediaList);
						}

					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// create folder if not exist

				File dir = new File("./Elections_Data");
				if (!dir.exists()) {
					dir.mkdir();
				}
				// check current file size
				File F = new File("./Elections_Data/" + "Tweets" + fileid + ".json");
				// loop until file not full
				while (F.length() > max_file_size) {
					fileid++;
					F = new File("./Elections_Data/" + "Tweets" + fileid + ".json");
				}
				// get URLs from the text
				URLEntity urls[] = status.getURLEntities();
				// create a thread and get the title of the URLs
				URLThread t = null;
		
				saveTweets(objTweet.toString(), F);
				

				// if it is a retweet, go get the retweeted statues and store it
				if (status.isRetweet()) {
					constructTweet(status.getRetweetedStatus());
				}

				// if it is a quoted tweet, go get the quoted statues and store
				
				if (status.getQuotedStatus() != null) {
					constructTweet(status.getQuotedStatus());
				}

				
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// System.out.println("Got a status deletion notice id:" +
				// statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// System.out.println("Got track limitation notice:" +
				// numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				// System.out.println("Got scrub_geo event userId:" + userId + "
				// upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
			}

			public void saveTweets(String tweet, File f) {
				BufferedWriter out = null;
				try {
					FileWriter fstream = new FileWriter(f, true); // true tells
																	// to append
																	// data.
					out = new BufferedWriter(fstream);
					System.out.println(tweet);
					out.write(tweet + "\n");
					out.close();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		};

		double[][] boundingBox = { { -125.0011, 24.9493}, { -66.9326, 49.5904  } }; /// The US
		
		twitterStream.addListener(listener);
		FilterQuery fq = new FilterQuery();
		String keywords[] = { "election","vote", "Cruz", "Trump", "Sanders", "Clinton", "Campaigns", "Sen " , "elections", "democratic",
							"republic"};
		fq.track(keywords);
		fq.language("en");
		
		twitterStream.filter(fq);
		
	}
}

