package Abstract.Models;

public class FindxRequestData extends RequestData {
    public FindxRequestData(String requestURL, int attemptsCount, int requestDelay) {
        super(requestURL, attemptsCount, requestDelay);
    }

    public FindxRequestData(RequestData requestData) {
        super(requestData.requestURL, requestData.attemptsCount, requestData.requestDelay);
        this.setRequestTerm(requestData.getRequestTerm().replaceAll("%22","").replaceAll("\\+\\+","\\+"));
    }

    @Override
    public String getNextPageQuery() {
        return String.format("?q=%s&p=%d&_pjax=%s", getRequestTerm(), ++page,"%23search-content");
    }
}
