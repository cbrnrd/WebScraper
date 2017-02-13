package crawlbabyGUI;


import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.*;

public class lister
{
  int en;
  Boolean running = true;
  Map<String, Integer> urlMap = new HashMap<String, Integer>();
  List<String> bread;
  Set<String> visited = new HashSet<String>();
  
  public lister(){
    bread = new ArrayList<String>();
  }
  
  public int findWord (String[] words, int depth) throws Throwable
  {
    URL url = new URL(bread.remove(0));
    
    
    
    if (visited.contains(url.getPath()))
    {
      return -3;
    }
    else
    {
      visited.add(url.getPath());
    }
    
    GuiStreams.out.print("Looking for word " );
    for( int rnd = 0; rnd < words.length; rnd++){
      GuiStreams.out.print(words[rnd] + " ");
    }
    
    GuiStreams.out.println( "at depth " + depth + " on page " + url);
    
    @SuppressWarnings("resource")
	Scanner s = new Scanner("Hello");
    try{
      s = new Scanner(url.openStream());
    }catch(Exception e){
      return -3; 
    }
    
    // Read token by token, place it in aToken
    while (s.hasNext())
    {
      
      String aToken = s.next();
      aToken = aToken.toLowerCase();
      
      
      if (en == words.length){
        GuiStreams.out.print("Found "); 
        for (int rnd = 0; rnd < words.length; rnd++){
          GuiStreams.out.print(words[rnd] + " ");
          
        }
        GuiStreams.out.println( "on " + url);
        return depth;
      }else{
        
        if(words[en].equals(aToken)){
          en++;
        }else{
          en = 0;
        }
      }
    }
    
    // If we reach here, we need to branch
    try
    {
      // Collect urls on page
      List<String> neighbors = LinkExtractor.extractLinks(url.toString());
      for (String sUrl : neighbors)
      {
        bread.add(sUrl);
        urlMap.put(sUrl, urlMap.get(url.toString()));
      }
      // Search them
      if (urlMap.get(url.toString()) > 9)
      {
        return -2;
      }
    }
    catch (Exception e)
    {
      GuiStreams.out.println("Bad link " + url + ": " + e);
      
    }
    
    // Error scenario in which we couldn't find the word
    return -1;
    
  }
}
