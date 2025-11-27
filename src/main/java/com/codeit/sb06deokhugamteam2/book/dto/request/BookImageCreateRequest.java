package com.codeit.sb06deokhugamteam2.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookImageCreateRequest {
    private byte[] bytes;
    private String contentType;
    private String originalFilename;
}
