import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command {
    REGISTER("^register:(?<id>-?\\d+):(?<name>\\S+):(?<money>\\d+)$"),
    LOGIN("^login:(?<id>-?\\d+)$"),
    LOGOUT("logout"),
    GET_PRICE("^get price:(?<shoeName>\\S+)$"),
    GET_QUANTITY("^get quantity:(?<shoeName>\\S+)$"),
    GET_MONEY("^get money$"),
    CHARGE_MONEY("^charge:(?<money>\\d+)$"),
    PURCHASE_SHOES("^purchase:(?<shoeName>\\S+):(?<quantity>\\d+)$"),
    ;


    final String regex;

    Command(String regex) {
        this.regex = regex;
    }

    public Matcher getMatcher(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        matcher.matches();
        return matcher;
    }

    public boolean matches(String input) {
        return input.matches(regex);
    }

    public String getGroup(String input, String group) {
        return getMatcher(input).group(group);
    }
}