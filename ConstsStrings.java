package Utils;

public class ConstsStrings {
    public static final String EMAIL_REGEX = "([\\p{L}-_\\.]+)@([\\w-_\\.]+[a-z])";
    public  static final String EMAIL_STATUS_SAME_DOMAIN = "same domain";
    public  static final String EMAIL_STATUS_GUESSED_DOMAIN = "guessed domain";
    public static String RESULT_NOT_MATCHED = "Result not matched";
    public static String CONNECTION_ISSUE = "Connection Issue";
    public static String ERROR_TOO_MANY_REQUEST = "Your request failed due to sending many requests to google server from same ip address. Please try again later";
}
