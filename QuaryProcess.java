
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class QuaryProcess {

    private String Input;
    private PorterStemmer St;
    private DBconnect DB;

    public QuaryProcess(String s) {
        Input = s;
        Input.toLowerCase();
        St = new PorterStemmer();
        DB = new DBconnect();
    }

    public List<String> GetPages() {

        String Words[];
        List<String> temp = new ArrayList<>();
        boolean f= false;
        if(Input.indexOf('"') >= 0)
            f=true;
        Words = Input.split("[\"\\ ]");
        for (int i = 0; i < Words.length; i++) {
            Words[i] = St.stem(Words[i]);
        }
        try {
            if(f)
                temp = DB.GetQueryURLPhrase(Words);
            else
                temp = DB.GetQueryURL(Words);
        } catch (SQLException ex) {
            Logger.getLogger(QuaryProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return temp;

    }

    public List<Integer> GetWordFreq() {
        return DB.GetWordsFreq();
    }

}
