package com.nick;

import java.net.URL;

import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

public class LocalDBFeedInfoCache implements FeedFetcherCache {
	
	
	private FeedDB db;
	
	public LocalDBFeedInfoCache (FeedDB db){
		this.db = db;
	}
	
	
	@Override
	public SyndFeedInfo getFeedInfo(URL feedUrl) {
		if(feedUrl == null)
			return null;
		
		if(feedUrl.toString().contains("doubletakes"))
			return null;
		
		return db.get(feedUrl);
	}

	@Override
	public void setFeedInfo(URL feedUrl, SyndFeedInfo syndFeedInfo) {
		db.put(feedUrl, syndFeedInfo);		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SyndFeedInfo remove(URL feedUrl) {
		if(feedUrl == null)
			return null;
		
		return db.delete(feedUrl);
	}
	
	public void close(){
		
	}

}
