
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

    /**
     *
     */
    public static ConcurrentHashMap<String, Integer> Vis_URL = new ConcurrentHashMap<String, Integer>();
    public static ConcurrentHashMap<String, String> Hashed_URL = new ConcurrentHashMap<String, String>();
    public static Queue<String> To_Vis_URL = new ConcurrentLinkedQueue<String>();
    public static ConcurrentHashMap<String, Integer> Ranking = new ConcurrentHashMap<String, Integer>();

    public int maxdepth = 10;
    public static int VisUrlSz = 0;
    public static int cnt;
    public Number statenum = 0;
    public boolean flag = true;
    public int time;

    Crawler(int t) {
        time = t;
    }

    public ConcurrentHashMap<String, Integer> GetHashRank (){
        return Ranking;
    }
    
    public synchronized void Visited(String site) {
        Vis_URL.put(site, 1);
    }

    public synchronized void SetHash(String site, Document doc) {

        // Vis for Freq array
        Elements e = doc.getElementsByTag("title");
        String title = e.text();
        e = doc.getElementsByTag("meta");
        String meta = e.attr("content");
        e = doc.getElementsByTag("a");
        String a = e.text();

        Hashed_URL.put(site, a + title + meta);
        
        
    }

    public synchronized void towrite(String site) {
        try (BufferedWriter statefile = new BufferedWriter(new FileWriter("/home/mostafa/NetBeansProjects/WebApplication5/State", true))) {
            statefile.write(site + "/");
            statefile.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public synchronized boolean Check(String site) {
        if (Vis_URL.get(site) == null) {
            return true;
        }
        return false;
    }

    public synchronized void tovis(String site) {

        if (Vis_URL.get(site) == null) {
            To_Vis_URL.add(site);
        }
        Object o = Ranking.get(site);
        if (o == null) {
            Ranking.put(site,1);
            return;
        }
        Ranking.put(site, (int)o +1);

    }

    public ConcurrentHashMap<String, Integer> GetRanking(){
        return Ranking;
    }
    
    public void processPage(String URL_Link, int depth) throws InterruptedException, MalformedURLException {

        if (VisUrlSz >= 900) {
            System.out.println("ANA 5LST.. 25ern b2et farasha gamyleeeeh 2222!!");
            return;
        }
        Visited(URL_Link);
        try {
            URL RobotURL = new URL(URL_Link);

            String Host = "http://" + RobotURL.getHost();
            //System.out.println("Host= " + Host);
            // String temp1 = link.attr("abs:href");
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(new URL(Host + "/robots.txt").openStream()))) {//temp1 + "robots.txt 
                //System.out.println("B3d " +  e.attr("abs:href"));
                String line = null;
                int tcnt = 1;
                while ((line = in.readLine()) != null && ++tcnt < 50) {

                    if (line.startsWith("Disallow")) {
                        try (BufferedWriter rob = new BufferedWriter(
                                new FileWriter("/home/mostafa/NetBeansProjects/WebApplication5/robot", true))) {
                            String y = line.substring(9);
                            rob.write(y);
                            rob.newLine();
                            String Comp = Host;
                            if (y.length() > 3) {
                                String te = line.substring(10), te2 = "";

                                for (int i = 0; i < te.length(); i++) {
                                    if (te.charAt(i) == '*') {
                                        te2 += "(.*)";
                                    } else {
                                        te2 += te.charAt(i);
                                    }
                                }
//                                System.out.println("teStre " + te + "// te2String " + te2);
//                                System.out.println(Host);
                                if (URL_Link.matches(te2)) {
                                    System.out.println("msktk !!");
                                    return;
                                }

                            }
                        } catch (IOException e1) {

                            System.out.println(e1);
                        }
                    }
                }
            } catch (IOException e1) {
                System.out.println(e1);;
            } catch (IllegalArgumentException ee) {
                System.out.println(ee);
            }

        } catch (java.net.MalformedURLException e) {
            System.out.println("Could not create URL !!");
        }

        // System .out.println(x);
        if (URL_Link.endsWith("/")) {
            URL_Link = URL_Link.substring(0, URL_Link.length() - 1);
        }

        Document doc = null;
        try {
            if (URL_Link == "") {
                return;
            }
           
            doc = Jsoup.connect(URL_Link).get();
        } catch (IOException e) {
            System.out.println("Doc Not connected to " + URL_Link);
            return;
        }
        System.out.println("D5l be " + URL_Link);
        SetHash(URL_Link, doc);
        towrite(URL_Link);

        // HTML file
        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/WebApplication5/HTML/" + this.cnt++ + ".html", false))) {
            file.write((doc.toString()));
            VisUrlSz++;

        } catch (IOException e) {
            System.out.println(e);
        }

        Elements links = null;
        try {
            links = doc.body().getElementsByAttribute("href");
        } catch (NullPointerException e) {
            return;
        }
        if (links != null) {
            for (Element link : links) {
                tovis(link.attr("abs:href"));// In queue
            }

        }
        //System.out.println("size abl: " + To_Vis_URL.size());
        while (!To_Vis_URL.isEmpty() && (int) (System.currentTimeMillis() / 1000) < time) {
            String site = To_Vis_URL.poll();
            if (Check(site)) {
                processPage(site, depth + 1);
            }
        }
        System.out.println("ANA 5LST.. 25ern b2et farasha gamyleeeeh !!");

    }

    public void mainprocessPage(String URL, HashMap<String, Integer> Vis) {
        if (!Vis.isEmpty()) {
            Vis_URL.putAll(Vis);
        }
        int cnter = Vis.size();
        if (cnter != 0) {
            cnt = cnter;
        } else if (!Vis_URL.isEmpty()) {
            cnt = Vis_URL.size();
        } else {
            cnt = 0;
        }

        //System.out.println("Main Process Man cnt = " + cnt);
        if (Hashed_URL.isEmpty()) {
            Document doc = null;
            try {
                doc = Jsoup.connect(URL).get();
            } catch (IOException e) {
                System.out.println(e);

            }

            try (BufferedWriter file = new BufferedWriter(
                    new FileWriter("/home/mostafa/NetBeansProjects/WebApplication5/HTML/" + cnt++ + ".html", false))) {
                file.write((doc.toString()));
                VisUrlSz++;
            } catch (IOException e) {
                System.out.println(e);
            }
            Visited(URL);
            SetHash(URL, doc);
            towrite(URL);

            Elements temp = doc.body().getElementsByAttribute("href");
            for (Element e : temp) {
                tovis(e.attr("abs:href"));
            }
        } else { // for freq
            //System.out.println("d5l fy al else..");
            String Lsite = "";
            Iterator<?> it = Hashed_URL.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String site = (String) pair.getKey();
                String has = (String) pair.getValue();

                Lsite = site;
                Document doc = null;
                try {
                    doc = Jsoup.connect(site).get();
                } catch (IOException e) {
                    System.out.println(e);

                }
                //get meta + a + title
                Elements e = doc.getElementsByTag("title");
                String title = e.text();
                e = doc.getElementsByTag("meta");
                String meta = e.attr("content");
                e = doc.getElementsByTag("a");
                String a = e.text();

                String temp = a + title + meta;
                if (!has.equals(temp)) {
//                    System.out.println("has= " + has + "site= " + a + title);
//                    System.out.println("d5l fy to visit..2");
                    tovis(site);
                    Lsite = "";
                } else {
                    System.out.println("Not changed!!");
                }
            }
            if (!Lsite.isEmpty()) {
                tovis(Lsite);
            }
        }
    }

    @Override
    public void run() {
        // System.out.println("cnt is: " + cnt);
        while (!To_Vis_URL.isEmpty()) {

            String site = To_Vis_URL.poll();
//            System.out.println("Run ste is: " + site);

            try {
                processPage(site, 1);
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        // State file
    }

}
