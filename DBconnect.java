
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBconnect {

    private String driver;
    private String url;
    private String name;
    private String pass;
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private List<Integer> WordFreq;

    DBconnect() {
        WordFreq = new ArrayList<>();
        driver = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/SearchEngine";
        name = "root";
        pass = "";
        try {
            Class.forName(driver);

            try {
                con = DriverManager.getConnection(url, name, pass);
                st = con.createStatement();
            } catch (SQLException ex) {
                System.out.println("NOT Connected!!");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBconnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection GetCon() {
        return con;
    }

    public ResultSet URLSelect(int U_WordID) {
        try {
            String q = "SELECT * FROM `URL` WHERE U_WordID=" + U_WordID;
            rs = st.executeQuery(q);

        } catch (Exception e) {
            System.out.println("URL Select Failed!!");
        }
        return rs;
    }

    public String WordSelect(String Name) {
        try {

            String q = "SELECT * FROM `Word` WHERE Name='" + Name + "'";
            rs = st.executeQuery(q);
            try {
                if (rs.next()) {
                    String x = rs.getString("WordID");
                    rs.close();
                    return x;
                }

            } catch (SQLException ex) {
                Logger.getLogger(DBconnect.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return "";
    }

    public void WordInsert(String Name) {
        try {
            String q = "INSERT INTO `Word`(`WordID`, `Name`) VALUES (null,'" + Name + "')";
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Word Insertion Failed!!");
        }

    }

    public void URLInsert(String Name, int Word_pri, String U_WordID, int WordIndex) {
        try {

            String q;
            q = "INSERT INTO `URL`(`ID`, `Name`, `Word_Pri`, `U_WordID`, `WordIndex`) VALUES (null,'" + Name + "'," + Word_pri + "," + U_WordID + "," + WordIndex + ")";;
            System.out.println("Link : " + Name + " Inserted ..");

            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("URL Insertion Failed");
        }
    }

    public void URLUpdate(String ID, String Name, int Word_pri, String U_WordID, int WordIndex) {
        try {
            String q;
            q = "UPDATE `URL` SET `Name`='" + Name + "',`Word_Pri`=" + Word_pri + ",`U_WordID`=" + U_WordID + ",`WordIndex`=" + WordIndex + " WHERE `ID`=" + ID;
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Update Failed");
        }
    }

    public void URLdelete(String ID) {
        try {
            String q;
            q = "DELETE FROM `URL` WHERE `ID`=" + ID;
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Delete Failed");
        }
    }

    public List<String> GetQueryURL(String[] x) throws SQLException {
        List<String> W = new ArrayList<>();
        List<String> Re = new ArrayList<>();
        HashMap<String, Integer> Re2 = new HashMap<String, Integer>();
        for (String a : x) {
            if (a.isEmpty()) {
                continue;
            }
            String temp = WordSelect(a);
            if (temp.isEmpty()) {
                continue;
            }
            W.add(temp);
        }
        String qString = "SELECT DISTINCT Name , Word_Pri FROM `URL` WHERE U_WordID =";
        boolean f = false;
        for (int i = 0; i < W.size(); i++) {
            f = true;
            qString += W.get(i);
            if (i < W.size() - 1) {
                qString += " or U_WordID =";
            }
        }
        if (f) {
            qString += " ORDER BY Word_Pri DESC";
            rs = st.executeQuery(qString);
            while (rs.next()) {
                Re2.put(rs.getString("Name"), Integer.parseInt(rs.getString("Word_Pri")));

            }
            Iterator<Map.Entry<String, Integer>> it = Re2.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Re.add((String) pair.getKey());
                WordFreq.add((int) pair.getValue());
            }
            return Re;
        } else {
            return null;
        }
    }

    public List<String> GetQueryURLPhrase(String[] x) throws SQLException {
        List<String> W = new ArrayList<>();
        List<String> Re = new ArrayList<>();
        HashMap<String, Integer> Re2 = new HashMap<String, Integer>();
        HashMap<String, Integer> Re3 = new HashMap<String, Integer>();
        for (String a : x) {
            if (a.isEmpty()) {
                continue;
            }
            String temp = WordSelect(a);
            if (temp.isEmpty()) {
                continue;
            }
            W.add(temp);
        }
        if (W.isEmpty()) {
            return null;
        }
        
        // Get Intersection
        String qString = "SELECT  Name, Word_Pri  FROM `URL` WHERE U_WordID =";
        int qwas = 0;
        for (int i = 0; i < W.size(); i++) {

            qString += W.get(i);
            if (i < W.size() - 1) {
                qString += " AND EXISTS (SELECT DISTINCT Name, Word_Pri  FROM `URL` WHERE U_WordID =";
                qwas++;
            }
        }
        for (int i = 0; i < qwas; i++) {
            qString += ")";
        }
        qString += " ORDER BY Word_Pri DESC";
        rs = st.executeQuery(qString);
        while (rs.next()) {
            Re2.put(rs.getString("Name"), Integer.parseInt(rs.getString("Word_Pri")));

        }
        Iterator<Map.Entry<String, Integer>> it = Re2.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Re.add((String) pair.getKey());
            WordFreq.add((int) pair.getValue());
        }
        //============================================================================
        qString = "SELECT  Name , Word_Pri FROM `URL` WHERE U_WordID =";

        for (int i = 0; i < W.size(); i++) {

            qString += W.get(i);
            if (i < W.size() - 1) {
                qString += " or U_WordID =";
            }
        }

        qString += " ORDER BY Word_Pri DESC";
        rs = st.executeQuery(qString);
        while (rs.next()) {
            if(Re2.get(rs.getString("Name")) == null)
                Re3.put(rs.getString("Name"), Integer.parseInt(rs.getString("Word_Pri")));

        }
        Iterator<Map.Entry<String, Integer>> itt = Re3.entrySet().iterator();
        while (itt.hasNext()) {
            Map.Entry pair = (Map.Entry) itt.next();
            Re.add((String) pair.getKey());
            WordFreq.add((int) pair.getValue());
        }
        System.err.println("RE size is "+Re.size());
        return Re;

    }

    public List<Integer> GetWordsFreq() {
        return WordFreq;
    }

}
