
package sephase1;

import java.util.concurrent.TimeUnit;


public class SEphase1 {

    
    public static void main(String[] args) throws InterruptedException {
       
        
        (new CrawlerTH()).start();
        TimeUnit.SECONDS.sleep(5);
        (new Indexer()).start();
    }
    
}
