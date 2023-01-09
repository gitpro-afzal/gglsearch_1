package Abstract.Models.OutputModels;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class OutputInstaFollowerCSVItem implements IOutputModel {

    @CsvBindByPosition(position = 0)
    private String instaAccount;

    @CsvBindByPosition(position = 1) //Number of followers
    private String followers;

    @CsvBindByPosition(position = 2) //Html page title
    private String htmlPageTitle;

    @CsvBindByPosition(position = 3) //Not Found
    private String notFound;

    @CsvBindByPosition(position = 4) //Description
    private String description;
    @CsvBindByPosition(position = 5) //Instagram bios
    private String instagramBios = "";


    public OutputInstaFollowerCSVItem(String instaAccount, String followers, String htmlPageTitle, String notFound, String description) {
        this.instaAccount = instaAccount;
        this.followers = followers;
        this.htmlPageTitle = htmlPageTitle;
        this.notFound = notFound;
        this.description = description;
    }

    public String getInstagramBios() {
        return instagramBios;
    }

    public void setInstagramBios(String instagramBios) {
        this.instagramBios = instagramBios;
    }

    @Override
    public String toCsvRowString() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", htmlPageTitle, instaAccount, followers, notFound, description, instagramBios);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputInstaFollowerCSVItem that = (OutputInstaFollowerCSVItem) o;
        return Objects.equals(instaAccount, that.instaAccount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(instaAccount);
    }
}
