
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HelloFor extends HttpServlet {

    private String query;
    private int Start;

    private PorterStemmer PS;
    private QuaryProcess QP;
    private List<String> Page = new ArrayList<>();

    @Override
    public void init() {
        query = "";
        PS = new PorterStemmer();
        //(new CrawlerTH()).start();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        query = request.getParameter("Search");
        System.out.println(query);
        String a = request.getParameter("Start");
        if (a == null) {
            Start = 1;
        } else {
            Start = Integer.parseInt(a);
        }

        boolean URLexists = true, Phrase = false;
        if (query.indexOf('"') >= 0) {
            Phrase = true;
        }
//        try {
//            //test
//            TimeUnit.SECONDS.sleep((long) (5));
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HelloFor.class.getName()).log(Level.SEVERE, null, ex);
//        }

        List<Integer> WF = new ArrayList<>();
        QP = new QuaryProcess(query);
        Page = QP.GetPages();

        if (Page == null) {
            System.out.println("NO PAGES");
            URLexists = false;
        } else {
            WF = QP.GetWordFreq();

            Ranker r = new Ranker();
            Page = r.GetPages(Page, WF);
        }
        // Set response content type
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        String title = GET_TITLE();
        String docType
                = "<!doctype html public \"-//w3c//dtd html 4.0 "
                + "transitional//en\">\n";

        docType += "<html>\n"
                + "<head><title>" + title + "</title></head>\n"
                + "<body>\n"
                + "<h1 align=\"center\">" + title + "</h1>\n"
                + "<h2 align=\"center\"> Results for "+query+"</h2><ul>";
        int c = 0;
        if (URLexists) {
            boolean ff = false;
            for (int i = (Start - 1) * 7; i < Page.size() && c < 7; i++) {
                String ll = "", s = Page.get(i);
                if (Phrase) {
                    ll = PhraseSearching(query, s);
                } else {
                    ll = SearchText(query, s); // get sentence where word was mentioned
                }
                if (ll.equals("???")) {
                    continue;
                }
                docType += "<li><a href=" + s + " target=_blank>" + s + " </a></li><br/>"
                        + "<p>" + ll + "</p><br/><br/>";
                c++;
                ff = true;
            }
            docType += "</ul>";
            if(ff)
                docType += "</body>" + Next_Label() + "</html>";
            else
                docType += "<p>NO links exists :( </p></body></html>";
        } else {
            docType += "<p>NO links exists :( </p>";
        }
        
        out.println(docType);
        System.err.println("After "+query);
    }

    public String GET_TITLE() {

        return "\n"
                + "\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n"
                + "    </head>\n"
                + "    <body >\n"
                + "    <!--<center><img src=\"Search.jpg\"  align=\"right\" width=\"250\" height=\"200\" /></center>-->\n"
                + "    <form action=\"HelloFor\" method=\"GET\">\n"
                + "        <table style=\"width:80%\">\n"
                + "            <tr>\n"
                + "                 <td><img src=\"Search.jpg\"  align=\"right\" width=\"250\" height=\"200\" /></td>\n"
                + "                <td> <input type=\"text\" name=\"Search\" size=\"60\">\n"
                + "                <button class=\"w3-btn w3-blue w3-xlarge\">Search <i class=\"fa fa-search\" ></i></button>\n"
                + "                </td>\n"
                + "               \n"
                + "            </tr>\n"
                + "        </table>\n"
                + "    </form>\n"
                + "   \n"
                + "</body>\n"
                + "</html>";
    }


    
    private String SearchText(String q, String site) {
        // COnnect to site
        if(site.endsWith("/"))
            site=site.substring(0,site.length()-1);
        Document doc = null;
        try {
            doc = Jsoup.connect(site).get();
        } catch (IOException e) {
            System.out.println("SearchText Doc Not connected to " + site);
            return "???";
        }
        String l = new String();
        String PageText = doc.body().text();

        String Words[], queryWords[];
        String parser = " ";//[ ~!@#$^&*=>><<{};:,<>|.]
        Words = PageText.split(parser);

        queryWords = q.split(parser);
        int s = 0, e = 0;
        System.out.println("d5l");
        for (int i = 0; i < Words.length; i++) {
            if (Words[i].isEmpty()) {
                continue;
            }

            for (String x : queryWords) {
                String aa = Words[i].toLowerCase();
                aa = PS.stem(aa);

                x = x.toLowerCase();
                x = PS.stem(x);

                if (aa.equals(x)) {
                    s = max(0, i - 5);
                    e = min(Words.length - 1, i + 5);
                    break;
                }
            }
        }
        l += "...";
        if (s == e) {
            return "???";
        }

        for (int a = s; a <= e; a++) {
            l += " " + Words[a];
        }
        return l + " ...";
    }

    private String PhraseSearching(String qu, String site) {
        // process query
        String[] QuWords = qu.split(" ");
        List<String> DoubleQuWords = new ArrayList<>();
        for (String a : QuWords) {
            int index = a.indexOf('"');
            if (index == 0)// lw "sdcs" 7tb2a mshkla
            {
                DoubleQuWords.add(a.substring(1, a.length()));
            } else if (index == a.length() - 1) {
                DoubleQuWords.add(a.substring(0, a.length() - 1));
            }
        }
        // System.out.println("site  now: " + site);
        Document doc = null;
        try {
            doc = Jsoup.connect(site).get();
        } catch (IOException e) {
            System.out.println("In PhraseSearch Doc Not connected to " + site);
            return "???";
        }

        // parse body
        String PageText = doc.body().text();
        String PageWords[];
        PageWords = PageText.split("\\.");
        for(String x:PageWords)
            System.out.print(x+" ");
        System.out.println();
        for(String x: DoubleQuWords)
            System.out.println(x);
            
        String res = SearchTextMethode(PageWords, DoubleQuWords);// Tempres = "";

        if (res.contains("ظ")) {
            return res.substring(0, res.length() - 1);
        }
        if (!res.equals("NO")) {
            return res;
        }
        return "???";

    }

    private String SearchTextMethode(String[] Pagelines, List<String> DoubleQuWords) {
        String res2 = "";
        String res = "";

        String fw = DoubleQuWords.get(0);
        fw.toLowerCase();
        fw = PS.stem(fw);

        //System.out.println("Sz"+Pagelines.length);
        for (int i = 0; i < Pagelines.length; i++) {

            String string = Pagelines[i];
            //System.out.println("line is: " + string);
            String[] str = string.split(" ");
            for (int jj = 0; jj < str.length; jj++) {

                String s = str[jj];
                s.toLowerCase();
                s = PS.stem(s);
                //System.out.println("fw: " + fw + " other: " + s);
                if (s.equals(fw)) {
                    res2 = Pagelines[i];
                    //System.out.println("res2 " + res2);
                    boolean f = true;
                    int j = jj;
                    for (String ss : DoubleQuWords) {

                        ss.toLowerCase();
                        ss = PS.stem(ss);

                        String sss = str[min(j, str.length)];
                        sss.toLowerCase();
                        sss = PS.stem(sss);

                        // System.out.println("ss: " + fw + " sss: " + s + "  ind: " + min(j, str.length) + " j: " + j + " strlen: " + str.length);
                        if (ss.equals(sss)) {
                            j++;
                            continue;
                        }
                        f = false;
                    }
                    if (f) {
                        return Pagelines[i];
                    }
                }
            }

        }
        if (res2.isEmpty()) {
            return "NO";
        }

        return res2 + "ظ";
    }

    public String Next_Label() {
        int sz = Page.size() / 7;
        if (Page.size() % 7 > 0) {
            sz++;
        }
        String r = "";
//        for(int i=0;i< query.length();i++)
//            if(query.==" ")
//                query[i]="+";
        for (int i = 1; i <= sz; ++i) {
            r += "<a href = http://localhost:8080/WebApplication5/HelloFor?Start=" + i + "&Search=" + query + ">" + i + " " + "</a>";
        }
        return r;
    }
}
