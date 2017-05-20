
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ranker {

    public ConcurrentHashMap<String, Integer> Ranking;

    Ranker() {
        Crawler c = new Crawler(1);
        Ranking = new ConcurrentHashMap<String, Integer>();
        Ranking = c.GetRanking();
        Iterator it = Ranking.entrySet().iterator();
        //System.out.println("Size: " + Ranking.size());
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println("Ranker : site:" + pair.getKey() + " VAlue :" + pair.getValue());
//        }
    }

    public List<Integer> GetPageRanking(List<String> Site) throws FileNotFoundException {

        List<Integer> l = new ArrayList<>();

        BufferedReader r = new BufferedReader(new FileReader("/home/mostafa/NetBeansProjects/WebApplication5/Rank"));
        int lines = 0;
        try {
            while (null != r.readLine()) {
                lines++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Ranker.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (lines > Ranking.size()) { // crash happened
            try (BufferedReader read = new BufferedReader(new FileReader("/home/mostafa/NetBeansProjects/WebApplication5/Rank"))) {
                while (read.ready()) {
                    String temp = read.readLine();
                    //System.out.println(temp);
                    String[] a = temp.split("ض");
                    for (String q : a) {
                        
                        if (q.length() <= 4) {
                            //System.out.println("After : "+q);
                            l.add(Integer.parseInt(q));
                            break;
                        }
                    }
                    //if(a.length >=2)

                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            for (String a : Site) {
                Object o = Ranking.get(a);
                if (o == null) {
                    continue;
                }
                l.add((int) o);
            }
        }
        return l;
    }

    public List<String> GetPages(List<String> Sites, List<Integer> SiteWP) {
        List<Integer> RankSite = new ArrayList<>();

        try {
            RankSite = GetPageRanking(Sites);
        } catch (FileNotFoundException ex) {

            Logger.getLogger(Ranker.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < SiteWP.size(); i++) {
            int j = i + 1;
            if (j == SiteWP.size()) {
                break;
            }
            while (SiteWP.get(i) == SiteWP.get(j)) {
                if (i >= RankSite.size() || j >= RankSite.size()) {
                    break;
                }
                if (RankSite.get(j) > RankSite.get(i)) {

                    String temp = Sites.get(i);
                    Sites.set(i, Sites.get(j));
                    Sites.set(j, temp);

                    int te = SiteWP.get(i);
                    SiteWP.set(i, SiteWP.get(j));
                    SiteWP.set(j, te);

                    int t = RankSite.get(i);
                    RankSite.set(i, RankSite.get(j));
                    RankSite.set(j, t);
                }
                if (++j == SiteWP.size()) {
                    break;
                }
            }
        }
        return Sites;
    }

}
