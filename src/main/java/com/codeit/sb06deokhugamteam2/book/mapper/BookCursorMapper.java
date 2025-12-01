package com.codeit.sb06deokhugamteam2.book.mapper;

import com.codeit.sb06deokhugamteam2.book.dto.data.PopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponsePopularBookDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class BookCursorMapper {

    public CursorPageResponsePopularBookDto toCursorBookDto(List<PopularBookDto> popularBookDtoList, Integer limit) {

        if(popularBookDtoList.isEmpty()) {
            return CursorPageResponsePopularBookDto.builder()
                    .content(popularBookDtoList)
                    .nextCursor(null)
                    .nextAfter(null)
                    .size(0)
                    .totalElements(null)
                    .hasNext(false)
                    .build();
        }

        boolean hasNext = false;
        if (popularBookDtoList.size() > limit) {
            popularBookDtoList.remove(limit.intValue());       // 추가로 가져온 한 개의 데이터 제거
            hasNext = true;
        }

        String nextCursor = popularBookDtoList.get(popularBookDtoList.size() - 1).rank() + "";

        Instant nextAfter = popularBookDtoList.get(popularBookDtoList.size() - 1).createdAt();

        return CursorPageResponsePopularBookDto.builder()
                .content(popularBookDtoList)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(popularBookDtoList.size())
                .totalElements(null)       // 커서 페이지네이션에서는 totalElements 필요 x, 응답에 포함되긴 함.
                .hasNext(hasNext)
                .build();
    }
}
