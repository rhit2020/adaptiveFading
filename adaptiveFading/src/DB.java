import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DB extends dbInterface {
    public DB(String connurl, String user, String pass) {
        super(connurl, user, pass);
    }


	public HashMap<String, HashMap<Integer, List<String>>> getContentConcept() {


		try {
			HashMap<String, HashMap<Integer, List<String>>> contentConcept =
					   new HashMap<String, HashMap<Integer, List<String>>>();
			HashMap<Integer, List<String>> lineConcepts;
			List<String> concepts;
			
			
			stmt = conn.createStatement();
			
			String query = " select distinct content_name, concept, sline from content_concept;";

			rs = stmt.executeQuery(query);
			String content, concept;
			int line;
			while (rs.next()) {
				content = rs.getString("content_name");
				concept = rs.getString("concept");
				line = rs.getInt("sline");
				lineConcepts = contentConcept.get(content);
				if (lineConcepts != null) {
					concepts = lineConcepts.get(line);
					if (concepts != null) {
						if (concepts.contains(concept) == false)
							concepts.add(concept);
					}else {
						concepts = new ArrayList<String>();
						concepts.add(concept);
						lineConcepts.put(line, concepts);
					}
				} else {
					concepts = new ArrayList<String>();
					concepts.add(concept);
					
					lineConcepts = new HashMap<Integer, List<String>>();
					lineConcepts.put(line, concepts);
					contentConcept.put(content, lineConcepts);
				}
			}
			return contentConcept;
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}

}
