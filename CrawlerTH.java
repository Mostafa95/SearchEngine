
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
//import java.sql.Statement;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
public class CrawlerTH extends Thread {

    public static int MAXDEPTH = 8;
    public String url;
    public static HashMap<String, Integer> Vis_URLState = new HashMap<String, Integer>();

    public void run() {
        int thrs = 20;

//        System.out.println("Choose how many threads to operate");
//        Scanner scanner = new Scanner(System.in);
//        thrs = scanner.nextInt();
//        scanner.close();

        String temp = "";
        String urll = "http://www.mkyong.com";

        //read from state if the crawller was interrputted
        try (BufferedReader read = new BufferedReader(new FileReader("/home/mostafa/NetBeansProjects/WebApplication5/State"))) {
            while (read.ready()) {
                temp = read.readLine();
                Vis_URLState.put(temp, 1);
                urll = temp;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        boolean f = true;
        Crawler MainC = new Crawler((int) (System.currentTimeMillis() / 1000));

        while (true) {
            System.out.println("BSMALLAAAAAH....");        
            MainC.mainprocessPage(urll, Vis_URLState);
            Vis_URLState.clear();
            int t=(int) (System.currentTimeMillis()/ 1000)+10000;
            
            for (int i = 0; i < thrs; i++) {
                (new Crawler(t)).start();
            }
            
            // indexer
//            if (f) {
//                (new Indexer((int) Vis_URLState.size())).start();
//                f = false;
//            }

            
            (new RankBackUp()).start();
            // wait for crawlers to finish
            try {
                TimeUnit.SECONDS.sleep((long) (10002));
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerTH.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
