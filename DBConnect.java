package indexer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    DBconnect() {
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
    
    public Connection GetCon(){return con;}
    
    public ResultSet URLSelect(int U_WordID) {
        try {
            String q = "SELECT * FROM `URL` WHERE U_WordID=" + U_WordID;
            rs = st.executeQuery(q);

//            while (rs.next()) {
//                String x = rs.getString("HallNumber");
//                String y = rs.getString("NumofSeats");
//                 String y1 = rs.getString("NumofRows");
//                System.out.println(x + " " + y+ " " + y1);
//            }
        } catch (Exception e) {
            System.out.println("Select Failed!!");
        }
        return rs;
    }

    public ResultSet WordSelect(String Name)  {
        try {
            String q = "SELECT * FROM `Word` WHERE Name='" + Name+"'";
            rs = st.executeQuery(q);
        } catch (Exception e) {
            System.out.println(e);
        }
        return rs;
    }

    public void WordInsert(String Name){
        try {
            String q = "INSERT INTO `Word`(`WordID`, `Name`) VALUES (null,'"+Name+"')";
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Insertion Failed!!");
        }

    }
    public void URLInsert(String Name,int Word_pri, String U_WordID,int PageRank) {
        try {

            String q;
            q = "INSERT INTO `URL`(`ID`, `Name`, `Word_Pri`, `U_WordID`, `PageRabk`) VALUES (null,'"+Name+"',"+Word_pri+","+U_WordID+","+PageRank+")";;
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Insertion Failed");
        }
    }
    public void URLUpdate(String ID,String Name,int Word_pri, String U_WordID,int PageRank ) {
        try{
            String q;
            q = "UPDATE `URL` SET `Name`='" + Name + "',`Word_Pri`=" + Word_pri + ",`U_WordID`=" + U_WordID + ",`PageRabk`=" + PageRank + " WHERE `ID`=" + ID;
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Update Failed");
        }
    }

    public void URLdelete(String ID) {
         try{
            String q;
            q = "DELETE FROM `URL` WHERE `ID`="+ID;
            st.executeUpdate(q);

        } catch (Exception e) {
            System.out.println("Delete Failed");
        }
    }



}

