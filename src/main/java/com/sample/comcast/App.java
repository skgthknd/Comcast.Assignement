package com.sample.comcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Hello world!
 *
 */
@SuppressWarnings("deprecation")
public class App {
	public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException {
		handleBranches();
		handleTags();
	}

	private static void handleBranches()
			throws URISyntaxException, IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		URI branchesUrl = new URI("https://api.github.com/repos/skgthknd/Comcast.Assignement/branches");
		String branches = urlGet(branchesUrl).toString();
		ArrayNode jsonNode = new ObjectMapper().readValue(branches, ArrayNode.class);

		List<String> branchList = new ArrayList<String>();
		List<String> containList = new ArrayList<String>();
		for (int i = 0; i < jsonNode.size(); i++) {
			String string = jsonNode.get(i).get("name").asText();
			branchList.add(string);
			String tree = getCommitUrl(branchesUrl, string);
			String data = urlGet(new URI(tree)).toString();
			if (!data.contains("travis.xml")) {
				containList.add(string);
			}
		}
		System.out.println("Printing all branches");
		System.out.println(branchList);
		System.out.println("Printing all Travis branches");
		System.out.println(containList);
	}

	private static void handleTags()
			throws URISyntaxException, IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		URI tagsUrl = new URI("https://api.github.com/repos/skgthknd/Comcast.Assignement/tags");
		String tags = urlGet(tagsUrl).toString();
		ArrayNode jsonNode = new ObjectMapper().readValue(tags, ArrayNode.class);

		List<String> tagsList = new ArrayList<String>();
		List<String> containsList = new ArrayList<String>();
		for (int i = 0; i < jsonNode.size(); i++) {
			String string = jsonNode.get(i).get("name").asText();
			tagsList.add(string);
			String tree = jsonNode.get(i).get("commit").get("url").asText();
			String data = urlGet(new URI(tree)).toString();
			if (!data.contains("travis.xml")) {
				containsList.add(string);
			}
		}
		System.out.println("Printing all Tags");
		System.out.println(tagsList);
		System.out.println("Printing all Tags with Travis");
		System.out.println(containsList);
	}

	static String getCommitUrl(URI branchesUrl, String string)
			throws IOException, ClientProtocolException, URISyntaxException, JsonParseException, JsonMappingException {
		String branchData = urlGet(new URI(branchesUrl.toString() + "/" + string)).toString();
		JsonNode node = new ObjectMapper().readValue(branchData, JsonNode.class);
		String tree = node.get("commit").get("commit").get("tree").get("url").asText();
		return tree;
	}

	static StringBuilder urlGet(URI branches) throws IOException, ClientProtocolException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(branches);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(httpResponse.getEntity().getContent()));
		String str = "";
		StringBuilder response = new StringBuilder();
		while ((str = bufferedReader.readLine()) != null) {
			response.append(str);
		}
		httpClient = null;
		return response;
	}
}
