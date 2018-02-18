
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class ConfigManager {
    
	
	// database connection parameters
    public String dbstring;
    public String dbuser;
    public String dbpass;   
 	public String relative_resource_path;

	private static String config_string = "./WEB-INF/config.xml";

    public ConfigManager(HttpServlet servlet) {
        try {
            ServletContext context = servlet.getServletContext();
            // System.out.println(context.getContextPath());
            InputStream input = context.getResourceAsStream(config_string);
			if (input != null) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(input);
				doc.getDocumentElement().normalize();
				// set database connection parameters
				dbstring = doc.getElementsByTagName("dbstring").item(0)
						.getTextContent().trim();
				dbuser = doc.getElementsByTagName("dbuser").item(0)
						.getTextContent().trim();
				dbpass = doc.getElementsByTagName("dbpass").item(0)
						.getTextContent().trim();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
