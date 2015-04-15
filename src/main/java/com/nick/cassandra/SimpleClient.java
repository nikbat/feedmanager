package com.nick.cassandra;


import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class SimpleClient {

	public Cluster cluster;
	private Session session;
	private PreparedStatement ps;
	private BatchStatement batch;
	public int count = 0;
	public long totalInserts;
	public long previousInserts;

	public Session getSession() {
		return this.session;
	}

	public void close() {
		session.close();
		cluster.close();
	}

	public static void main(String[] args) {
		SimpleClient client = new SimpleClient();
		
		 client.connect("192.168.1.9"); 
		 client.setSession();
		 client.prepaeBatch(); 
		 client.addBatch("1112", "Name2", "Name2");
		 client.addBatch("1113", "Name3", "Name3"); 
		 client.addBatch("1114", "Name4", "Name4"); 
		 client.addBatch("1115", "Name5", "Name5");
		 client.loadData(); 
		 client.queryData(); client.close();
		 
		

	}

	public long startTime = 0;

	public void init() {
		startTime = System.currentTimeMillis();		
		connect("192.168.1.9"); 
		setSession();
		prepaeBatch();
		
	}

	public void print() {
		
		long elapsed = (System.currentTimeMillis() - startTime) / 1000;
		if(elapsed == 0){
			elapsed = 1;
		}
		
		if(previousInserts == totalInserts){
			System.out.println("Same");
			return;
		}
		
		System.out.println("w:"+totalInserts+" elapsed:"+ elapsed + " w/s " +totalInserts/elapsed);
		previousInserts = totalInserts;
		
	}

	public void setSession() {
		session = cluster.connect();
	}

	public void loadData() {
		session.execute("INSERT INTO mykeyspace.users (user_id,  fname, lname) "
				+ "VALUES ('1111', 'Nipun', 'Batra')");
	}

	public void prepaeBatch() {
		ps = session
				.prepare("INSERT INTO mykeyspace.users (user_id,  fname, lname) VALUES (?, ?, ?)");
		batch = new BatchStatement();
	}

	public void addBatch(String id, String fname, String lname) {
		batch.add(ps.bind(id, fname, lname));
		count++;
		if (count % 5000 == 0) {
			//System.out.println("BB:"+batch.getFetchSize());
			session.execute(batch);
			batch.clear();
			//System.out.println("BA:"+batch.getFetchSize());
			count = 0;
		}
	}

	public void queryData() {

		ResultSet results = session.execute("SELECT * FROM mykeyspace.users;");
		for (Row row : results) {
			System.out.println(String.format("%-30s\t%-20s\t%-20s",
					row.getString("user_id"), row.getString("fname"),
					row.getString("lname")));
		}
	}

	public void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).build();
		// cluster.getConfiguration().getSocketOptions().setReadTimeoutMillis(10000);

		cluster.getConfiguration().getProtocolOptions()
				.setCompression(ProtocolOptions.Compression.LZ4);

		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}

	}

	

}
