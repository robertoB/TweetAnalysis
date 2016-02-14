
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.*;


public class TagMe implements AddFeature {

	FileWriter writerTweets;
	ArrayList<Status> tweets=new ArrayList<Status>();
	URL url ;
	String charset = "UTF-8";
	String param1name = "text";
	String param2name = "key";
	String param2value = ""; //first to use TagMe ask for a temporary key sending an email to tagme@di.unipi.it
	String param3name = "include_categories";
	String param3value = "true";	
	String param4name = "tweet";
	String param4value = "true";

	public TagMe(ArrayList<Status>tweets){
		try{
			writerTweets=new FileWriter("QueryResults/Output_TagMe.txt");
			url=new URL ("http://tagme.di.unipi.it/tag");
			this.tweets=tweets;
		}catch(Exception e){
			e.printStackTrace();
		}

	}



	@Override
	public void execute() {
		// TODO Auto-generated method stub
		//config TAGME request parameters
		try{
			HashMap<String,Integer> CounterOfTopics=new HashMap<String,Integer>();
			for(Status tweet:tweets){
				String param1value = tweet.getText();
				//	writerTweets.write(param1value+System.lineSeparator());
				String query = String.format("%s=%s&%s=%s&%s=%s", URLEncoder.encode(param1name, charset), URLEncoder.encode(param1value, charset), URLEncoder.encode(param2name, charset), URLEncoder.encode(param2value, charset), URLEncoder.encode(param3name, charset), URLEncoder.encode(param3value, charset), URLEncoder.encode(param4name, charset), URLEncoder.encode(param4value, charset));

				//open TAGME connection
				HttpURLConnection connessione = (HttpURLConnection) url.openConnection();
				connessione.setRequestMethod("POST");
				connessione.setDoOutput(true);

				//get TAGME response
				OutputStream output = null;
				try {
					output = connessione.getOutputStream();
					output.write(query.getBytes(charset));
				} 
				finally {
					if (output != null) try { output.close(); } catch (IOException err){}			
				}

				//read TAGME response
				int rspCode = connessione.getResponseCode();
				String messaggio = connessione.getResponseMessage(); 
				String contenttype = connessione.getContentType();	
				String responsetag = "";
				if (contenttype.contains("application/json")){		
					Scanner input = new Scanner(connessione.getInputStream());		
					while (input.hasNextLine()){
						String arrayinput[]=input.nextLine().split(",");
						for(String tag:arrayinput){
							if(tag.contains("\"title\"")){
								String topic=tag.split(":")[1];
								if(!CounterOfTopics.containsKey(topic.trim())){
									CounterOfTopics.put(topic.trim(), 1);
								}
								else{
									Integer numberofoccurences=CounterOfTopics.get(topic.trim());
									numberofoccurences++;
									CounterOfTopics.put(topic.trim(),numberofoccurences);
								}
								
							}
						}
					}
					input.close();		
				}
				
			
			}
			
			for (Map.Entry<String, Integer> entry : CounterOfTopics.entrySet()) {
				String key = entry.getKey().toString();;
				Integer value = entry.getValue();
				writerTweets.write("topic:"+key+"  occurences:"+value+System.lineSeparator());
			}
			
			writerTweets.flush();

		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

}
