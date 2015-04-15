package com.nick;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.SerializationUtils;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;

public class FeedDB {
	
	private Environment myEnv;

    // The databases that our application uses
    private Database feedDB;
    private Database classCatalogDb;
    
 // Needed for object serialization
    private StoredClassCatalog classCatalog;
    
    String envHomeStr;
    File envHome; 
    boolean readOnly;
    
    // Our constructor does nothing
    public FeedDB(String envHomeStr, boolean readOnly) {
    	this.envHomeStr = envHomeStr;
    	this.readOnly = readOnly;
    	this.envHome = new File(envHomeStr);
    	setup(envHome, readOnly);
    	
    }
    
    public void setup(File envHome, boolean readOnly)
            throws DatabaseException {
    	
    	EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        DatabaseConfig myDbConfig = new DatabaseConfig();
        SecondaryConfig mySecConfig = new SecondaryConfig();
        
        // If the environment is read-only, then
        // make the databases read-only too.
        myEnvConfig.setReadOnly(readOnly);
        myDbConfig.setReadOnly(readOnly);
        mySecConfig.setReadOnly(readOnly);

        
        // If the environment is opened for write, then we want to be
        // able to create the environment and databases if
        // they do not exist.
        myEnvConfig.setAllowCreate(!readOnly);
        myDbConfig.setAllowCreate(!readOnly);
        mySecConfig.setAllowCreate(!readOnly);
        
        // Allow transactions if we are writing to the database
        myEnvConfig.setTransactional(!readOnly);
        myDbConfig.setTransactional(!readOnly);
        mySecConfig.setTransactional(!readOnly);

        // Open the environment
        myEnv = new Environment(envHome, myEnvConfig);

        // Now open, or create and open, our databases
        // Open the vendors and inventory databases
        feedDB = myEnv.openDatabase(null,"FeedDB",myDbConfig);

        // Open the class catalog db. This is used to
        // optimize class serialization.
        classCatalogDb = myEnv.openDatabase(null,"ClassCatalogDB",myDbConfig);

        // Create our class catalog
        classCatalog = new StoredClassCatalog(classCatalogDb);


    }

	public Database getFeedDB() {
		return feedDB;
	}

	public void setFeedDB(Database feedDB) {
		this.feedDB = feedDB;
	}
	
	/*public Object get(byte[] key){
		
		Object output = null;
		try{			
			DatabaseEntry keyEntry = new DatabaseEntry(key);
	    	DatabaseEntry dataEntry = new DatabaseEntry();
	    	
			OperationStatus status = feedDB.get(null, keyEntry, dataEntry, null);
	        if (status == OperationStatus.SUCCESS) {
	        	output = SerializationUtils.deserialize(dataEntry.getData());
	        }
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return output;
	}
	
	public void put(byte[] key, byte[] data){
		
		try{
			feedDB.put(null, new DatabaseEntry(key), new DatabaseEntry(data));			
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	public <K extends Serializable , V extends Serializable> V delete(K k){
		V v = null;
		try{			
			v = get(k);
			if(v != null){
				DatabaseEntry keyEntry = new DatabaseEntry(SerializationUtils.serialize(k));
				OperationStatus status = feedDB.delete(null, keyEntry);
		        if (status == OperationStatus.SUCCESS) {
		        	
		        }
			}
			
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return v;
	}
	
	public <K extends Serializable , V extends Serializable> void put(K k, V v){
		try{
			feedDB.put(null, new DatabaseEntry(SerializationUtils.serialize(k)), new DatabaseEntry(SerializationUtils.serialize(v)));			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public <K extends Serializable , V extends Serializable> V get(K k){
		V v = null;
		try{			
			DatabaseEntry keyEntry = new DatabaseEntry(SerializationUtils.serialize(k));
	    	DatabaseEntry dataEntry = new DatabaseEntry();
	    	
			OperationStatus status = feedDB.get(null, keyEntry, dataEntry, null);
	        if (status == OperationStatus.SUCCESS) {
	        	v = SerializationUtils.deserialize(dataEntry.getData());
	        }
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return v;
	}
	
	
	public void close(){
		if(feedDB != null){
			feedDB.close();
		}
		if(classCatalogDb != null){
			classCatalogDb.close();
		}
		if(classCatalog != null){
			classCatalog.close();
		}
		if(myEnv != null){
			myEnv.close();
		}
			
	}
	
	
	
    
    public static void main(String[] args){
    	String key = "key";
    	String data = "data2";
    	
    	FeedDB db = new FeedDB("c:/temp/data/", false);
    	System.out.println(db.getFeedDB());
    	
    	/*db.getFeedDB().putNoOverwrite(null, new DatabaseEntry(SerializationUtils.serialize(key)), new DatabaseEntry(SerializationUtils.serialize(data)));
    	
        DatabaseEntry keyEntry = new DatabaseEntry();
        DatabaseEntry dataEntry = new DatabaseEntry();

    	Cursor cursor = db.getFeedDB().openCursor(null, null);

        while (cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT) ==       OperationStatus.SUCCESS) {
            System.out.println("key=" + (String)SerializationUtils.deserialize(keyEntry.getData()) +
                               " data=" + (String)SerializationUtils.deserialize(dataEntry.getData()));

        }
        cursor.close();
    	
    	DatabaseEntry keyEntry1 = new DatabaseEntry(SerializationUtils.serialize(key));
    	DatabaseEntry dataEntry1 = new DatabaseEntry();
    	
        OperationStatus status = db.getFeedDB().get(null, keyEntry1, dataEntry1, null);
        if (status == OperationStatus.SUCCESS) {
        	String output = (String)SerializationUtils.deserialize(dataEntry1.getData());
        	System.out.println(output);
        }else{
        	System.out.println("Empty");
        }*/
    	
    	db.put(key, data);    	
    	String data1 = db.get(key);
    	System.out.println(data1);
    	String data2 = db.delete(key);
    	System.out.println(data2);
    	data1 = db.get(key);
    	System.out.println(data1);
    	
    }

}
