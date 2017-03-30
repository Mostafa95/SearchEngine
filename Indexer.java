/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.internal.org.xml.sax.InputSource;
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
    public static void main(String[] args) throws SQLException, ParserConfigurationException {
        DBconnect con = new DBconnect();
        int MAXStrSz = 16, MINStrSz = 3, PageRank = 0;
        String parser = "[ `~!@#$^&*()-=>><<{};:\"\\,<>\\|\'\\. ]", URL_Link = "";//http://jsoup.org/

        PorterStemmer _Stem = new PorterStemmer();
//String temp="";
// Document doc = Jsoup.connect(URL_Link).get();
        try (BufferedReader read = new BufferedReader(new FileReader("/home/mostafa/NetBeansProjects/Indexer/HTMLs/State.txt"))) {
            Document doc = null;
            int cnt = 1;
            while (read.ready()) {
                HashMap Hash = new HashMap();
                URL_Link = read.readLine();
                System.out.println(URL_Link);
                File input = new File("/home/mostafa/NetBeansProjects/Indexer/HTMLs/" + cnt++ + ".html");
                doc = Jsoup.parse(input, "UTF-8", "");

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
                    Word = _Stem.stem(Word);
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
                    Word = Word.toLowerCase();
                    Word = _Stem.stem(Word);

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

                    Word = Word.toLowerCase();
                    Word = _Stem.stem(Word);
                    Object val = Hash.get(Word);
                    if (val == null) {
                        Hash.put(Word, 100);
                    } else {
                        Hash.put(Word, (int) val + 100);
                    }
                    // System.out.println(e.text());
                }
//            // parse title // pri+1000
                Elements title = doc.getElementsByTag("title");
                String Wordstitle[], titletext;
                titletext = title.text();
                Wordstitle = titletext.split(parser);
                for (String e : Wordstitle) {
                    if (e.length() <= MINStrSz || e.length() >= MAXStrSz) {
                        continue;
                    }
                    //stem
                    e = e.toLowerCase();
                    e = _Stem.stem(e);

                    Object val = Hash.get(e);
                    if (val == null) {
                        Hash.put(e, 1000);
                    } else {
                        Hash.put(e, (int) val + 1000);
                    }
                    // System.out.println(e.text());
                }
                ResultSet rs;
                System.out.println(Hash.size());
                Iterator it = Hash.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String WordName = pair.getKey().toString();
                    int WordFreq = (int) pair.getValue();

                    rs = con.WordSelect(WordName);

                    if (!rs.next()) { // Word doesn't exist in DB
                        con.WordInsert(WordName);

                        ResultSet rs2 = con.WordSelect(WordName);

                        if (rs2.next() == false) {
                            System.out.println("False");
                        } else {
                            String WordID = rs2.getString("WordID");

                            con.URLInsert(URL_Link, WordFreq, WordID, PageRank);

                        }
                    } else {
                        String WordID = rs.getString("WordID");
                        con.URLInsert(URL_Link, WordFreq, WordID, PageRank);
                    }

                    it.remove(); // avoids a ConcurrentModificationException
                }

//            for (Element e : Hyperlink) {
//                System.out.println(e.text());
//            }
            }
            try {
                con.GetCon().close();
            } catch (SQLException se) {
                System.out.println("NOT CLosed !!");
            }
        } catch (IOException ex) {

            System.out.println("Error");

        }
    }
}

