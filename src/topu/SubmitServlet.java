package topu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import utils.EnvironmentProperty;

public class SubmitServlet extends HttpServlet {

	private static final long serialVersionUID = 1234538090976492241L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpClient httpClient = login();
		HttpPost httpPost = new HttpPost(EnvironmentProperty.get("submitUrl"));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("source", ""));
		nameValuePairs.add(new BasicNameValuePair("contestId", "3778"));
		nameValuePairs.add(new BasicNameValuePair("problemNumber", "1"));
		nameValuePairs.add(new BasicNameValuePair("language", "G++"));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		httpPost.setEntity(entity);
		HttpResponse resp = httpClient.execute(httpPost);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String reply = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			reply += line;
		}
		System.out.println("submit response : " + reply);
		
		JSONObject jsonObj = JSONObject.fromObject(reply);
		if("ERROR".equals(jsonObj.getString("result"))) {
			String msg = jsonObj.getString("message");
			System.out.println(unicodeToString(msg));
		} else {
			String redirectUrl = jsonObj.getString("redirect");
			String html = HtmlParser.getOnePage(redirectUrl, httpClient);
			String ojStatus = HtmlParser.parseStatus(html);
			System.out.println(ojStatus);
			
			// update submitStatus to TopU
			// response to POP
			
		}
	}

	public HttpClient login() throws ServletException, IOException {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(EnvironmentProperty.get("loginUrl"));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("email", EnvironmentProperty.get("email")));
		nameValuePairs.add(new BasicNameValuePair("password", EnvironmentProperty.get("password")));
		nameValuePairs.add(new BasicNameValuePair("redirectUrl", EnvironmentProperty.get("redirectUrl")));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
		httpPost.setEntity(entity);
		HttpResponse resp = httpClient.execute(httpPost);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String reply = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			reply += line;
		}
		System.out.println("login response : " + reply);
		return httpClient;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		String str = unicodeToString("\u63d0\u4ea4\u6210\u529f");
//		System.out.println(str);
		doPost(req, resp);
	}

	private String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }
	
	private void checkRequestParameters(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements()) {
			String name = e.nextElement();
			String value = request.getParameter(name);
			System.out.println(name + " : " + value);
		}
	}	
}
