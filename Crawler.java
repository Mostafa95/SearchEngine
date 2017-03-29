
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

    public static ConcurrentHashMap<String, Integer> Vis_URL = new ConcurrentHashMap<String, Integer>();
    public int maxdepth = 10;
    public static int VisUrlSz = 0;
    public int cnt = 0;
    // public int thrs;
    public Number statenum = 0;

    public synchronized boolean torplc(String site) {
        return Vis_URL.replace(site, 0, 1);
    }

    public synchronized boolean Check(String site, int val) {
        if (val == 0) {

            // System.out.println("Before " + Thread.currentThread().getId() + " " + site + " " + val);
            if (Vis_URL.replace(site, 0, 1)) {
                System.out.println(site + " " + Vis_URL.get(site));
            }

            return true;
        }
        return false;
    }

    public synchronized void tovis(String site) {
        if (!(Vis_URL.containsKey(site))) {
            Vis_URL.put(site, 0);
        }
    }

    public void processPage(String URL, int depth) {

        if (depth > 110 ||  VisUrlSz > 20) {//
            return;
        }

        // Object Val = Vis_URL.get(URL);
        // if (Val == null) {
        // Vis_URL.put(URL, 0);
        // } //mn condition el main msh mn hna
        // URL found but not parsed
        // boolean ha = torplc(URL);
        // if (ha)
        // System.out.println(URL + " " + Vis_URL.get(URL));
        // System .out.println(x);
        if (URL.endsWith("/")) {
            URL = URL.substring(0, URL.length() - 1);
        }

        Document doc = null;
        try {
            System.out.println("D5l be " + URL);
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            System.out.println("Doc Not connected to URL");
        }
        // System.out.println("3dda");
        // HTML file
        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + cnt++ + ".html", false))) {
            file.write((doc.toString()));
            VisUrlSz++;
        } catch (IOException e) {
            System.out.println(e);
        }

        // State file
        // try (BufferedWriter statefile = new BufferedWriter(
        // new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
        // statefile.write((int) statenum); //// HAAAAA<<<
        // } catch (IOException e) {
        // System.out.println(e);
        // }
        Elements links = doc.body().getElementsByAttribute("href");
        if (links != null) {
            for (Element link : links) {

                tovis(link.attr("abs:href"));
            }

        }
//        int lo = Vis_URL.size();
//        for (int i = 0; i < lo; i++) {
//            int val;
//            String link;
//            synchronized (Vis_URL) {
//                val = (int) Vis_URL.values().toArray()[i];
//                link = (String) Vis_URL.keySet().toArray()[i];
//            }
//
//            if (Check(link, val)) {
//                processPage(link, depth + 1);
//            }
//        }
        Iterator<?> it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Check(pair.getKey().toString(), (int) pair.getValue())) {
                processPage(pair.getKey().toString(), depth + 1);
            }

            it.remove();// avoids a ConcurrentModificationException
        }

        // try (BufferedWriter statefile = new BufferedWriter(
        // new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
        // statefile.write((int) statenum); //// HAAAAA<<<
        // } catch (IOException e) {
        // System.out.println(e);
        // }
    }

    public void mainprocessPage(String URL) {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
            //System.out.println("tmma");
        } catch (IOException e) {
            System.out.println(e);
        }
        //System.out.println("msh tmma");
        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + cnt++ + ".html", false))) {
            file.write((doc.toString()));
        } catch (IOException e) {
            System.out.println(e);
        }
        // get all links and recursively call the processPage method
        // Elements Links = doc.getElementsByTag("body");
        Elements temp = doc.body().getElementsByAttribute("href");

        String x;
        for (Element e : temp) {
            tovis(e.attr("abs:href"));
        }

    }

    @Override
    public void run() {
//        int pos = (int) (Thread.currentThread().getId() % Vis_URL.size());
//
//        String l = (String) Vis_URL.keySet().toArray()[pos];
//        processPage(l, 1);

        Iterator it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if ((int) pair.getValue() == 0) {
                //  System.out.println(pair.getKey().toString());
                processPage(pair.getKey().toString(), 1);
                break;
            }
            it.remove(); // avoids a ConcurrentModificationException
        }

    }
}

