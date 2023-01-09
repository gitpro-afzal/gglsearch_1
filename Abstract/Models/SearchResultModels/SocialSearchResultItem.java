package Abstract.Models.SearchResultModels;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialSearchResultItem extends RegularSearchResultItem {
    private static Pattern followerPattern = Pattern.compile("(.+?)([0-9]+[,.]?[0-9]+)([kKmM]? [fF]ollowers)(.*)");
    private double followers;
    private String unit;

    public SocialSearchResultItem(String mainHeader, String link, String description) {
        super(mainHeader, link, description);
        Pair<Double, String> follow = extractFollowers();
        this.followers = follow.getKey();
        this.unit = follow.getValue();
    }

    public double getFollowers() {
        return followers;
    }

    public String getUnit() {
        return unit;
    }

    public Pair<Double, String> extractFollowers() {
        Pair<Double, String> result = Pair.of(0d, "Follower");
        Matcher matcher = followerPattern.matcher("r" + getDescription());
        if (matcher.matches()) {
            String followers = matcher.group(2);
            Pair<Double, String> follower = Pair.of(Double.valueOf(followers.replace(",", "")), matcher.group(3).trim());
            if ("Followers".equalsIgnoreCase(follower.getValue().trim())) {
                result = Pair.of(follower.getKey() / 1000, "k Followers");
            } else {
                result = follower;
            }
        }
        this.followers = result.getKey();
        this.unit = result.getValue();
        return result;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLink());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Objects.equals(getLink(), ((SocialSearchResultItem) o).getLink());
    }
}
