
## crawler

- user agent

- Save to frontier: discover-or-not + robotstxt * + filtre * --> get link to save into frontier (csv) 
  - apply retry * when: connection time out or socket time out
  - when error 5.x.x or 4.x.x: just log them and we may try later again
  - use multi thread * if possible

- From frontier to elastic: save data from frontier into elastic-status-index
  - use rpex functionalities

- fetcher + parser + save content: use links within csv or elastic status to fetch content from and save it back to elastic-content-index
  - use selenium headless: if needed to get content
  - may be nice to have a pojo representing a content format to get from within each link(page)
  - use rpex functionalities
  - use caching to avoid re download the same content
  - use session and cookies if login is required
  - use browser context to simulate different sessions.
  - avoid detection: delay between actions


