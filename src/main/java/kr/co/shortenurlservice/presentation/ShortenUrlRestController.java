package kr.co.shortenurlservice.presentation;

import jakarta.validation.Valid;
import kr.co.shortenurlservice.application.SimpleShortenUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortenUrlRestController {

    private SimpleShortenUrlService simpleShortenUrlService;

    @Autowired
    public ShortenUrlRestController(SimpleShortenUrlService simpleShortenUrlService) {
        this.simpleShortenUrlService = simpleShortenUrlService;
    }

    @PostMapping("/shortenUrl")
    public ResponseEntity<ShortUrlCreateResponseDto> createShortenUrl(
            @Valid @RequestBody ShortenUrlCreateRequestDto shortenUrlCreateRequestDto
            ) {

        ShortUrlCreateResponseDto shortUrlCreateResponseDto = simpleShortenUrlService.generateShortenUrl(shortenUrlCreateRequestDto);
        return ResponseEntity.ok().body(shortUrlCreateResponseDto);
    }


    @GetMapping("/{shortenUrl}")
    public ResponseEntity<ShortenUrlCreateRequestDto> redirectShortenUrl(
            @PathVariable String shortenUrl
    ) {
        return ResponseEntity.ok().body(null);
    }

    // 단축 URL 정보 조회 API
    @GetMapping("/shortenUrl/{shortenUrlKey}")
    public ResponseEntity<ShortenUrlInformationDto> getShortenUrlInformation(
            @PathVariable String shortenUrlKey) { //ReqeustBody로 해당 모든 정보를 줘야하는거아닌가 ?

        ShortenUrlInformationDto shortenUrlInfomationDto = simpleShortenUrlService.getShortenUrlInformationByShortenUrlKey(shortenUrlKey);


        return ResponseEntity.ok().body(shortenUrlInfomationDto);
    }
}
