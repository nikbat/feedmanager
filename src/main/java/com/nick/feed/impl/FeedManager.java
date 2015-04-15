package com.nick.feed.impl;

import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;

import com.nick.FeedDB;
import com.nick.MyTwitter;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class FeedManager {
	
	protected Logger log = Logger.getLogger(this.getClass());
	
	private MyTwitter myTwitter;
	private FeedFetcherCache feedInfoCache;
	private FeedDB tweetDB;	
	private String feedURLs;
	
	public FeedManager(MyTwitter myTwitter,FeedFetcherCache feedInfoCache,FeedDB tweetDB, String feedURLs){
		this.myTwitter = myTwitter; 
		this.feedInfoCache = feedInfoCache;
		this.tweetDB = tweetDB;
		this.feedURLs = feedURLs;
	}

	@Scheduled(fixedDelay =1000*60*60*1)
	public void doWork() throws Exception {
		log.debug("Feeds"+feedURLs);		
		
		String[] feeds = feedURLs.split(",");
		
		for(String f : feeds){
			URL feedUrl = new URL(f);
			FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
			FetcherEventListenerImpl listener = new FetcherEventListenerImpl(myTwitter,tweetDB);
			fetcher.addFetcherEventListener(listener);

			//System.err.println("Retrieving feed " + feedUrl);
			// Retrieve the feed.
			// We will get a Feed Polled Event and then a
			// Feed Retrieved event (assuming the feed is valid)
			SyndFeed feed = fetcher.retrieveFeed(feedUrl);

			//System.err.println(feedUrl + " retrieved");
			//System.err.println(feedUrl + " has a title: " + feed.getTitle() + " and contains " + feed.getEntries().size() + " entries.");
			// We will now retrieve the feed again. If the feed is unmodified
			// and the server supports conditional gets, we will get a "Feed
			// Unchanged" event after the Feed Polled event
			//System.err.println("Polling " + feedUrl + " again to test conditional get support.");
			//SyndFeed feed2 = fetcher.retrieveFeed(feedUrl);
			//System.err.println("If a \"Feed Unchanged\" event fired then the server supports conditional gets.");

			//ok = true;
		}
	}
	
	public void destroy() {
		log.debug("Distroy Feedmanager");
		log.debug("Distroy Feedmanager");
	}
	
	class FetcherEventListenerImpl implements FetcherListener {
		/**
		 * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
		 */
		
		private MyTwitter myTwitter;
		private FeedDB tweetDB;
		
		public FetcherEventListenerImpl(MyTwitter myTwitter,FeedDB tweetDB){
			this.myTwitter = myTwitter;
			this.tweetDB = tweetDB;
		}
		public void fetcherEvent(FetcherEvent event) {
			String eventType = event.getEventType();
			
			
			if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
				log.debug("\tEVENT: Feed Polled. URL = " + event.getUrlString());
			} else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {				
				SyndFeed feeds = event.getFeed();			
				List entries = feeds.getEntries();				
				
				for(int i=0;i <entries.size();i++){
					
					SyndEntryImpl entry = (SyndEntryImpl)entries.get(i);					
					SyndEntryImpl entryDB = tweetDB.get(entry.getTitle());
					
					if( entry.getUri() == null || entry.getUri().trim().length() < 4){
						continue;
					}
					
					if(entryDB != null && entryDB.getPublishedDate() != null && entry.getPublishedDate() != null && entry.getPublishedDate().equals(entryDB.getPublishedDate())){
						log.debug("ALREADY PUBLISHED");
						continue;
					}
					
					if(entryDB != null && entryDB.getDescription() != null && entry.getDescription() != null && entry.getDescription().equals(entryDB.getDescription())){
						log.debug("ALREADY PUBLISHED");
						continue;
					}
					
					
					log.debug("Title "+entry.getTitle());					
					log.debug("Description "+entry.getDescription().getValue().replaceAll("\\<.*?>",""));					
					log.debug("Uri "+entry.getUri());
					log.debug("Link "+entry.getLink());
					log.debug("Author "+entry.getAuthor());
					log.debug("Date "+entry.getPublishedDate());
					
					
					/*List categoriesList = entry.getCategories();
					if(categoriesList != null){
						System.out.print("Category ");
						for(int k = 0; k < categoriesList.size();k++){
							if(categoriesList.get(k) != null){
								SyndCategoryImpl ci = (SyndCategoryImpl)categoriesList.get(k);
								System.out.print(ci.getName() +",");
							}
						}
					}*/
					
					String imgURL = null;
					
					/*List<SyndEnclosure> encls = entry.getEnclosures();
					  if(!encls.isEmpty()){
					    for(SyndEnclosure e : encls){
					    	imgURL = e.getUrl().toString();
					    	break;
					    }                       
					 }*/
					if(StringUtils.containsIgnoreCase(entry.getUri(), "youngadventuress") ||
							StringUtils.containsIgnoreCase(entry.getUri(), "fourjandals") ||
							StringUtils.containsIgnoreCase(entry.getUri(), "emmastraveltales") ||
							StringUtils.containsIgnoreCase(entry.getUri(), "doubletakes") ||
							StringUtils.containsIgnoreCase(entry.getUri(), "anywhere.com") ||
							StringUtils.containsIgnoreCase(entry.getUri(), "adventure-journal") ||
							
							
							StringUtils.containsIgnoreCase(entry.getLink(), "youngadventuress") ||
							StringUtils.containsIgnoreCase(entry.getLink(), "fourjandals") ||
							StringUtils.containsIgnoreCase(entry.getLink(), "emmastraveltales") ||
							StringUtils.containsIgnoreCase(entry.getLink(), "doubletakes") ||
							StringUtils.containsIgnoreCase(entry.getLink(), "anywhere.com")||
							StringUtils.containsIgnoreCase(entry.getUri(), "adventure-journal") ){
						
						try{
							if(entry.getContents() != null && entry.getContents().size() > 0 && entry.getContents().get(0) != null){
								
								SyndContentImpl description = (SyndContentImpl)entry.getContents().get(0);							
								Document doc = Jsoup.parse(description.getValue());
								Elements media = doc.select("[src]");
								for (Element src : media) {
						            if (src.tagName().equals("img")){
						            	imgURL = src.attr("abs:src");
						            	log.debug(imgURL);
						            	break;
						            }
								}
							}
							
							if(imgURL == null) {
								if(entry.getDescription() != null){
									
									SyndContentImpl description = (SyndContentImpl)entry.getDescription();							
									Document doc = Jsoup.parse(description.getValue());
									Elements media = doc.select("[src]");
									for (Element src : media) {
							            if (src.tagName().equals("img")){
							            	imgURL = src.attr("abs:src");
							            	log.debug(imgURL);
							            	break;
							            }
									}
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					 
					try{
						String title = "";
						int linkLength = entry.getLink().length();
						int titleLength = 0;
						
						if(linkLength > 140){
							continue; // add code to generate mini link
						}else if(linkLength < 140){
							if(linkLength + entry.getTitle().length() < 140){
								title = entry.getTitle();
							}else{
								titleLength = 140 - entry.getTitle().length();
								if(entry.getTitle().length() < titleLength){
									title = entry.getTitle();
								}else{
									title = entry.getTitle().substring(0,titleLength-1 );
								}
							}
							
							
						}
						if(imgURL != null && (imgURL.endsWith(".jpg") || imgURL.endsWith(".png"))){
							if(StringUtils.containsIgnoreCase(entry.getLink(), "doubletakes") ){
								if(StringUtils.containsIgnoreCase(entry.getLink(),"Desktop")){
									continue;
								}
								myTwitter.tweet(title + " "+ entry.getLink(), new URL(imgURL));
							}else{
								myTwitter.tweet(title + " "+ entry.getUri(), new URL(imgURL));
							}
							
						}else{
							myTwitter.tweet(entry.getTitle() + " "+ entry.getUri());
						}
						Thread.sleep(1000*2);
						tweetDB.put(entry.getTitle(), entry);
						
					}catch(Exception e){
						e.printStackTrace();						
					}
					
				}
				
			} else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
				log.debug("\tEVENT: Feed Unchanged. URL = " + event.getUrlString());
			}
		}
	}

}
