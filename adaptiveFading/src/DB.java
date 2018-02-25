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
			
			String query = " select distinct title, concept, sline from ent_challenge_concept;";

			rs = stmt.executeQuery(query);
			String content, concept;
			int line;
			while (rs.next()) {
				content = rs.getString("title");
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
	
	public String getNewStudyContents() {

		try {
			stmt = conn.createStatement();
			
			String query = " select distinct content_name from new_study_contents;";

			rs = stmt.executeQuery(query);
			String contents = "";
			while (rs.next()) {
				contents += rs.getString("content_name") + ",";
			}
			if (contents.length() > 0)
				contents = contents.substring(0, contents.length() - 1); // this is
																			// for
																			// ignoring
																			// the
																			// last
																			// ,
			return contents;
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}

	public String getCourseContent(int cid) {
		try {
			String contents = "";
			stmt = conn.createStatement();

			String query = " SELECT  C.content_name " +
                           " FROM ent_topic T, rel_topic_content TC, ent_content C" +
                           " WHERE T.course_id = " + cid + 
                           " and T.active=1 and C.visible = 1 " +
                           " and TC.visible = 1 " +
                           " and TC.topic_id=T.topic_id and C.content_id = TC.content_id;"; 
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				contents += rs.getString("content_name") + ",";
			}
			if (contents.length() > 0)
				contents = contents.substring(0, contents.length() - 1); // this is
																			// for
																			// ignoring
																			// the
																			// last
																			// ,
			return contents; //returns the last result (value of last record)
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}

	public Double getPrevAttemptResult(String usr, String grp, String prevAttempt) {
		try {
			Double res = null;
			stmt = conn.createStatement();

			String query = " SELECT result FROM um2.ent_user_activity UA" + 
						   " where UA.userid = (select userid from um2.ent_user where login='"+ usr + "')" +
						   " and UA.groupid = (select userid from um2.ent_user where login='"+ grp + "')"+
					       " and UA.activityid = (select activityid from um2.ent_activity where activity='"+ prevAttempt + "')"; 
			rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				res = rs.getDouble("result");
			}
			return res; //returns the last result (value of last record)
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}


	public HashMap<String, List<Integer>> getChallengeBlankMap() {
		try {
			HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
			stmt = conn.createStatement();

			String query = "SELECT content_name,blank_line from user_study_2018.rel_challenge_blank;"; 
			rs = stmt.executeQuery(query);
			String content_name;
			int bline;
			List<Integer> list;
			while (rs.next()) {
				content_name = rs.getString("content_name");
				bline = rs.getInt("blank_line");
				list = map.get(content_name);
				if (list == null) {
					list = new ArrayList<Integer>();
					list.add(bline);
					map.put(content_name, list);
				} else {
					list.add(bline);
				}
			}
			return map; 
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}


	public void insertUsrFeedback(String usr, String grp, String sid, 
								  String item, String val, String topicId, String A) {

        String query = "";
        try {
            stmt = conn.createStatement();
            query = "INSERT INTO ent_user_feedback (user_id,session_id,group_id,"
            		+ "item, response, datentime, topic_id, A) values ('"
                        + usr
                        + "','"
                        + sid
                        + "','"
                        + grp
                        + "','"
                        + item
                        + "',"
                        + val
                        + ","
                        + "now(),'"
                        + topicId + "','"
                        + A
                        + "');";
                stmt.executeUpdate(query);
            this.releaseStatement(stmt, rs);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            System.out.println(query);
            releaseStatement(stmt, rs);
        }		
	}
}
