package com.codeit.sb06deokhugamteam2.book.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NaverSearchResponse {
    private List<NaverBookDto> items;
}
