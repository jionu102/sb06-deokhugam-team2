package com.codeit.sb06deokhugamteam2.book;

import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class BookIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @MockitoBean
    private S3Storage s3Storage;

    @Test
    @DisplayName("Book 생성 API호출 통합 테스트")
    void createBook_Success() throws Exception {
        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest();
        bookCreateRequest.setAuthor("author");
        bookCreateRequest.setIsbn("123456789");
        bookCreateRequest.setTitle("title");
        bookCreateRequest.setDescription("description");
        bookCreateRequest.setPublisher("publisher");
        bookCreateRequest.setPublishedDate(LocalDate.parse("2025-11-26"));

        String jsonRequest = objectMapper.writeValueAsString(bookCreateRequest);
        MockMultipartFile bookData = new MockMultipartFile("bookData", "",  MediaType.APPLICATION_JSON_VALUE, jsonRequest.getBytes());

        byte[] imageBytes = "test image bytes".getBytes();
        MockMultipartFile imageData = new MockMultipartFile("thumbnailImage", "test.jpg", "image/jpeg", imageBytes);

        //when
        when(s3Storage.getThumbnail(any(String.class))).thenReturn("https://test-bucket/test.jpg");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/books")
                .file(imageData)
                .file(bookData)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        String ResponseBody = result.getResponse().getContentAsString();
        BookDto bookDto =  objectMapper.readValue(ResponseBody, BookDto.class);
        assertThat(bookDto.getTitle()).isEqualTo("title");
        assertThat(bookDto.getIsbn()).isEqualTo("123456789");
        assertThat(bookDto.getAuthor()).isEqualTo("author");
        assertThat(bookDto.getPublisher()).isEqualTo("publisher");
        assertThat(bookDto.getPublishedDate()).isEqualTo(LocalDate.parse("2025-11-26"));
        assertThat(bookDto.getDescription()).isEqualTo("description");
        assertThat(bookDto.getThumbnailUrl()).isEqualTo("https://test-bucket/test.jpg");

        assertThat(bookRepository.findAll()).hasSize(1);
    }
}
