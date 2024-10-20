package ttm.eu;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Log4j2
public class WebCrawler {
    // set this from property file
    private static final String userAgent = "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)";
    // set this from property file
    private static final int TOTAL_PAGE = 100; // total AllVisitedPages

    /**
     *
     * @param startLink base URL string
     * @param visitedUrls discoveredLink and nonDiscoveredLink ...string
     * @param allVisitedPages link where document where successfully fetched ...string
     */
    public void crawl(String startLink, Set<String> visitedUrls, Set<String> allVisitedPages, List<String[]> datalist){
        /*
         Set<String> visitedUrls = new HashSet<>();
         Set<String> AllVisitedPages = new HashSet<>();
        */
        Queue<String> queue = new LinkedList<>();  // will contains start url and next link to proceed
        queue.add(startLink);

        while (!queue.isEmpty() && allVisitedPages.size() <= TOTAL_PAGE) {
            String currentUrl = queue.poll();
            if(visitedUrls.contains(currentUrl)) {
                continue;
            }
            Document document = null;
            try {
                Connection.Response response = Jsoup.connect(currentUrl)
                        .userAgent(userAgent)
                        .followRedirects(true)
                        .header("Accept-Language", "*")
                        .timeout(5 * 1000)
                        .execute();
                if(response.statusCode() == 200) {
                    document = response.parse();
                } else {
                    visitedUrls.add(currentUrl);
                    log.error("Error status :  {} {} ", response.statusCode(), response.statusMessage());
                }
            } catch (HttpStatusException e) {
                visitedUrls.add(currentUrl);
                log.error("Error status : For  {} {} - {}", currentUrl, e.getMessage(), e.toString());
            } catch (IOException e) {
                if(StringUtils.contains(e.getMessage(),"out")){
                    //Read timed out or Connect timed out
                    visitedUrls.add(currentUrl); //with status -1
                } else  {
                    visitedUrls.add(currentUrl); //with status 500
                    e.printStackTrace();
                }
                log.error("Error:  {} {} - {}", currentUrl, e.getMessage(), e.toString());
            }
            if(document != null) {
                allVisitedPages.add(currentUrl);
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    String linkText = link.text();
                    String nextLinkString = link.attr("abs:href");
                    // save into csv frontier
                    datalist.add(new String[]{currentUrl, linkText, nextLinkString});
                    if(nextLinkString.startsWith("http") && !visitedUrls.contains(nextLinkString)/*TODO add shoulbevisited filter + robotstxt*/){
                        queue.add(nextLinkString);
                    }
                }
                visitedUrls.add(currentUrl);
            }
        }

        Util.saveToCsv(datalist, "frontier2");
    }
}
