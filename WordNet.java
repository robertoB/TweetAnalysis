

import java.io.File;
import java.util.ArrayList;

import rita.wordnet.RiWordnet;

public class WordNet implements AddFeature {
	private RiWordnet wordnet;
	private String [] bagofwords;
	private ArrayList<String> queryarray;
	private String querystring="";
	
	public WordNet(String [] bagofwords) {
		this.bagofwords=bagofwords;
		File f=new File("WordNet-3.0");
		wordnet = new RiWordnet(null,f.getAbsolutePath());
		queryarray=new ArrayList<String>();
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		for(String word : bagofwords){	
			
		   String posStr=wordnet.getBestPos(word);
		   String[] AllSynonims=  wordnet.getAllSynonyms(word, posStr);
		   String [] Synonims=wordnet.getSynonyms(word, posStr);
		   ArrayList<String> ArrSynonims=new ArrayList<String>();
		   
		   
		   for(String synonim:Synonims){
			   ArrSynonims.add(synonim);			   
		   }
		 
		   
		   for(String synonim:AllSynonims){
			   if(ArrSynonims.isEmpty() || ArrSynonims.contains(synonim)){
				   queryarray.add("("+synonim+")");
			
				   
			   }
			   
		   }

	
		}
		
		
		for (int x=0,len=queryarray.size();x<len;x++){
			querystring+= " OR "+queryarray.get(x);
//			if(x<len-1){
//				querystring+=" OR ";
//			}
			
		}
		
	}
	
	public String getString(){
		
		return querystring;
	}
	

}
