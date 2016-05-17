import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class TrainingServlet extends HttpServlet{
	
	private HttpClient client=new HttpClient();
	private String tag[];
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		response.sendRedirect("iframe.html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		System.out.println("Start training...");
		
		String url_str=request.getParameter("url");
		String address=request.getParameter("address");
		System.out.println("url string: "+url_str);
		System.out.println("address: "+address);
		
		String content = client.getContentFromUrl(url_str);
		if(content.contains(address))
			System.out.println("contains the address!");
		tag = dataProcess(content,address);
		
		saveToDB(url_str,tag);
		printSuccessPage(out);
	}

	private void saveToDB(String url_str, String[] tag2) throws MalformedURLException {
		// TODO Auto-generated method stub
		URL url=new URL(url_str);
		String host=url.getHost();
	}

	private String[] dataProcess(String content, String address) throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		System.out.println(content);
		String[] tag=null;
//		ArrayList<Integer> indexs=new ArrayList<Integer>();
//		int index=content.indexOf(address);
//		index.add();
//		while (index >= 0) {
//		    System.out.println(index);
//		    index = word.indexOf(guess, index + 1);
//		}
		
		
//		String root=System.getProperty("user.dir");
//		//String root=ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
//		String binPath=null;
//		
//		System.out.println(new File(".\\bin").getAbsolutePath());
//		
//		if(root.contains("\\"))
//			binPath=".\\bin\\en-ner-person.bin";
//		else
//			binPath=root+"/src/en-ner-person.bin";
//		


//		int position = getPosition(address, content); 
//		Tidy tidy = new Tidy(); 
//		tidy.setQuiet(true); 
//		tidy.setShowWarnings(false);
//		tidy.setXHTML(true); 
//		
//		StringWriter writer=new StringWriter();
//		Document doc = tidy.parseDOM(new StringReader(content),writer);
//		//System.out.println(writer.toString());
	
		return null;
	}

	private void printSuccessPage(PrintWriter out) {
		// TODO Auto-generated method stub
		out.println("<!DOCTYPE html><html><body>");
		out.println("<title>training</title>");
		out.println("<a href=\"training\">Back</a>");
		out.println("</body></html>");
	}

}
