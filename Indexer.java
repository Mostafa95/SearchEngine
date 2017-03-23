/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author mostafa
 */
public class Indexer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        try {
            DBconnect con = new DBconnect();
            // con.WordInsert("MOstafa");
//        ResultSet rs=con.WordSelect("Mosa");
//        String y="";
//        while (rs.next()) {
//            String x = rs.getString("Name");
//            y = rs.getString("WordID");
//            //String y1 = rs.getString("NumofRows");
//            //System.out.println(x + " " + y );
//            
//        }
//        con.URLInsert("www.go.com", 100, y, 0);
            //con.URLUpdate("2", "www.google.com", 0, "2", 0);
            //con.URLdelete("3");
            //con.get();

            int MAXStrSz = 16, MINStrSz = 3;
            String parser = "[ `~!@#$^&*()-=>><<{};:\"\\,.<>\\|]";
            HashMap Hash = new HashMap();
            PorterStemmer _Stem = new PorterStemmer();

            Document doc = Jsoup.connect("http://jsoup.org/").get();

            // parsing entire document , pri +1
            String PageText = doc.body().text();
            String Words[];
            Words = PageText.split(parser);

            for (String Word : Words) {
                if (Word.length() <= MINStrSz || Word.length() >= MAXStrSz) {
                    continue;
                }
                // stem

                Word = Word.toLowerCase();
                Word= _Stem.stem(Word);
                Object val = Hash.get(Word);
                if (val == null) {
                    Hash.put(Word, 1);
                } else {
                    Hash.put(Word, (int) val + 1);
                }
            }
            // parsing <a> // pri +10
            Elements Hyperlink = doc.getElementsByTag("a");
            String WordsHyper[], Hypertext;
            Hypertext = Hyperlink.text();
            WordsHyper = Hypertext.split(parser);

            for (String Word : WordsHyper) {

                if (Word.length() <= MINStrSz || Word.length() >= MAXStrSz) {
                    continue;
                }
                //stem
                  Word= Word.toLowerCase();
                  Word= _Stem.stem(Word);

                Object val = Hash.get(Word);
                if (val == null) {
                    Hash.put(Word, 10);
                } else {
                    Hash.put(Word, (int) val + 10);
                }
                // System.out.println(e.text());
            }
//
            // parse meta // pri+100
            Elements Meta = doc.getElementsByTag("meta");
            String temp = Meta.attr("content");
            String[] str = temp.split(parser);
            for (String Word : str) {

                if (Word.length() <= MINStrSz || Word.length() >= MAXStrSz) {
                    continue;
                }
                //stem

                 Word= Word.toLowerCase();
                 Word= _Stem.stem(Word);
                Object val = Hash.get(Word);
                if (val == null) {
                    Hash.put(Word, 100);
                } else {
                    Hash.put(Word, (int) val + 100);
                }
                // System.out.println(e.text());
            }
//            // parse meta // pri+1000
            Elements title = doc.getElementsByTag("title");
            String Wordstitle[], titletext;
            titletext = title.text();
            Wordstitle = titletext.split(parser);
            for (String e : Wordstitle) {
                if (e.length() <= MINStrSz || e.length() >= MAXStrSz) {
                    continue;
                }
                //stem
                e= e.toLowerCase();
                   e= _Stem.stem(e);

                Object val = Hash.get(e);
                if (val == null) {
                    Hash.put(e, 1000);
                } else {
                    Hash.put(e, (int) val + 1000);
                }
                // System.out.println(e.text());
            }
            // test hash Table
            Iterator it = Hash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
            for (int i = 0; i < Hash.size(); i++) {
                System.out.println(Hash.get(i));
            }
//            for (Element e : Hyperlink) {
//                System.out.println(e.text());
//            }
            con.GetCon().close();
        } catch (Exception ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

