package Package;

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
	public static DB db = new DB();
	public static HashMap Vis_URL = new HashMap();
	public static int thrs;
	public int maxdepth = 2;

	public int cnt = 0;
	public int states[];
	public int statecnt = 0;
	public Number statenum = 0;
	public int cnter = 0;
	/** Queue to store URL's, can be accessed by multiple threads **/
	public ConcurrentLinkedQueue<String> tovisit = new ConcurrentLinkedQueue<String>();

	/** ArrayList of visited URL's **/
	public ArrayList<String> visited = new ArrayList<String>();

	public void processPage(String URL, int depth) throws SQLException, IOException {
		// check if the given URL is already in database
		// cnt++;

		if (depth == maxdepth)
			return;

		String Suburl, sql;
		if (URL.endsWith("/")) {
			Suburl = URL.substring(0, URL.length() - 1);
		}
		URL = Suburl;
		Object Val = Vis_URL.get(URL);
		if (Val == null) {
			Vis_URL.put(URL, 0);
		}
		if ((int) Val == 0) { // URL found but not parsed
			Vis_URL.replace(URL, 0, 1);
			Document doc = Jsoup.connect(URL).get();

			try (BufferedWriter file = new BufferedWriter(
					new FileWriter("C:\\Ahmed\\uni\\APT\\" + cnt + ".html", false))) {
				file.write((doc.toString()));
			} catch (IOException e) {
				System.out.println(e);
			}
			// get all links and recursively call the processPage method
			String sqlid = "select RecordID from Record where URL = '" + URL + "'";
			ResultSet rss = db.runSql(sqlid);
			if (rss.next()) {
				statenum = ((Number) rss.getObject("RecordID")).intValue();
				System.out.println(statenum);
			}

			try (BufferedWriter statefile = new BufferedWriter(
					new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
				statefile.write((int) statenum); //// HAAAAA<<<
			} catch (IOException e) {
				System.out.println(e);
			}

			Elements links = doc.select("a[href]");
			for (Element link : links) {
				Vis_URL.put(link.toString(), 0);
				// Vis_URL.
			}

			// for(int i=0; i<Vis_URL.size();i++){
			Iterator it = Vis_URL.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if ((int) pair.getValue() == 0)
					processPage(pair.getKey().toString(), ++depth);

				// System.out.println(pair.getKey() + " = " +
				// pair.getValue());
				it.remove(); // avoids a ConcurrentModificationException
			}

			// hhhhggggf

			// ResultSet rs = db.runSql(sql);

			try (BufferedWriter statefile = new BufferedWriter(
					new FileWriter("C:\\Ahmed\\uni\\APT\\State.txt", false))) {
				statefile.write((int) statenum); //// HAAAAA<<<
			} catch (IOException e) {
				System.out.println(e);
			}
			// Document doc = Jsoup.connect(URL).get();

			// }
		}
	}

	// }
	// cnter++;
	// if (cnter < thrs + 1) {
	// Crawler crawl = new Crawler();
	// crawl.processPage(link.attr("abs:href"));
	// Thread tt = new Thread(crawl);
	// tt.start();
	// } // depth++;
	// else
	// processPage(link.attr("abs:href"));
	// // String theLink = htmls.attr("abs:href");
	// // if(theLink.startsWith("http") &&
	// // !(tovisit.contains((Object)theLink)))
	// // {
	// // tovisit.add(theLink);
	// // visited.add(theLink);
	// // System.out.println(theLink);
	// // }
	// }
	// }else{

	// }

	public void mainprocessPage(String URL) throws SQLException, IOException {
		// check if the given URL is already in database
		// cnt++;
		// String sql = "INSERT INTO `Crawler`.`Record` " + "(`URL`) VALUES " +
		// "(?);";
		// PreparedStatement stmt = db.conn.prepareStatement(sql,
		// Statement.RETURN_GENERATED_KEYS);
		// stmt.setString(1, URL);
		// stmt.execute();
		// // get useful information
		Document doc = Jsoup.connect(URL).get();

		try (BufferedWriter file = new BufferedWriter(new FileWriter("C:\\Ahmed\\uni\\APT\\" + cnt + ".html", false))) {
			file.write((doc.toString()));
		} catch (IOException e) {
			System.out.println(e);
		}
		// get all links and recursively call the processPage method
		Elements links = doc.select("a[href]");

		for (Element e : links) {
			Vis_URL.put(e.toString(), 0);
		}

	}

	@Override
	public void run() {
		String sq = "select * from Record where URL = '" + URL + "'";
		ResultSet rs = db.runSql(sql);
		if (rs.next()) {
		// TODO Auto-generated method stub

	}
}
