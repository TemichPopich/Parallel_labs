package utils;

import java.util.List;

public class Utils {
    public static final int PAGE_SIZE = 48;

    public static final String RESULT_FILE_NAME = "results/thread_%d_result.txt";

    public static final String PARSE_ERROR_MESSAGE = "Error while parsing links";

    public static final String BASE_LINK_PARSE_PATTERN = "div[class=\"row unit-list\"] a";

    public static final String SUB_LINK_PARSE_PATTERN = "table td:contains(Производитель) + td";

    public static final List<Integer> THREADS = List.of(2, 4, 6, 8);

    public static final List<String> BASE_LINKS = List.of(
        "https://mkub.ru/dlja-gostinoj/vitrini-i-bufeti/?limit=48",
        "https://mkub.ru/dlja-gostinoj/mjagkaja-mebel/divani/prjamie-divani/?limit=48",
        "https://mkub.ru/dlja-gostinoj/tumbi-dlja-tv/napolnie-tv-tumbi/?limit=48"
    );
}
