package Abstract.Models.OutputModels;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class OutputLinkedinCSVItem implements IOutputModel {

    @CsvBindByPosition(position = 0)
    private String linkedinAccount;

    @CsvBindByPosition(position = 1) //Number of followers
    private String roles;

    @CsvBindByPosition(position = 2) //Html page title
    private String htmlPageTitle;

    @CsvBindByPosition(position = 3) //Not Found
    private String notFound;

    @CsvBindByPosition(position = 4) //Description
    private String description;

    public OutputLinkedinCSVItem(String linkedinAccount, String roles, String htmlPageTitle, String notFound, String description) {
        this.linkedinAccount = linkedinAccount;
        this.roles = roles;
        this.htmlPageTitle = htmlPageTitle;
        this.notFound = notFound;
        this.description = description;
    }


    @Override
    public String toCsvRowString() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", htmlPageTitle, linkedinAccount, roles, notFound, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputLinkedinCSVItem that = (OutputLinkedinCSVItem) o;
        return Objects.equals(linkedinAccount, that.linkedinAccount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(linkedinAccount);
    }
}
