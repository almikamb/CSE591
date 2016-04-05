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

public class Main {

	public static int fileid = 0;
	public final static int max_file_size = 10000000; // 10MB
	public static int numTweet = 0;
	public static int arg1 = 0;

	public static void main(final String[] args) throws TwitterException {
		final List<URLThread> tList = new ArrayList<URLThread>();

		// arg1 = 50000;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("yJNBMPjrLS7shZZWiLUzYQ");
		cb.setOAuthConsumerSecret("hqBTCRM1zD8lOo63GO6p7IWEX8R8n37b5GuJ9utXtY");
		cb.setOAuthAccessToken("2177078886-Xu7pRSYjY9upnePOSr9vLr2EBTup3txPx79NgnX");
		cb.setOAuthAccessTokenSecret("bH7QavkQbDGeuDg6tRMdBD8l6ekgks5YZa263EZxaBBu0");
		cb.setJSONStoreEnabled(true);
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			public void onStatus(Status status) {
				constructTweet(status);
			}

			public void constructTweet(Status status) {
				// make sure that is not Retweet and the tweet in English
				// !status.isRetweet() &&
				// status.getLang().equals("en")
				// if (status.getGeoLocation() != null) {
				// if (status.isRetweet()) {
				// if (status.getUserMentionEntities().length > 0) {
				numTweet++;
				JSONObject objTweet = new JSONObject();
				try {

					// objTweet.put("RetweetedStatusChainInfo",
					// status.getRetweetedStatus());

					// user profile info:
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

				File dir = new File("./Data");
				if (!dir.exists()) {
					dir.mkdir();
				}
				// check current file size
				File F = new File("./Data/" + "Tweets" + fileid + ".json");
				// loop until file not full
				while (F.length() > max_file_size) {
					fileid++;
					F = new File("./Data/" + "Tweets" + fileid + ".json");
				}
				// get URLs from the text
				URLEntity urls[] = status.getURLEntities();
				// create a thread and get the title of the URLs
				URLThread t = null;
				/*
				 * if (urls.length != 0) { t = new URLThread("ThURL1", urls,
				 * objTweet, F); tList.add(t); t.start(); } else // the Tweet
				 * JSON has no URLs, save into the file {
				 */
				saveTweets(objTweet.toString(), F);
				// }

				// if it is a retweet, go get the retweeted statues and store it
				if (status.isRetweet()) {
					constructTweet(status.getRetweetedStatus());
				}

				// if it is a quoted tweet, go get the quoted statues and store
				// it
				if (status.getQuotedStatus() != null) {
					constructTweet(status.getQuotedStatus());
				}

				// check if # tweets reach specified argument 1
				/*
				 * if (arg1 == numTweet) { // loop and wait for all threads for
				 * (URLThread thread : tList) { // only wait if thread is still
				 * running if (thread.isAlive()) thread.join(); }
				 * System.out.println("It's been collected "+numTweet+ " Tweets"
				 * ); System.exit(0); }
				 */
				// }
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

		double[][] boundingBox = { { -180, -90 }, { 180, 90 } }; /// whole
																	/// world;
		FilterQuery filter = new FilterQuery();
		filter.locations(boundingBox);

		// twitterStream.addListener(listener);
		// twitterStream.filter(filter);
		FilterQuery fq = new FilterQuery();
		String keywords[] = { "" };

		fq.track(keywords);
		twitterStream.addListener(listener);
		twitterStream.sample();
	}
}

class URLThread implements Runnable {

	public Thread t;
	public String threadName;
	public URLEntity arrURL[];
	public JSONObject tweet;
	public File file;
	final String userAgent = "Mozilla/5.0 (X11; U; Linux x86_64; it-it) AppleWebKit/534.26+ (KHTML, like Gecko) Ubuntu/11.04 Epiphany/2.30.6";

	URLThread(String name, URLEntity url[], JSONObject tweet, File f) {
		threadName = name;
		arrURL = url;
		this.tweet = tweet;
		file = f;
	}

	public void run() {
		// Create JSON URL,Title array
		JSONArray urlList = new JSONArray();

		for (int i = 0; i < arrURL.length; i++) {
			String title = crawl(arrURL[i].getExpandedURL());
			if (title == "") {
				title = crawl(arrURL[i].getExpandedURL()); // give it another
															// shot
			}
			try {
				JSONObject objURL = new JSONObject();
				objURL.put("URL", arrURL[i].getExpandedURL());
				objURL.put("Title", title);
				urlList.put(objURL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// Add the URL,Title to the Tweet JSON object
			tweet.put("URLs", urlList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Save the Tweet JSON into the file
		saveTweets(tweet.toString());
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	// implement thread join
	// this function will wait until all threads are finish
	public void join() {
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isAlive() {
		return t.isAlive();
	}

	public void saveTweets(String tweet) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(file, true); // true tells to
																// append data.
			out = new BufferedWriter(fstream);
			System.out.println(tweet);
			out.write(tweet + "\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public String crawl(String url) {
		String title = "";
		Connection con = Jsoup.connect(url).userAgent(userAgent).referrer("http://www.google.com").followRedirects(true)
				.ignoreContentType(true).ignoreHttpErrors(true).timeout(10000); // 30
																				// seconds
		try {
			Connection.Response resp = con.execute();
			Document doc = null;
			if (resp.statusCode() == 200) {
				doc = con.get();
				title = doc.title();
				return title;
			} else {
				return title;
			}
		} catch (SocketTimeoutException e) {
			System.out.println("The title couldn't be retrieved from the URL: " + url);
			return title;
		} catch (IOException e) {
			System.out.println("The title couldn't be retrieved from the URL: " + url);
			return title;
		}
	}
}
