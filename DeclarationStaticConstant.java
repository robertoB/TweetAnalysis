

import javax.swing.JOptionPane;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public abstract class DeclarationStaticConstant {
	protected final static String CONSUMER_KEY = "";//ask for a key

	protected final static String CONSUMER_KEY_SECRET = "";//ask for a key

	protected final static String ACCESS_TOKEN_KEY ="";//ask for a key

	protected final static String ACCESS_TOKEN_SECRET_KEY = "";//ask for a key

	protected final static int NUMBER_OF_TWEETS=100;

	protected final static String QUERY=JOptionPane.showInputDialog("Inserisci l'artista:")+" "+JOptionPane.showInputDialog("Inserisci il nome dell'album:");

	protected final static String OUTPUT_PATH_TWEETS= "QueryResults/Output_Tweets.txt";
	protected final static String VOTES= "QueryResults/Votes.txt";

	protected final static double MAXSIMILARITY= 0.90;

	protected final static int MAXROUNDTRIPS=1;
	protected final static StanfordCoreNLP pipeline= new StanfordCoreNLP("Sentiment.properties");
	protected final static String CHARSET="UTF-8";

}
