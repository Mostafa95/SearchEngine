/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
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
public class CrawlerTH extends Thread{

    public static int MAXDEPTH = 8;
    public String url;
    public static ConcurrentHashMap<String, Integer> Vis_URLState = new ConcurrentHashMap<String, Integer>();

    public void run()  {
        int thrs;

        System.out.println("Choose how many threads to operate");
        Scanner scanner = new Scanner(System.in);
        thrs = scanner.nextInt();
        scanner.close();

        String temp = "";
        String urll = "http://www.mkyong.com";

        //read from state if the crawller was interrputted
        try (BufferedReader read = new BufferedReader(new FileReader("/home/mostafa/NetBeansProjects/Indexer/HTMLs/State.txt"))) {
            while (read.ready()) {
                temp = read.readLine();
                Vis_URLState.put(temp, 1);
                urll = temp;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        Crawler MainC = new Crawler();
        MainC.mainprocessPage(urll, Vis_URLState, (int) Vis_URLState.mappingCount());
        for (int i = 0; i < thrs; i++) {
            
            (new Crawler()).start();
            
            try {
                TimeUnit.SECONDS.sleep((long) ((i + 3) * 0.3));
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerTH.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Create Indexer Thread
        (new Indexer( (int) Vis_URLState.mappingCount()) ).start();
    }

}


