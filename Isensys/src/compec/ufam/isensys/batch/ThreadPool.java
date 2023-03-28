package compec.ufam.isensys.batch;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
	
    private final PoolWorker[] threads;
    private final LinkedBlockingQueue<Runnable> queue;
    private boolean started = false;

    public ThreadPool(final int numThreads) {
    	
        this.queue   = new LinkedBlockingQueue<Runnable>();
        this.threads = new PoolWorker[numThreads];

        for (int i = 0; i < numThreads; i++) {
        	
            threads[i] = new PoolWorker();
            threads[i].start();
            
        }
        
    }

    public void execute(final Runnable task) {
    	
        synchronized (queue) {
        	
            queue.add(task);
            queue.notify();
            
        }
        
        started = true;
        
    }

    private class PoolWorker extends Thread {
    	
    	@Override
        public void run() {
        	
            Runnable task;

            while (!started || !queue.isEmpty()) {
            	
                try {
                	
                	synchronized (queue) {
                		task = queue.poll();
					}
                    
                    if (task != null)
                    	task.run();
                    
                } catch (RuntimeException exception) {
                	
                	exception.printStackTrace();
                    
                }
                
            }
            
        }
    	
    }
    
}