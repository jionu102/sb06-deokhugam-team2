package com.codeit.sb06deokhugamteam2.book.client;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import com.codeit.sb06deokhugamteam2.common.exception.exceptions.OcrException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class OcrClient {

    @Value("${spring.ocr.api-key}")
    private String ocrApiKey;

    public String callOcrApi(MultipartFile image) throws IOException {

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

        OkHttpClient client = new OkHttpClient();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new OcrException(ErrorCode.OCR_API_ERROR,
                    Map.of("message", ErrorCode.OCR_API_ERROR.getMessage()),
                    HttpStatus.BAD_GATEWAY);
        }
    }
}
