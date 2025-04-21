package moody.bot.youtubedl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtil {
    public static String extractVideoId(String url) {
        String pattern = "(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|v/))([a-zA-Z0-9_-]{11})";
        Pattern compiled = Pattern.compile(pattern);
        Matcher matcher = compiled.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid YouTube URL");
    }
}
