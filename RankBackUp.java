
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import static java.util.Spliterators.iterator;
import static java.util.Spliterators.iterator;
import static java.util.Spliterators.iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RankBackUp extends Thread{
    public ConcurrentHashMap<String, Integer> Rank;

    public RankBackUp() {
    Rank = new ConcurrentHashMap<>();
    }
    
    
    public void run(){
        
        Crawler c= new Crawler(1);
       
        while(true)
        {
            try {
                TimeUnit.SECONDS.sleep((long) (5));
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerTH.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Rank = c.GetHashRank();
            System.out.println("Rank Size: "+Rank.size());
            try (BufferedWriter file = new BufferedWriter(
                    new FileWriter("/home/mostafa/NetBeansProjects/WebApplication5/Rank", false))) {
                Iterator <?> it=Rank.entrySet().iterator();
                
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry) it.next();
                    String x = (String) pair.getKey();
                    if(x.isEmpty())
                        continue;
                    String site = x+"ض"+ Integer.toString((int) pair.getValue()) ;
                   // System.out.println("Link is: "+site);
                   
                   file.write(site);
                   file.newLine(); 
                }
                
                file.close();
            } catch (IOException e) {
                System.out.println(e);
            }   
        }
    }
    
}
