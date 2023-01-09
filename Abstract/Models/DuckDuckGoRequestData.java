package Abstract.Models;

public class DuckDuckGoRequestData extends RequestData {


    int totalRecordFound;
    int s = 0;

    public void setRecordFound(int recordFound) {
        this.totalRecordFound += recordFound;
    }

    public DuckDuckGoRequestData(String requestURL, int attemptsCount, int requestDelay) {
        super(requestURL, attemptsCount, requestDelay);
    }

    public DuckDuckGoRequestData(RequestData requestData) {
        super(requestData.requestURL, requestData.attemptsCount, requestData.requestDelay);
        this.setRequestTerm(requestData.getRequestTerm());
    }

    @Override
    public String getNextPageQuery() {
        int currentPage = page++;
        if (currentPage == 0) {
            return "q=" + getRequestTerm()+"&kl=us-en";
        } else if (currentPage == 1) {
            s += 30;
        } else {
            s += 50;
        }
        return String.format("q=%s&s=%d&nextParams=&v=l&o=json&dc=%d&api=%2Fd.js&kl=us-en", getRequestTerm(), s, totalRecordFound + 1);
    }
}
