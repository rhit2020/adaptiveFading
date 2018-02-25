import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StoreFeedback
 */
@WebServlet("/StoreFeedback")
public class StoreFeedback extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StoreFeedback() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String usr = request.getParameter("usr"); 
		String grp = request.getParameter("grp"); 
		String sid = request.getParameter("sid"); 
		String item = request.getParameter("item");
		String val = request.getParameter("val");
		String topicId = request.getParameter("topic_id");
		String A = request.getParameter("A");
		
		ConfigManager cm = new ConfigManager(this); 
		
		DB db = new DB(cm.dbstring, cm.dbuser, cm.dbpass);
		db.openConnection();
		db.insertUsrFeedback(usr, grp, sid, item, val, topicId, A);
		db.closeConnection();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request,response);
	}

}
