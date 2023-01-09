package Abstract.Models.OutputModels;

import com.opencsv.bean.CsvBindByPosition;

public class OutputRegularCSVItem implements IOutputModel {

    @CsvBindByPosition(position = 0) // Company Name
    private String companyName;

    @CsvBindByPosition(position = 1) //Company Website
    private String website;

    @CsvBindByPosition(position = 2) //Html page title
    private String htmlPageTitle;

    @CsvBindByPosition(position = 3) //address
    private String address;

    @CsvBindByPosition(position = 4) //Html page title
    private String phone;

    @CsvBindByPosition(position = 5) //Not Found
    private String notFound;
    @CsvBindByPosition(position = 6) //Broken Link
    private String brokenLink;


    public OutputRegularCSVItem(String companyName, String webSite, String htmlPageTitle, String address, String phone, String notFound, String brokenLink) {
        this.companyName = companyName;
        this.website = webSite;
        this.htmlPageTitle = htmlPageTitle;
        this.notFound = notFound;
        this.address = address;
        this.phone = phone;
        this.brokenLink = brokenLink;
    }

    public String getCompanyName() {
        if (companyName == null) {
            return "";
        }
        return this.companyName;
    }

    public String getWebsite() {
        if (website == null) {
            return "";
        }
        return this.website;
    }

    public String getHtmlPageTittle() {
        if (htmlPageTitle == null) {
            return "";
        }
        return htmlPageTitle;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setHtmlPageTitle(String htmlPageTitle) {
        this.htmlPageTitle = htmlPageTitle;
    }

    @Override
    public String toCsvRowString() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", htmlPageTitle, website, address, phone, notFound, brokenLink);
    }

    public String getNotFound() {
        return notFound;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getBrokenLink() {
        return brokenLink;
    }
}
