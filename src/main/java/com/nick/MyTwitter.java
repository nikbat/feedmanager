package com.nick;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MyTwitter {

	protected Logger log = Logger.getLogger(this.getClass());
	
	public static void main(String[] args) throws Exception {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("e1vOilXSffRP4cbWPg1YktJjs")
		  .setOAuthConsumerSecret("3wLrNfLNpkXTm9NRCy0YYsONAfXpn1QUsOZM3CSue9FfLzCJRV")
		  .setOAuthAccessToken("44928293-prOIZBluYfFp2OE121zh4C2Jo4qD2Z47wJxWHs0u0")
		  .setOAuthAccessTokenSecret("YpV3KhaSu9X27s3nPpO6f9R0j1NmJAgOXtLX7giMg2T3U");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		Status status = twitter.updateStatus("Test Tweet");
	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
		System.out.println("Tweet"); 

	}
	
	ConfigurationBuilder cb = new ConfigurationBuilder();
	TwitterFactory tf;
	Twitter twitter;
	
	public MyTwitter(){
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("e1vOilXSffRP4cbWPg1YktJjs")
		  .setOAuthConsumerSecret("3wLrNfLNpkXTm9NRCy0YYsONAfXpn1QUsOZM3CSue9FfLzCJRV")
		  .setOAuthAccessToken("44928293-prOIZBluYfFp2OE121zh4C2Jo4qD2Z47wJxWHs0u0")
		  .setOAuthAccessTokenSecret("YpV3KhaSu9X27s3nPpO6f9R0j1NmJAgOXtLX7giMg2T3U");
		tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
	public void tweet(String tweet) throws Exception{
		Status status = twitter.updateStatus(tweet);
	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
		System.out.println("Tweet");
	}
	
	public void tweet(String tweet, File file) throws Exception {
		StatusUpdate status = new StatusUpdate(tweet);
		InputStream fs = new FileInputStream(file);
		status.setMedia(tweet, fs);
		twitter.updateStatus(status);
	}
	
	public void tweet(String tweet, URL url) throws Exception {		
		StatusUpdate status = new StatusUpdate(tweet);
		InputStream fs = new BufferedInputStream(url.openStream());
		status.setMedia(tweet, fs);
		twitter.updateStatus(status);
	}
	
	public void destroy() {
	      System.out.println("Distroy MyTwitter");
	      log.debug("Distroy MyTwitter");
	}
	
	

}
