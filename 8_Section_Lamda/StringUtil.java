public class StringUtil {

    public static boolean isAllLowerCase(String value) {
        for (Character c : value.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return false;
            }
        }

        return true;
    }
}
