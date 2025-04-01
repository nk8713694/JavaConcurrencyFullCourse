package threadSafety;

import java.util.ArrayList;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        List<String> prefs = new ArrayList<>();
        prefs.add("qadd1");
        ImmutableUser immutableUser = new ImmutableUser(1, "Lavakumar", prefs);

        immutableUser.getPreferences().add("add2");
        System.out.println(immutableUser);
    }
}
