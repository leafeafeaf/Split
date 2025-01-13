package kr.co.shortenurlservice.domain;

import lombok.Getter;

import java.util.Locale;
import java.util.Random;

@Getter
public class ShortenUrl {

    private String originalUrl;
    private String shortenUrlKey;
    private Long redirectCount;

    public ShortenUrl(String originalUrl, String shortenUrlKey, Long redirectCount) {
        this.originalUrl = originalUrl;
        this.shortenUrlKey = shortenUrlKey;
        this.redirectCount = redirectCount;
    }

    public ShortenUrl(String originalUrl, String shortenUrlKey) {
        this.originalUrl = originalUrl;
        this.shortenUrlKey = shortenUrlKey;
        this.redirectCount = 0L;
    }

    public static String generateShortenUrlKey() {
        String base56Characters = "23456789ABCDEFGHJKLNMPQSTUVWXYZabcdefghijkmnopqrstuvwxyz";

        Random random = new Random();
        StringBuilder shortenUrlKey = new StringBuilder();

        for (int count = 0; count < 8; count++) {
            int base56CharactersIndex = random.nextInt(0, base56Characters.length());
            char base56Character = base56Characters.charAt(base56CharactersIndex);

            shortenUrlKey.append(base56Character);
        }
        return shortenUrlKey.toString();
    }

}
