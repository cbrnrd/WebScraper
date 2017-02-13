package crawlbabyGUI;

import java.util.List;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkExtractor
{
  
  public final static void main(String[] args) throws Exception
  {
    
    String site = "http://973-eht-namuh-973.com/";
    List<String> links = LinkExtractor.extractLinks(site);
    for (String link : links)
    {
      
    }
  }
  
  public static List<String> extractLinks(String url) throws Exception
  {
    
    final ArrayList<String> result = new ArrayList<String>();
    Document doc = Jsoup.connect(url).get();
    
    Elements links = doc.select("a[href]");
    String text = doc.body().text();
    String linkText = links.text();
    
    //
    String[] ary = text.split(" ");
    //Arrays.asList(asy);
    //System.out.println(ary[0]);
    for (Element link : links)
    {
      result.add(link.attr("abs:href"));
    }
    return result;
  }
}
