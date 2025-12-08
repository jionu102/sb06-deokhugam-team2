package com.codeit.sb06deokhugamteam2.book.service;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.OcrException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Value("${spring.ocr.api-key}")
    private String ocrApiKey;

    public String getIsbnByOcrApi(MultipartFile image) {

        // 무료버전 OCR API는 1MB 이하의 파일만 처리 가능
        // 월 25,000번 요청 가능
        if (image.getSize() > 1024 * 1024) {
            throw new RuntimeException("파일 크기는 1MB 이하여야 합니다.");
        }

        try {

            String json = callOcrApi(image);

            JsonNode root = objectMapper.readTree(json);

            String parsedText = root
                    .get("ParsedResults")
                    .get(0)
                    .get("ParsedText")
                    .asText();

            Pattern pattern = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)");     // ex. 978-3-16-148410-0
            Matcher matcher = pattern.matcher(parsedText);

            if (matcher.find()) {
                return matcher.group(0).replaceAll("-", "");
            } else {
                throw new OcrException(ErrorCode.ISBN_NOT_FOUND,
                        Map.of("message", ErrorCode.ISBN_NOT_FOUND.getMessage(), "detail", parsedText),
                        HttpStatus.NOT_FOUND);
            }

        } catch (IOException e) {
            throw new OcrException(ErrorCode.OCR_API_ERROR,
                    Map.of("message", ErrorCode.OCR_API_ERROR.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String callOcrApi(MultipartFile image) throws IOException {

        final String url = "https://api.ocr.space/parse/image";
        final String language = "eng";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apikey", ocrApiKey)
                .addFormDataPart("language", language)
                .addFormDataPart("file", image.getOriginalFilename(),
                        RequestBody.create(
                                image.getBytes(),
                                MediaType.parse(image.getContentType())
                        ))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new OcrException(ErrorCode.OCR_API_ERROR,
                    Map.of("message", ErrorCode.OCR_API_ERROR.getMessage()),
                    HttpStatus.BAD_GATEWAY);
        }
    }
}
