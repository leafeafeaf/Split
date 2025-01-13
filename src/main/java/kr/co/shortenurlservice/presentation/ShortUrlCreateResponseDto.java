package kr.co.shortenurlservice.presentation;

import kr.co.shortenurlservice.domain.ShortenUrl;

public class ShortUrlCreateResponseDto {

    private String originalURl;
    private String shortenUrlKey;

    public ShortUrlCreateResponseDto(ShortenUrl shortenUrl) {
        this.originalURl = shortenUrl.getOriginalUrl();
        this.shortenUrlKey = shortenUrl.getShortenUrlKey();
    }

    public String getOriginalURl(){
        return originalURl;
    }

    public String getShortenUrlKey() {
        return shortenUrlKey;
    }
}
