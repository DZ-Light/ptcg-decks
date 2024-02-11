package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class RegexTest {
    @Test
    void regexTest() {
        matchCard("4 Battle VIP Pass FST 225 PH");
        matchCard("8 Darkness Energy 7");
    }


    private static void matchCard(String string) {

        //ptcglive conversion for GG/TG cards (the alt art bs) (don't apply to promo sets)
        string = Pattern.compile("(?!PR-)(\\w{2,3})-(\\w{2,3}) (\\d+)").matcher(string).replaceAll("$1 $2$3");
        //special case for double crisis set
        string = Pattern.compile("xy5-5 ").matcher(string).replaceAll("DCR ");
        string = Pattern.compile(" PH").matcher(string).replaceAll("");
        String regexWithOldSet = "(\\d+) (.+?)(?= \\w*-\\w*\\d*$) (\\w*-\\w*\\d*)";
        String regexWithSet = "(\\d+) (.+?) (\\w{2,3}) ((\\w{2,3})?\\d+[a-zA-Z]?)";
        String regexWithPRSet = "(\\d+) (.+?) (PR-\\w{2,3}) (\\d+)";
        String regexWithSpecialSet = "(\\d+) (.+?) ((?:\\w{2,3}(?:\\s+[a-zA-Z\\d]+)*)(?:\\s+(\\w{2,3}\\s*[a-zA-Z\\d]+)\\s*)*)$";
        String regexWithoutSet = "(\\d+) (.+?)(?=\\s\\d|$|(\\s\\d+))";

        Pattern pattern1 = Pattern.compile(regexWithOldSet);
        Matcher matcher1 = pattern1.matcher(string);

        Pattern pattern2 = Pattern.compile(regexWithSet);
        Matcher matcher2 = pattern2.matcher(string);

        Pattern pattern3 = Pattern.compile(regexWithPRSet);
        Matcher matcher3 = pattern3.matcher(string);

        Pattern pattern4 = Pattern.compile(regexWithSpecialSet);
        Matcher matcher4 = pattern4.matcher(string);

        Pattern pattern5 = Pattern.compile(regexWithoutSet);
        Matcher matcher5 = pattern5.matcher(string);
        if (matcher1.matches()) {
            log.info("[{}] match 1-regexWithOldSet", string);
            log.info("quantity: {}, name: {}, id: {}", matcher1.group(1), matcher1.group(2), matcher1.group(3));
        } else if (matcher2.find()) {
            log.info("[{}] match 2-regexWithSet", string);
            log.info("quantity: {}, name: {}, set: {}, setNumber: {}", matcher2.group(1), matcher2.group(2), matcher2.group(3), matcher2.group(4));
        } else if (matcher3.matches()) {
            log.info("[{}] match 3-regexWithPRSet", string);
            log.info("quantity: {}, name: {}, prSet: {}, setNumber: {}", matcher3.group(1), matcher3.group(2), matcher3.group(3), matcher3.group(4));
        } else if (matcher4.matches()) {
            log.info("[{}] match 4-regexWithSpecialSet", string);
            log.info("quantity: {}, name: {}, set: {}", matcher4.group(1), matcher4.group(2), matcher4.group(3));
        } else if (matcher5.matches()) {
            log.info("[{}] match 5-regexWithoutSet", string);
            log.info("quantity: {}, name: {}", matcher5.group(1), matcher5.group(2));
        }
    }

    private static boolean regexMatches(String regex, String string) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    private static String regexMatches(String regex, String string, int index) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.group(index);
    }
}
