package topu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class HtmlParser {

	public static String getOnePage(String htmlUrl, HttpClient httpClient) {
		String ret = "";
		try {
			HttpGet httpGet = new HttpGet(htmlUrl);
			HttpResponse resp = httpClient.execute(httpGet);
			BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			ret = sb.toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String parseStatus(String html) {
		String mark = "result-ce";
		int startIndex = html.indexOf(mark) + mark.length() + 2;
		int endIndex = startIndex;
		while(html.charAt(endIndex) != '<') endIndex++;
		return html.substring(startIndex, endIndex);
	}
	
}
