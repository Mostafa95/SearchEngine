
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import java.util.LinkedList;
//import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

    public static HashMap Vis_URL = new HashMap();
    public int maxdepth = 4;
    public static int VisUrlSz = 0;
    public int cnt = 0;
    public Number statenum = 0;

    public void processPage(String URL, int depth) {

        if (depth == maxdepth || VisUrlSz == 50) {
            return;
        }

        if (URL.endsWith("/")) {
            URL = URL.substring(0, URL.length() - 1);
        }

        Object Val = Vis_URL.get(URL);
        if (Val == null) {
            Vis_URL.put(URL, 0);
        }

        // URL found but not parsed
        Vis_URL.replace(URL, 0, 1);
        VisUrlSz++;
        Document doc = null;
        try {
            System.out.println("D5l be "+URL);
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            System.out.println("Doc Not connected to URL");
        }
        System.out.println("3dda");
        // HTML file
        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + cnt++ + ".html", false))) {
            file.write((doc.toString()));
        } catch (IOException e) {
            System.out.println(e);
        }
        // State file
//        try (BufferedWriter statefile = new BufferedWriter(
//                new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
//            statefile.write((int) statenum); //// HAAAAA<<<
//        } catch (IOException e) {
//            System.out.println(e);
//        }

        Elements links = doc.body().getElementsByAttribute("href");
        for (Element link : links) {
            Vis_URL.put(link.attr("abs:href"), 0);

        }

        Iterator it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if ((int) pair.getValue() == 0) {
                processPage(pair.getKey().toString(), ++depth);
            }

            it.remove(); // avoids a ConcurrentModificationException
        }

//        try (BufferedWriter statefile = new BufferedWriter(
//                new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
//            statefile.write((int) statenum); //// HAAAAA<<<
//        } catch (IOException e) {
//            System.out.println(e);
//        }
    }

    public void mainprocessPage(String URL) {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            System.out.println("Doc Not connected to URL");
        }

        try (BufferedWriter file = new BufferedWriter(new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + cnt++ + ".html", false))) {
            file.write((doc.toString()));
        } catch (IOException e) {
            System.out.println(e);
        }
        // get all links and recursively call the processPage method
      // Elements Links = doc.getElementsByTag("body");
       Elements temp = doc.body().getElementsByAttribute("href");
//       String temp = Links.attr("href");
//       String [] str = temp.split(" ");
//       System.out.println(temp);
       
        for (Element e : temp) {
          // System.out.println(e.attr("href"));
            Vis_URL.put(e.attr("abs:href"), 0);
        }

    }

    @Override
    public void run() {
        Iterator it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if ((int) pair.getValue() == 0) {
                System.out.println(pair.getKey().toString());
                processPage(pair.getKey().toString(), 1);
                break;
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}

