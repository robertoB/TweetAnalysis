

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.*;

public class TweetPolling extends DeclarationStaticConstant {

	public void start() throws TwitterException, IOException {

		//Initialization --Start
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
		.setOAuthAccessToken(ACCESS_TOKEN_KEY)
		.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET_KEY);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		ArrayList<Query> queries=new ArrayList<Query>();
		AddFeature featurewordnetlyrics=new WordNet(new String[]{"lyrics"});
		AddFeature featurewordnetarrangement=new WordNet(new String[]{"arrangement"});
		AddFeature featurewordnetoriginality=new WordNet(new String[]{"originality"});
		FileWriter writerTweets=new FileWriter(OUTPUT_PATH_TWEETS);
		FileWriter votesresult= new FileWriter(VOTES);	
		ArrayList<Status>ListForTagMe=new ArrayList<Status>();
		double grandtotal=0;
		int numberofqueries=0;
		//Initialization -- End

		//Query Creation -- Start
			// Execute Methods
						featurewordnetlyrics.execute();
						featurewordnetarrangement.execute();
						featurewordnetoriginality.execute();
			// End Execute Methods

		queries.add(new Query(QUERY));
		queries.add(new Query(QUERY+" lyrics"+featurewordnetlyrics.getString()));
		queries.add(new Query(QUERY+" arrangement"+featurewordnetarrangement.getString()));
		queries.add(new Query(QUERY+" originality"+featurewordnetoriginality.getString()));
		queries.add(new Query(QUERY+" cover"));
		queries.add(new Query(QUERY+" video"));
		// Query Creation -- End

		
	
		for (Query query : queries){
			//Initialization -- Start
			ArrayList<Status> tweetList = new ArrayList<Status>();	
			Map<String,Integer> UserAuthority=new HashMap<String,Integer>();
			int mainSentiment = 0;
			double voto= 0;
			int totaldenom=0; 
			// Initialization -- End


			SetTweetList(tweetList, query,twitter, UserAuthority);		
			
			ListForTagMe.addAll(tweetList);
			
			for(Status tweet : tweetList) {
				int longest = 0;
				String comment="";
				byte[] tweettextt = tweet.getText().getBytes("UTF-8");
				comment = new String(tweettextt, "US-ASCII").replaceAll("[^\\p{L}\\p{Z}]","");;

				
				Annotation annotation = pipeline.process(comment);
				for (CoreMap sentence : annotation
						.get(CoreAnnotations.SentencesAnnotation.class)) {
					Tree tree = sentence
							.get(SentimentAnnotatedTree.class);
					int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
					String partText = sentence.toString();
					if (partText.length() > longest) {
						mainSentiment = sentiment;
						longest = partText.length();
					}
				}

				writerTweets.write("@" + tweet.getUser().getScreenName() + ":" +comment+ ", vote: " + mainSentiment+System.lineSeparator());	
				voto=voto+mainSentiment*UserAuthority.get(tweet.getUser().getScreenName());
				totaldenom+=UserAuthority.get(tweet.getUser().getScreenName());

			}
			
			
			if(tweetList.size()>0){
				voto= (voto/totaldenom);
				}
			
			numberofqueries++;
			grandtotal+=voto;
			votesresult.write("Query:"+query.getQuery()+System.lineSeparator());
			votesresult.write("Mean Vote:"+voto+System.lineSeparator());
			//printMap(UserAuthority);
			
		
		}
		votesresult.write("Total Mean Vote:"+(grandtotal/numberofqueries)+System.lineSeparator());
		votesresult.flush();
		writerTweets.flush();
		AddFeature featuretagme=new TagMe(ListForTagMe);
		featuretagme.execute();


	}








	public static void printMap (Map<String,Integer> map){

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey().toString();;
			Integer value = entry.getValue();
			System.out.println(entry.getKey()+"      "+entry.getValue()+ System.lineSeparator());
		} 
	}


	public double getSimilarity(String s1, String s2){
		String comparermajor="";
		String comparerminor="";
		if (s1.length()>s2.length()){
			comparermajor=s1;
			comparerminor=s2;
		}
		else{
			comparermajor=s2;
			comparerminor=s1;
		}
		double simil=0;
		for(int i=0;i<comparerminor.length();i++){
			if(comparermajor.charAt(i)==comparerminor.charAt(i))
				simil++;
		}
		double ratio=0.0;
		if(simil>0){
			ratio=simil/comparermajor.length();

		}
		return ratio;

	}

	

	public void SetTweetList(ArrayList<Status> tweetList, Query query, Twitter twitter, Map<String,Integer>UserAuthority) throws IOException{
		
		long lastID = Long.MAX_VALUE;
		ArrayList<Status> tweets = new ArrayList<Status>();


		


		ArrayList<String>alreadytweeted=new ArrayList<String>();
		
		int roundtrips=0;


		while (tweets.size() < NUMBER_OF_TWEETS && roundtrips<MAXROUNDTRIPS) {
			roundtrips++;
			if (NUMBER_OF_TWEETS - tweets.size() > 100)
				query.setCount(100);
			else
				query.setCount(NUMBER_OF_TWEETS - tweets.size());
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());

				for (Status status : tweets){
					boolean totweet=true;
					String towrite="@" + status.getUser().getScreenName() + ":" +status.getText();
					for(int x=0; x<alreadytweeted.size();x++){
						if(getSimilarity(towrite,alreadytweeted.get(x))>MAXSIMILARITY){

							totweet=false;
							break;
						}
					}
					if(totweet){
						if(!UserAuthority.containsKey(status.getUser().getScreenName()) && !status.isRetweet()){
							UserAuthority.put(status.getUser().getScreenName(),1);
						}else if(status.isRetweet() && !UserAuthority.containsKey(status.getRetweetedStatus().getUser().getScreenName()) ){
							UserAuthority.put(status.getRetweetedStatus().getUser().getScreenName(),1);

						}
						if(status.getUser().getLang().equals("en")) {
							if(status.isRetweet()){

								Integer val=UserAuthority.get(status.getRetweetedStatus().getUser().getScreenName());
								val++;
								UserAuthority.put(status.getRetweetedStatus().getUser().getScreenName(), val);
							}
							else{
					
								tweetList.add(status);
								alreadytweeted.add(towrite);
							}
						}

					}

					if (status.getId() < lastID)
						lastID = status.getId();
				}

			}

			catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te);
			}

			query.setMaxId(lastID - 1);
		}


	}
	public static void main(String[] args) throws Exception {

		new TweetPolling().start();// run the Twitter client

	}
}
