

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

		
		if (AdaptiveFading.content_concept_map == null) 
			content_concept_map = getContentConcept() ; //fill the static variable the first time
			
		//call cache
		HashMap<String,Double> kmap = GetKCSummary.getItemKCEstimates(usr, grp);
		
		HashMap<Integer,List<String>> lineConcept = content_concept_map.get(item);
		
		List<Integer> linesToBeShown = new ArrayList<Integer>();
		double average;
		int count;
		for (int i : lineConcept.keySet()) {
			average = 0;
			count = 0;
			for (String c : lineConcept.get(i)) {
				if (kmap.get(c) != null) {
					average += kmap.get(c);
					count++;
				}
			}
			average = average / count; 
			if (average < 0.7) {
				linesToBeShown.add(i);
			}
		}
		
		//return the response to the user
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String output = "{\"lines\":\""+getLinesToShow(linesToBeShown)+"\"}";
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
}
