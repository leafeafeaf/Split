package kr.co.shortenurlservice.presentation.Controller;

import jakarta.validation.Valid;
import kr.co.shortenurlservice.application.SimpleShortenUrlService;
import kr.co.shortenurlservice.presentation.responseDto.ShortenUrlInformationDto;
import kr.co.shortenurlservice.presentation.reqeustDto.ShortenUrlCreateRequestDto;
import kr.co.shortenurlservice.presentation.responseDto.ShortUrlCreateResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

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

    //리다이렉트 기능
    @GetMapping("/{shortenUrl}")
    public ResponseEntity<ShortenUrlCreateRequestDto> redirectShortenUrl(
            @PathVariable String shortenUrl
    ) throws URISyntaxException {

        String originalUri = simpleShortenUrlService.getOriginalUrlByShortenUrlKey(shortenUrl);

        URI redirectUrl = new URI(originalUri);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUrl);

        //헤더와 상태코드를 포함
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);

    }

    // 단축 URL 정보 조회 API
    @GetMapping("/shortenUrl/{shortenUrlKey}")
    public ResponseEntity<ShortenUrlInformationDto> getShortenUrlInformation(
            @PathVariable String shortenUrlKey) { //ReqeustBody로 해당 모든 정보를 줘야하는거아닌가 ?

        ShortenUrlInformationDto shortenUrlInfomationDto = simpleShortenUrlService.getShortenUrlInformationByShortenUrlKey(shortenUrlKey);


        return ResponseEntity.ok().body(shortenUrlInfomationDto);
    }



}
