/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sephase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

    public int thr;
    public static ConcurrentHashMap<String, Integer> Vis_URL = new ConcurrentHashMap<String, Integer>();
    public int maxdepth = 10;
    public int VisUrlSz = 0;
    public static int cnt = 0;
    public Number statenum = 0;
    public boolean flag = true;

    public synchronized boolean torplc(String site) {
        return Vis_URL.replace(site, 0, 1);
    }

    public synchronized void towrite(String site) {
        try (BufferedWriter statefile = new BufferedWriter(new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/State.txt", true))) {
            statefile.write(site + "/");//// HAAAAA<<<
            statefile.newLine();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //mmkn t substitie el sz da b countmapping btb2a more accurate 
    public synchronized boolean Check(String site, int val) {
        if (val == 0) { // System.out.println("Before " +
            // Thread.currentThread().getId() + " " + site + " " +
            // val);
            if (Vis_URL.replace(site, 0, 1)) {
                //System.out.println(site + " " + Vis_URL.get(site));
            }
            return true;
        }
        return false;
    }

    public synchronized void tovis(String site) {

        if (Vis_URL.get(site) == null) {
            Vis_URL.put(site, 0);//    
        }
    }

    public void processPage(String URL_Link, int depth) throws InterruptedException, MalformedURLException {

        if (VisUrlSz >= 8) {
            return;
        }

        try {
            URL RobotURL = new URL(URL_Link);

            String Host = "http://" + RobotURL.getHost();
            System.out.println("Host= " + Host);
            // String temp1 = link.attr("abs:href");
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(new URL(Host + "/robots.txt").openStream()))) {//temp1 + "robots.txt 
                //System.out.println("B3d " +  e.attr("abs:href"));
                String line = null;
                cnt = 1;
                while ((line = in.readLine()) != null && ++cnt < 50) {

                    if (line.startsWith("Disallow")) {
                        try (BufferedWriter rob = new BufferedWriter(
                                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/robot.txt", true))) {
                            String y = line.substring(9);
                            rob.write(y);
                            rob.newLine();
                            String Comp = Host;
                            if (y.length() > 3) {
                                String te = line.substring(10), te2 = "";

                                for (int i = 0; i < te.length(); i++) {
                                    if (te.charAt(i) == '*') {
                                        te2 += "(.*)";
                                    }
                                    te2 += te.charAt(i);
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

        boolean ha = torplc(URL_Link);
        if (ha) {
            System.out.println(URL_Link + " " + Vis_URL.get(URL_Link));
        }

        // System .out.println(x);
        if (URL_Link.endsWith("/")) {
            URL_Link = URL_Link.substring(0, URL_Link.length() - 1);
        }
        // String paramValue = "param\\with\\backslash";
//        String yourURLStr="";
//        try {
//            yourURLStr = URL_Link + java.net.URLEncoder.encode(paramValue, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
//        }

        Document doc = null;
        try {
            if (URL_Link == "") {
                return;
            }
            System.out.println("D5l be " + URL_Link);
            doc = Jsoup.connect(URL_Link).get();
        } catch (IOException e) {
            System.out.println("Doc Not connected to " + URL_Link);
            return;
        }
        // System.out.println("3dda");
        // HTML file
        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + this.cnt++ + ".html", false))) {
            file.write((doc.toString()));
            VisUrlSz++;
            // System.out.println(URL_Link + " " + VisUrlSz);
        } catch (IOException e) {
            System.out.println(e);
        }
        // State file

        towrite(URL_Link);
        Elements links = null;
        try {
            links = doc.body().getElementsByAttribute("href");
        } catch (NullPointerException e) {
            return;
        }
        if (links != null) {
            for (Element link : links) {
                tovis(link.attr("abs:href"));
            }

        }

        Iterator<?> it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Check(pair.getKey().toString(), (int) pair.getValue())) {
                processPage(pair.getKey().toString(), depth + 1);
            }
            // it.remove();// avoids a ConcurrentModificationException
        }
    }

    public void mainprocessPage(String URL, int t, ConcurrentHashMap<String, Integer> Vis, int cnter) {
        Vis_URL.putAll(Vis);
        thr = t;
        this.cnt = cnter;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            System.out.println(e);
        }

        try (BufferedWriter file = new BufferedWriter(
                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + this.cnt++ + ".html", false))) {
            file.write((doc.toString()));
        } catch (IOException e) {
            System.out.println(e);
        }

        // e3mel state l awel wa7eddddd haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaammmmmmmmmm
        Elements temp = doc.body().getElementsByAttribute("href");
        for (Element e : temp) {
            tovis(e.attr("abs:href"));
            // System.out.println("abl " +  e.attr("abs:href"));

            String temp1 = e.attr("abs:href");

//            try (BufferedReader in = new BufferedReader(
//                    new InputStreamReader(new URL(temp1 + "robots.txt").openStream()))) {//temp1 + "robots.txt 
//                //System.out.println("B3d " +  e.attr("abs:href"));
//                String line = null;
//                while ((line = in.readLine()) != null) {
//
//                    if (line.startsWith("Disallow")) {
//                        try (BufferedWriter rob = new BufferedWriter(
//                                new FileWriter("/home/mostafa/NetBeansProjects/Indexer/HTMLs/robot.txt", true))) {
//
//                            rob.write(line.substring(9));
//                            rob.newLine();
//
//                        } catch (IOException e1) {
//
//                            System.out.println(e1);
//                        }
//                    }
//                }
//            } catch (IOException e1) {
//                continue;
//            } catch (IllegalArgumentException ee) {
//                System.out.println(temp1);
//            }
        }
    }

    @Override
    public void run() {
        // int pos = (int) (Thread.currentThread().getId() % Vis_URL.size());
        //
        // String l = (String) Vis_URL.keySet().toArray()[pos];
        // processPage(l, 1);

        Iterator<?> it = Vis_URL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (Check(pair.getKey().toString(), (int) pair.getValue())) {
                try {
                    processPage(pair.getKey().toString(), 1);
                    //break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        // State file
    }

}

