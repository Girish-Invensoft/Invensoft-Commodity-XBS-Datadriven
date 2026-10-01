package utils;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**   Resolves date expressions used in Gherkin step text into LocalDate values.
 *    Supported formats:
 *   "today"        -> LocalDate.now()
 *   "today+7"       -> LocalDate.now().plusDays(7)
 *   "today-7"       -> LocalDate.now().minusDays(7)
 *   "2026-09-16"    -> LocalDate.parse(...) literal ISO date, unchanged behavior    */
public class DateExpressionResolver {

    // Case-insensitive so "Today", "TODAY+7" etc. also work from Gherkin text.
    private static final Pattern RELATIVE_PATTERN =
            Pattern.compile("^today\\s*([+-]\\s*\\d+)?$", Pattern.CASE_INSENSITIVE);

    private DateExpressionResolver() {
        // static utility, no instances
    }

    public static LocalDate resolve(String expression) {
        String expr = expression.trim();
        Matcher matcher = RELATIVE_PATTERN.matcher(expr);

        if (matcher.matches()) {
            LocalDate today = LocalDate.now();
            String offsetGroup = matcher.group(1);
            if (offsetGroup == null) {
                return today;
            }
            int offset = Integer.parseInt(offsetGroup.replaceAll("\\s+", ""));
            return today.plusDays(offset); // negative offsets subtract naturally
        }

        // Not a relative expression — fall back to literal ISO date, same as before.
        // This keeps existing scenarios with hardcoded dates working unchanged.
        return LocalDate.parse(expr);
    }
}