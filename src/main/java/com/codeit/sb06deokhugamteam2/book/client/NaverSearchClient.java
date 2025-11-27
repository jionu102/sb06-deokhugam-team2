package com.codeit.sb06deokhugamteam2.book.client;

import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverSearchResponse;
import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.NaverSearchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
public class NaverSearchClient {
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public NaverSearchClient(RestTemplate restTemplate,
                             @Value("${spring.naver.client-id}") String clientId,
                             @Value("${spring.naver.client-secret}") String clientSecret) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public NaverBookDto bookSearchByIsbn(String isbn) {
        String requestUrl = "https://openapi.naver.com/v1/search/book.json?query=" + isbn;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<NaverSearchResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, NaverSearchResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NaverSearchException(ErrorCode.NAVER_SEARCH_EXCEPTION,
                    Map.of(
                            "naver status", response.getStatusCode(),
                            "detail", "Naver API 오류입니다."
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        NaverSearchResponse naverSearchResponse = response.getBody();

        if (naverSearchResponse == null) {
            throw new NaverSearchException(ErrorCode.NAVER_SEARCH_EXCEPTION,
                    Map.of(
                            "detail", "Naver API 응답의 body가 없습니다."
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        if (naverSearchResponse.getItems() == null || naverSearchResponse.getItems().isEmpty()) {
            throw new NaverSearchException(ErrorCode.NAVER_SEARCH_EXCEPTION,
                    Map.of(
                            "detail", "검색 결과가 존재하지 않습니다."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (naverSearchResponse.getItems().size() > 1) {
            throw new NaverSearchException(ErrorCode.NAVER_SEARCH_EXCEPTION,
                    Map.of(
                            "detail", "여러 검색 결과가 존재합니다. isbn을 확인해주세요."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        NaverBookDto naverBookDto = naverSearchResponse.getItems().get(0);

        if (naverBookDto.getThumbnailImage() != null) {
            byte[] imageBytes = downloadImage(naverBookDto.getThumbnailImage());
            String encodingImageData = Base64.getEncoder().encodeToString(imageBytes);
            naverBookDto.setThumbnailImage(encodingImageData);
        }

        return naverBookDto;
    }

    private byte[] downloadImage(String imageUrl) {
        return restTemplate.getForObject(imageUrl, byte[].class);
    }
}
