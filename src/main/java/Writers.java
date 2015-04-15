

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nick.cassandra.SimpleClient;


public class Writers {
	
	long totalInserts;
	public static void main(String[] args){
		new Writers().init();
	}
	
	volatile long startTime = 0;
	
	SimpleClient c = null;
	public void init(){
		
		c = new SimpleClient();
		c.init();
		
		ExecutorService s = Executors.newFixedThreadPool(10);
		PrintingThread p = new PrintingThread(c);
		MyThread t1 = new MyThread(c,0);
		MyThread t2 = new MyThread(c,10_000_000);
		Future<String> pf = s.submit(p);
		Future<String> f1 = s.submit(t1);
		//Future<String> f2 = s.submit(t2);
		
		/*if(f1.isDone()){
			pf.cancel(true);
		}*/
		
		
	}
	
	public void print(){
		long elapsed = (System.currentTimeMillis() - startTime) / 1000;
		if(elapsed == 0){
			elapsed = 1;
		}
		
		if(c.previousInserts == c.totalInserts){
			System.out.println("Same");
			return;
		}
		
		System.out.println("w:"+totalInserts+" elapsed:"+ elapsed + " w/s " +totalInserts/elapsed);
		c.previousInserts = totalInserts;
	}
	
	class MyThread implements Callable<String> {

		SimpleClient client;
		int start;
		public MyThread(SimpleClient client, int start) {
			this.client = client;
			this.start = start;

		}

		@Override
		public String call() throws Exception {
			for (int i = 0; i < 2000000; i++) {
				client.totalInserts++;
				
				client.addBatch(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
				/*if (i % 10000 == 0) {
					Thread.sleep(100);
				}*/
			}
			System.out.println("Done");
			return "Sucess";

		}

	}

	class PrintingThread implements Callable<String> {
		SimpleClient client;
		public PrintingThread(SimpleClient client) {
			this.client = client;
		}

		@Override
		public String call() throws Exception {
			while (true) {
				client.print();
				//print();
				Thread.sleep(1000);
			}

		}

	}


}
