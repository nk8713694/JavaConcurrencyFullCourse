package threadSafety;

import java.util.ArrayList;
import java.util.List;

public final class ImmutableUser {

    private final Integer userId;

    private final String name;

    private final List<String> preferences;

    public ImmutableUser(Integer userId, String name, List<String> preferences) {
        this.userId = userId;
        this.name = name;
        this.preferences = preferences;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<String> getPreferences() {
        return new ArrayList<>(preferences);
    }

    @Override
    public String toString() {
        return "ImmutableUser{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", preferences=" + preferences +
                '}';
    }
}
