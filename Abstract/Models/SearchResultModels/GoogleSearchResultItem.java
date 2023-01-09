package Abstract.Models.SearchResultModels;

import Utils.StrUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class GoogleSearchResultItem {

    public GoogleSearchResultItem(String mainHeader, String link, String description) {
        this.mainHeader = mainHeader;
        this.link = link.toLowerCase();
        this.description = description;
        try {
            URI uri = new URI(link);
            this.hostName = uri.getHost();
        } catch (URISyntaxException e) {
            // do nothing
        }
        if (this.hostName == null) {
            this.hostName = link;
        }
    }

    private Set<Metadata> metadatas = new TreeSet<>(Comparator.comparingInt(Metadata::hashCode));
    private String mainHeader;
    private String link;
    private String description;
    private String hostName;

    public String getMainHeader() {
        return mainHeader;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHostName() {
        return hostName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoogleSearchResultItem that = (GoogleSearchResultItem) o;
        if (!this.getMetadatas().isEmpty()) {
            return Objects.equals(this.getMetadatas(), that.getMetadatas());
        }
        return Objects.equals(mainHeader, that.mainHeader) &&
                Objects.equals(link, that.link) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        if (!this.getMetadatas().isEmpty()) {
            return Objects.hash(getMetadatas());
        }
        return Objects.hash(mainHeader, link, description);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Set<Metadata> getMetadatas() {
        return metadatas;
    }

    private double score;

    public static class Metadata {
        private String email;
        private String status;
        private String website;
        private String domain;
        private String header;

        public Metadata(String header, String website, String email, String status) {
            this.email = email.toLowerCase();
            this.status = status;
            this.website = website.toLowerCase();
            this.header = header;
            this.domain = StrUtils.cleanProtocolSchema(this.website);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Metadata metadata = (Metadata) o;
            return Objects.equals(domain, metadata.domain)
                    && Objects.equals(email, metadata.email)
                    && Objects.equals(status, metadata.status);
        }

        @Override
        public int hashCode() {

            return Objects.hash(domain, email, status);
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public String getWebsite() {
            return website;
        }

        public String getHeader() {
            return header;
        }
    }
}
