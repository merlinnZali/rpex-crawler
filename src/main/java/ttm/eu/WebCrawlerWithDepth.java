package ttm.eu;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
public class WebCrawlerWithDepth {
    // set this from property file
    private static final String userAgent = "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)";
    // set this from property file
    private static final int MAX_DEPTH = 2;

    /**
     *
     * @param url base url string
     * @param depth depth integer
     * @param visitedLinks discoveredLink (document fetched) ...string
     * @param nonDiscoveredLinks nonDiscoveredLink ...string
     * @param datalist frontier ...string[]
     */
    public void getPageLinks(String url, int depth, Set<Link> visitedLinks, Set<Link> nonDiscoveredLinks, List<String[]> datalist) {
        if (depth < MAX_DEPTH) {
            log.info(">> Depth:  {} [ {}  ]", depth, url);
            Document document = request(url, depth, visitedLinks, nonDiscoveredLinks);
            if(document != null){
                depth++;
                String title = document.title();
                log.info("Doc title {} for url: {}", title, url);

                /*int docid = document.getWebURL().getDocid();
                String url = page.getWebURL().getURL();
                String domain = page.getWebURL().getDomain();
                String path = page.getWebURL().getPath();
                String subDomain = page.getWebURL().getSubDomain();
                String parentUrl = page.getWebURL().getParentUrl();
                String anchor = page.getWebURL().getAnchor();*/

                String nextLinkString = "";
                String linkText = "";
                for (Element link : document.select("a[href]")) {
                    nextLinkString = link.attr("abs:href");
                    //nextLinkString = link.absUrl("href");
                    linkText = link.text();
                    if(StringUtils.isNoneBlank(linkText)){
                        log.info("Text {} for url: {}", linkText, nextLinkString);
                    }

                    Link nextLink = Link.builder().url(nextLinkString).build();
                    if(nextLinkString.startsWith("http") && !visitedLinks.contains(nextLink)/*TODO add shoulbevisited filter + robotstxt*/){
                        // save into csv frontier
                        datalist.add(new String[]{url, linkText, nextLinkString});
                        getPageLinks(nextLinkString, depth, visitedLinks, nonDiscoveredLinks, datalist);
                    }
                }
            }
        }
    }

    private static Document request(String url, int depth, Set<Link> visitedLinks, Set<Link> nonDiscoveredLinks) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .followRedirects(true)
                    .header("Accept-Language", "*")
                    .timeout(5 * 1000)
                    .execute();

            if(response.statusCode() == 200) {
                Link nextLink = Link.builder().url(url).depth(depth).discovered(true).status(response.statusCode()).build();
                visitedLinks.add(nextLink);
                return response.parse();
            } else {
                Link nonDiscoveredLink = Link.builder().url(url).depth(depth).discovered(false).status(response.statusCode()).build();
                nonDiscoveredLinks.add(nonDiscoveredLink);
                log.error("Error:  {} {} ", response.statusCode(), response.statusMessage());
                return null;
            }

        } catch (HttpStatusException e) {
            log.error("Error status  {} {} - {}", url, e.getMessage(), e.toString());
            Link nonDiscoveredLink = Link.builder().url(url).depth(depth).discovered(false).status(e.getStatusCode()).build();
            nonDiscoveredLinks.add(nonDiscoveredLink);
            return null;
        } catch (IOException e) {
            log.error("Error:For  {} {} - {}", url, e.getMessage(), e.toString());
            Link nonDiscoveredLink;
            //Read timed out or Connect timed out
            if(StringUtils.contains(e.getMessage(),"out")){
                nonDiscoveredLink = Link.builder().url(url).depth(depth).discovered(false).status(-1).build();
            } else  {
                nonDiscoveredLink = Link.builder().url(url).depth(depth).discovered(false).status(500).build();
                e.printStackTrace();
            }
            nonDiscoveredLinks.add(nonDiscoveredLink);
            return null;
        }
    }
}
