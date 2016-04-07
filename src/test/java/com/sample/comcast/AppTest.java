package com.sample.comcast;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() throws Exception {
		handleBranches();
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
		Assert.assertTrue(branchList.contains("master"));
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
		StringBuilder response = new StringBuilder();
		String url = branches.getScheme() + "://" + branches.getHost() + branches.getPath();
		if (url.equals("https://api.github.com/repos/skgthknd/Comcast.Assignement/branches"))
			response.append("[{\"name\": \"master\"}]");
		else if (url.equals("https://api.github.com/repos/skgthknd/Comcast.Assignement/branches/master")) {
			response.append(
					"{\"name\": \"master\",\"commit\": {\"commit\": {\"tree\": {\"url\": \"https://api.github.com/repos/skgthknd/Comcast.Assignement/git/trees/d2bb8b46dfaec776a5dc51379935e86f22f776cd\"}}}}");
		}
		return response;
	}
}