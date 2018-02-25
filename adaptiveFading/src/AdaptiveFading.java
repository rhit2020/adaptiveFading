

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdaptiveFading")
public class AdaptiveFading extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/*---------------------------------------------------------
	 * The content-concept relationship data structure (STATIC)
	 * --------------------------------------------------------- */			
	private static HashMap<String, HashMap<Integer,List<String>>> content_concept_map;
	private static HashMap<String,List<Integer>> challenge_blank_map;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdaptiveFading() {
        super();
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get the URL parameters
		String usr = request.getParameter("usr"); // user name
		String grp = request.getParameter("grp"); // group name
		String item = request.getParameter("item"); // name of the item
		String A = request.getParameter("A"); // name of the item

		
		if (AdaptiveFading.content_concept_map == null) 
			content_concept_map = getContentConcept() ; //fill the static variable the first time

		if (AdaptiveFading.challenge_blank_map == null) 
			challenge_blank_map = getChallengeBlankMap() ; //fill the static variable the first time

		List<Integer> blankLines = challenge_blank_map.get(item);
		List<Integer> linesToBeShown = new ArrayList<Integer>();

		
		if (A.equals("1")) {
			//call cache
			HashMap<String,Double> kmap = GetKCSummary.getItemKCEstimates(usr, grp);

			if (item != null) {
				HashMap<Integer,List<String>> lineConcept = content_concept_map.get(item);
				List<String> concepts;
				double average;
				int count;
				if (lineConcept != null) {
					for (int bline : blankLines) {
						concepts = lineConcept.get(bline-1); //note:sline = bline - 1, sline starts from 0, bline starts from 1
						if (concepts != null) {
							average = 0;
							count = 0;
							for (String c : concepts) {
								if (kmap.get(c) != null) {
									average += kmap.get(c);
									count++;
								}
							}
							if (count == 0) {
								average = 0;
								System.out.println("AdaptiveFading.java:count got 0 for the item: "+item);
							}
							else
								average = average / count; 
							if (average < 0.7) {
								linesToBeShown.add(bline);
							}
						}
					}
				}
			}

		} else {
			linesToBeShown.addAll(blankLines);
		}
		
		//return the response to the user
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String output = "{\"lines\":\""+getLinesToShow(linesToBeShown)+"\", \"noBlank\":" +
				(blankLines.size()-linesToBeShown.size())+"}";
		out.print(output);
		
	}

	private String getLinesToShow(List<Integer> linesToBeShown) {
		String txt = "";
		for (int i : linesToBeShown)
			txt += (txt.isEmpty() ? i : "," + i);
		return txt;
	}


	private HashMap<String, HashMap<Integer,List<String>>>  getContentConcept() {
		DB db;
		ConfigManager cm = new ConfigManager(this);
		db = new DB(cm.dbstring,cm.dbuser,cm.dbpass);
		db.openConnection();
		HashMap<String, HashMap<Integer,List<String>>> map = db.getContentConcept();
		db.closeConnection();
		return map;
	}
	
	private HashMap<String, List<Integer>> getChallengeBlankMap() {
		DB db;
		ConfigManager cm = new ConfigManager(this);
		db = new DB(cm.dbstring,cm.dbuser,cm.dbpass);
		db.openConnection();
		HashMap<String, List<Integer>> map = db.getChallengeBlankMap();
		db.closeConnection();
		return map;
	}
}
