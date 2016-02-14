# TweetAnalysis

The Project is a simple tool for tweet analisys (using Twitter4J libraries,http://twitter4j.org/en/index.html).
It aims to study the sentiment of the comments founded on Twitter in the musical domain. 
Running the program it will be shown a pane for submitting a query (Author and Album).
The Query is automatically enriched with some key words in order to find more detailed comments.
It's possible to use WordNet (https://wordnet.princeton.edu/) in order to find synonims of some key words used for the query.
Each comment is evaluated using Standford libraries for sentiment analysis(http://nlp.stanford.edu/sentiment/code.html), and for each key word it's calculated the weighted media of the resuling votes giving higher weight to users whose comments have been retweeted.
Furthermore each comment can be processed using TagMe(http://tagme.di.unipi.it/) in order to find how many times a topic will be founded.

In order to use the project:
- Include the java files into the relevant workspace
- Download the libraries requested (http://twitter4j.org/en/index.html,https://wordnet.princeton.edu/,http://nlp.stanford.edu/sentiment/code.html)
- Create a WordNet Database Folder and insert int into the folder of the project (https://wordnet.princeton.edu/).
- Insert into the class DeclarationStaticConstant the keyvalue obtained from twitter.
- Run the main class TweetPolling
- Select Artist Name
- Select Album Name 
- The Reults are shown into the folder Query Results
- In order to use WordNet or TagMe uncomment the execute() methods; first to use TagMe ask for a temporary key sending an email to tagme@di.unipi.it.
- Using the Class DeclarationStaticConstant it's possible to change some settings (for example max tweet per query)
