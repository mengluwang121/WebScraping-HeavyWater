import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

public class SearchingServlet extends HttpServlet {
	
	private HttpClient client=new HttpClient();
	
	//private String states="California|Alabama|Arkansas|Arizona|Alaska|Colorado|Connecticut|Delaware|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|New Hampshire|New Jersey|New Mexico|New York|North Carolina|North Dakota|Ohio|Oklahoma|Oregon|Pennsylvania|Rhode Island|South Carolina|South Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|West Virginia|Wisconsin|Wyoming";
	//private String statesBre="CA|";
	private String states=" Alabama | AL | Alaska | AK | Arizona | AZ | Arkansas | AR | California | CA | Colorado | CO | Connecticut | CT | District of Columbia | DC | Delaware | DE | Florida | FL | Georgia | GA | Hawaii | HI | Idaho | ID | Illinois | IL | Indiana | IN | Iowa | IA | Kansas | KS | Kentucky | KY | Louisiana | LA | Maine | ME | Maryland | MD | Massachusetts | MA | Michigan | MI | Minnesota | MN | Mississippi | MS | Missouri | MO | Montana | MT | Nebraska | NE | Nevada | NV | New Hampshire | NH | New Jersey | NJ | New Mexico | NM | New York | NY | North Carolina | NC | North Dakota | ND | Ohio | OH | Oklahoma | OK | Oregon | OR | Pennsylvania | PA | Puerto Rico | PR | Rhode Island | RI | South Carolina | SC | South Dakota | SD | Tennessee | TN | Texas | TX | Utah | UT | Vermont | VT | Virginia | VA | Washington | WA | West Virginia | WV | Wisconsin | WI | Wyoming | WY ";
	
	//store map for url agent-location 
	private HashMap<String,String> agentMap=new HashMap<String,String>();
	//store map for url address-location 
	private HashMap<String,String> addressMap=new HashMap<String,String>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		out.println("<html><head><title>Search Address or Agent According to the Url</title></head><body>");
		printInputForm(out);
		out.println("</body></html>");
		out.flush();
		out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		String url_str=request.getParameter("url");
		String action=request.getParameter("action");
		
		out.println("<html><head><title>Search Address or Agent According to the Url</title></head><body>");
		printInputForm(out);
		
		if(action.equals("GetAgent"))
			printAgentResult(url_str,out);
		else if(action.equals("GetAddress"))
			printAddressResult(url_str,out);	
			
		out.println("</body></html>");
		
	}

	private void printInputForm(PrintWriter out)
	{
		out.println("<form method=\"post\">");
		out.println("Url: <input type=\"text\" name=\"url\"><br>");
		out.println("<input type=\"submit\" name=\"action\" value=\"GetAgent\">");
		out.println("<input type=\"submit\" name=\"action\" value=\"GetAddress\">");
		out.println("</form>");
	}
	
	
	private void printAgentResult(String url_str, PrintWriter out) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		
		addressMap.put("yourkwagent.com","strong|0");
		addressMap.put("kw.com","title|0");
		addressMap.put("zillow.com","title|0");
		addressMap.put("remax.com","title|0");
		
		URL url= new URL(url_str);
		String fullhost=url.getHost();
		System.out.println("fullhost="+fullhost);
		String parts[]=fullhost.split("\\.");
		String host="";
		int n=parts.length;
		for(int i=0;i<n&&i<2;i++)
			host=parts[n-1-i]+"."+host;
		host=host.substring(0,host.length()-1);

		out.println("Possible Agent Names Are: <br>");
		if(addressMap.containsKey(host))
		{
			System.out.println("has been trained, get infor from database...");
			String[] info=addressMap.get(host).split("\\|");
			String tag=info[0];
			int index=Integer.parseInt(info[1]);

			String content = client.getContentFromUrl(url_str);
			Elements elements=Jsoup.parse(content).getElementsByTag(tag);
			int i=0;
			for (Element element : elements) {
				if(i==index)
				{
					String text=element.ownText();
					if(text!=null&&text!=""){
						String name="";
						InputStream is = new FileInputStream("D:\\workspace\\Test\\bin\\en-ner-person.bin");
						TokenNameFinderModel model = new TokenNameFinderModel(is);
						is.close();
						NameFinderME nameFinder = new NameFinderME(model);
						String[] words=text.split(" ");
						Span nameSpans[] = nameFinder.find(words);
						for(Span s: nameSpans)
						{
							System.out.println(s);
							int start=s.getStart();
							int end=s.getEnd();
							if(end-start>0)
							{
								out.println(words[start]+" "+words[start+1]+"<br>");
								System.out.println(words[start]+" "+words[start+1]+"<br>");
							}
						}
					
					System.out.println("name: "+text);
					out.println("name: "+text+"<br>");
					
					break;
					}
				}
			}
		}
		else
		{
			ArrayList<String> possibleAgentNames = dataProcessForAgent(url_str);
			for(String s:possibleAgentNames)
				out.println(s+"<br>");
		}
	}
	
	private void printAddressResult(String url_str, PrintWriter out) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		ArrayList<String> possibleAddressNames=dataProcessForAddress(url_str);
		out.println("Possible Addresses are: <br>");
		for(String s:possibleAddressNames)
			out.println(s+"<br>");
	}
	
	private ArrayList<String> dataProcessForAgent(String url_str) throws ClientProtocolException, IOException{
		// TODO Auto-generated method stub
		
		ArrayList<String> possibleAddressNames = dataProcessForAddress(url_str);
		
		ArrayList<String> possibleAgentNames = new ArrayList<String>();
		
		String content = client.getContentFromUrl(url_str);
		
		if(content==null||content=="")
			return possibleAgentNames;
		
		//clean content for process
		String[] texts=cleanContent(content);
		//record name count
		HashMap<String,Integer> names=new HashMap<String,Integer>();
		
		//use OpenNLP to detect name
		InputStream is = new FileInputStream("D:\\workspace\\Test\\bin\\en-ner-person.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		NameFinderME nameFinder = new NameFinderME(model);
		
		for(String t:texts){
			//split
			String []words = t.split("\\s|,");

			//clean words for name: only contains A-Za-z; First Charactor is UpperCase, followes are LowerCase
			for(int i=0;i<words.length;i++)
			{
				//words[i]=words[i].replaceAll("[^A-Za-z]","");
				//words[i]=words[i].replaceAll("[0-9]","");
				if(words[i].length()>1)
					words[i]=words[i].substring(0, 1)+words[i].substring(1).toLowerCase();
			}
			
			Span nameSpans[] = nameFinder.find(words);
			
			for(Span s: nameSpans)
			{
				int start=s.getStart();
				int end=s.getEnd();
				String name="";
				for (int i=start;i<end;i++)
					name += " "+words[i];
				name=name.substring(1);
				System.out.println(name);
				if(names.containsKey(name))
				{
					int count=names.get(name);
					count++;
					names.put(name, count);
				}
				else
					names.put(name,1);
			}		
		}
	
		int max = 1;
		for (String name : names.keySet()) {
			int count = names.get(name);
			System.out.println("name: " + name + " count: " + count);
			if (count == max) {
				if(!isAddress(name, possibleAddressNames))
					possibleAgentNames.add(name);
			} else if (count > max) {		
				if(!isAddress(name, possibleAddressNames))
				{	
					max = count;
					possibleAgentNames.clear();
					possibleAgentNames.add(name);	
				}
			}
		}
		
		System.out.println("max count: " + max);
		for(String s:possibleAgentNames)
			System.out.println(s);
		
		return possibleAgentNames;
	}
	
	private boolean isAddress(String name, ArrayList<String> addresses)
	{
		boolean isAddress=false;
		for(String a : addresses)
			if (a.contains(name))isAddress=true;
		return isAddress;
	}
	
	private ArrayList<String> dataProcessForAddress(String url_str) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		
		ArrayList<String> possibleAddressNames = new ArrayList<String>();
		
		String content = client.getContentFromUrl(url_str);
		
		if(content==null||content=="")
			return possibleAddressNames;
		
		//clean content for process
		String[] texts=cleanContent(content);
		//record count
		HashMap<String,Integer> addresses=new HashMap<String,Integer>();
		
		String regex=states;
		Pattern pattern=Pattern.compile(regex);
		
		for(int i=0;i<texts.length;i++)
		{
			Matcher matcher=pattern.matcher(texts[i]);
			
			while(matcher.find()) {	
				String address="";
				//add state
				String state=matcher.group(0).trim();
				address+=state;
				
				//get details
				texts[i]=texts[i].replaceAll("\\s", " ");
                String[] words=texts[i].split(" ");
                int midIndex = Arrays.asList(words).indexOf(state);
                //add zip
				if(midIndex+1<words.length&&words[midIndex+1].matches("[\\d]{5}"))
					address=address+" "+words[midIndex+1];
				//add pre
				for(int j=midIndex-1;j>=0;j--)
				{
					if(words[j].matches("[A-Z0-9].*"))
						address=words[j]+" "+address;
					else
						break;
				}
				
				System.out.println("address: " +address );
				System.out.println("text: " +texts[i]);
				
				//put this address to candidate;
				if(addresses.containsKey(address))
				{
					int count=addresses.get(address);
					count++;
					addresses.put(address, count);
				}
				else
					addresses.put(address,1);
			}
		}
		
		int max = 1;
		for (String address : addresses.keySet()) {
			int count = addresses.get(address);
			System.out.println("address: " + address + " count: " + count);
			if (count == max) {
				possibleAddressNames.add(address);
			} else if (count > max) {		
				max = count;
				possibleAddressNames.clear();
				possibleAddressNames.add(address);	
			}
		}
		
		System.out.println("max count: " + max);
		for(String s:possibleAddressNames)
			System.out.println(s);
		
		return possibleAddressNames;
	}

	private String[] cleanContent(String content) {
		// TODO Auto-generated method stub
		ArrayList<String> texts=new ArrayList<String>();
		Elements elements=Jsoup.parse(content).getAllElements();
		for (Element element : elements) {
			String text=element.ownText();
			if(text!=null&&text!=""){
				//System.out.println(text);
		    	texts.add(text+" ");
			}
		}
		return texts.toArray(new String[texts.size()]);
	} 
	
}