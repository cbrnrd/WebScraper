package crawlbabyGUI;

import java.net.URL;
import java.util.List;
import java.util.Scanner;


public class crawl
{
  
  List<String> bread;
  static boolean running = true;
  
  @SuppressWarnings({ "unused", "resource" })
public static void main(String[] args) throws Throwable
  {
    lister list = new lister();
    
    // Makes scanner
	Scanner usrIn = new Scanner(GuiStreams.in);
    
    // Reads in url from keyboard
    GuiStreams.out.println("Put in a URL");
    String sUrl = usrIn.nextLine();
    list.urlMap.put(sUrl, 0);
    
    // Reads in word from keyboard
    GuiStreams.out.println("Put in a word");
    String word = usrIn.nextLine().toLowerCase();
    LinkExtractor.extractLinks(sUrl);
    String[] words = word.split(" ");
    
    // makes url obj from sUrl string
    URL link = new URL(sUrl);
    int depth = 0;
    list.bread.add(sUrl);
    int numUrl = 0;
    while(running){
      
      depth = list.findWord(words, numUrl);
      numUrl = numUrl + 1; 
      
      if(depth == -3){
        depth = -1;
        numUrl--;
      }
      
      if(depth != -1){
        running = false;
        
      }
    }
    if(depth >= 0){
      GuiStreams.out.println("Number of pages: " + depth);
    }else{
      GuiStreams.out.println("Not able to find " + word + " after " + numUrl + " searches");
    }
    
    
  }
  
}
