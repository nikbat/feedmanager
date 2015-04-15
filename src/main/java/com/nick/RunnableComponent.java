package com.nick;

import org.apache.log4j.Logger;

/**
 * Implements {@link Runnable} to provide logging and error handling 
 * functionality to "runnable" components.
 * 
 * Such components are normally executed asynchronously by a thread or a a 
 * timer. 
 * 
 * Instead of implementing <tt>run()</tt>, subclasses should implement
 * <tt>doWork()</tt>. 
 */
public abstract class RunnableComponent implements Runnable {
	
	protected Logger log = Logger.getLogger(getDefaultLogCategory());
	
	String name = getClass().getSimpleName();
	private volatile long lastStartTime;
	private volatile long lastActivityTime;
	private volatile long lastFinishTime = System.currentTimeMillis();
	private volatile Thread runningThread;
	
	public abstract void doWork() throws Exception;

	public void setName(String name) {
		this.name = name;
		this.log = Logger.getLogger(getDefaultLogCategory() + "." + name);
	}
	
	public String getName() {
	    return name;
    }
	
	public boolean isRunning() {
		return runningThread != null;
	}
	
	public long getLastActivityTime() {
		return lastActivityTime;
	}
	
	public long getLastStartTime() {
		return lastStartTime;
	}
	
	public long getLastFinishTime() {
		return lastFinishTime;
	}
	
	public Thread getRunningThread() {
		return runningThread;
	}
	
	/**
	 * Note that something has been done. This is intended to be used by 
	 * longer-running components to keep the hang checker from seeing a hang
	 * when work is being accomplished.
	 */
	protected void noteActivity() {
		lastActivityTime = System.currentTimeMillis();
	}
	
	@Override
	synchronized public final void run() {
		try {
			noteActivity();
			lastStartTime = lastActivityTime;
			runningThread = Thread.currentThread();
			
			log.info("Starting " + getClass().getSimpleName());
			doWork();
			log.info("Finished " + getClass().getSimpleName());
			
		} catch (Throwable t) {
			String message = t.getMessage();
			if (message == null) {
				message = t.toString();
			} else {
				message = message + " - " + t.getClass().getName();
			}
			
			getLogger().error(message + " - doWork()", t);
			
		} finally {
			lastFinishTime = System.currentTimeMillis();
			runningThread = null;
		}
	}
	
	/**
	 * Returns this component's default log category. Defaults to the class 
	 * name, but can be overridden by subclasses to return a custom value.
	 * 
	 * @return
	 */
	protected String getDefaultLogCategory() {
		return this.getClass().getName();
	}

	/**
	 * Returns this component's logger.
	 * 
	 * @return
	 */
	public Logger getLogger() {
		return log;
	}

	/**
	 * Convenience method for logging an error.
	 */
	public void error(String message, Throwable t) {
		log.error(message, t);
	}

	/**
	 * Convenience method for logging an error.
	 */
	public void error(String message) {
		log.error(message);
	}

	/**
	 * Convenience method for logging a warning.
	 */
	public void warn(String message, Throwable t) {
		log.warn(message, t);
	}

	/**
	 * Convenience method for logging a warning.
	 */
	public void warn(String message) {
		log.warn(message);
	}
}
