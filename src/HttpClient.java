import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {

	public HttpClient() {

	}

	public String getContentFromUrl(String url_str) throws ClientProtocolException, IOException {
		String content = null;
		if(url_str==null||url_str=="")
			return null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url_str);
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");
		CloseableHttpResponse response = httpclient.execute(httpGet);

		String content_type = response.getFirstHeader("Content-Type").getValue();
		System.out.println("content_type \n" + content_type);

		if (content_type.contains("text/html")) {
			HttpEntity entity = response.getEntity();
			if (entity != null)
				content = EntityUtils.toString(entity);
			System.out.println("content-length: \n" + content.length());
		}
		response.close();
		return content;
	}

}
