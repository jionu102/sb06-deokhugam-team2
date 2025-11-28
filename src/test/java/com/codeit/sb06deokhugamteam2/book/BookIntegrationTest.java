package com.codeit.sb06deokhugamteam2.book;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.ISBN;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    private NaverSearchClient naverSearchClient;

    @MockitoBean
    private S3Storage s3Storage;

    private final String ISBN = "123456789";
    private final String AUTHOR = "author";
    private final String TITLE = "title";
    private final String DESCRIPTION = "description";
    private final String PUBLISHER = "publisher";
    private final LocalDate PUBLISHED_DATE = LocalDate.now();
    private final String THUMBNAIL_URL = "https://test-bucket/test.jpg";

    @Test
    @DisplayName("Book 생성 API호출 통합 테스트")
    void createBook_Success() throws Exception {
        //given
        BookCreateRequest bookCreateRequest = new BookCreateRequest();
        bookCreateRequest.setAuthor(AUTHOR);
        bookCreateRequest.setIsbn(ISBN);
        bookCreateRequest.setTitle(TITLE);
        bookCreateRequest.setDescription(DESCRIPTION);
        bookCreateRequest.setPublisher(PUBLISHER);
        bookCreateRequest.setPublishedDate(PUBLISHED_DATE);

        String jsonRequest = objectMapper.writeValueAsString(bookCreateRequest);
        MockMultipartFile bookData = new MockMultipartFile("bookData", "", MediaType.APPLICATION_JSON_VALUE, jsonRequest.getBytes());

        byte[] imageBytes = "test image bytes".getBytes();
        MockMultipartFile imageData = new MockMultipartFile("thumbnailImage", "test.jpg", "image/jpeg", imageBytes);

        //when
        when(s3Storage.getThumbnail(any(String.class))).thenReturn(THUMBNAIL_URL);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/books")
                .file(imageData)
                .file(bookData)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        String ResponseBody = result.getResponse().getContentAsString();
        BookDto bookDto = objectMapper.readValue(ResponseBody, BookDto.class);
        assertThat(bookDto.getTitle()).isEqualTo(TITLE);
        assertThat(bookDto.getIsbn()).isEqualTo(ISBN);
        assertThat(bookDto.getAuthor()).isEqualTo(AUTHOR);
        assertThat(bookDto.getPublisher()).isEqualTo(PUBLISHER);
        assertThat(bookDto.getPublishedDate()).isEqualTo(LocalDate.now());
        assertThat(bookDto.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(bookDto.getThumbnailUrl()).isEqualTo(THUMBNAIL_URL);

        assertThat(bookRepository.findAll()).hasSize(1);

        verify(s3Storage, times(1)).getThumbnail(anyString());
        verify(s3Storage, times(1)).putThumbnail(anyString(), any(byte[].class), anyString());
    }

    @Test
    @DisplayName("isbn으로 조회 API호출 통합 테스트")
    void bookInfo_Success() throws Exception {
        NaverBookDto clientNaverBookDto = new NaverBookDto();
        clientNaverBookDto.setIsbn(ISBN);
        clientNaverBookDto.setTitle(TITLE);
        clientNaverBookDto.setDescription(DESCRIPTION);
        clientNaverBookDto.setPublisher(PUBLISHER);
        clientNaverBookDto.setPublishedDate(PUBLISHED_DATE);
        clientNaverBookDto.setAuthor(AUTHOR);
        clientNaverBookDto.setThumbnailImage("Base64 Encoding Image");

        when(naverSearchClient.bookSearchByIsbn(ISBN)).thenReturn(clientNaverBookDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/api/books/info")
                        .param("isbn", ISBN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        String ResponseBody = result.getResponse().getContentAsString();
        NaverBookDto naverBookDto = objectMapper.readValue(ResponseBody, NaverBookDto.class);

        assertThat(naverBookDto.getTitle()).isEqualTo(TITLE);
        assertThat(naverBookDto.getIsbn()).isEqualTo(ISBN);
        assertThat(naverBookDto.getAuthor()).isEqualTo(AUTHOR);
        assertThat(naverBookDto.getPublisher()).isEqualTo(PUBLISHER);
        assertThat(naverBookDto.getPublishedDate()).isEqualTo(PUBLISHED_DATE);
        assertThat(naverBookDto.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(naverBookDto.getThumbnailImage()).isEqualTo("Base64 Encoding Image");
    }

    @Test
    @DisplayName("도서 수정 API 통합 테스트")
    void bookUpdate_Success() throws Exception {
        Book book = Book.builder()
                .isbn(ISBN)
                .author(AUTHOR)
                .description(DESCRIPTION)
                .publishedDate(PUBLISHED_DATE.minusDays(1L))
                .thumbnailUrl(THUMBNAIL_URL)
                .publisher(PUBLISHER)
                .title(TITLE)
                .build();

        Book savedBook = bookRepository.save(book);
        UUID targetBookId = savedBook.getId();

        BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setTitle("updated title");
        bookUpdateRequest.setDescription("updated description");
        bookUpdateRequest.setPublisher("updated publisher");
        bookUpdateRequest.setPublishedDate(LocalDate.now());
        bookUpdateRequest.setAuthor("updated author");

        String jsonRequest = objectMapper.writeValueAsString(bookUpdateRequest);
        MockMultipartFile bookData = new MockMultipartFile("bookData", "", MediaType.APPLICATION_JSON_VALUE, jsonRequest.getBytes());

        byte[] imageBytes = "updated image bytes".getBytes();
        MockMultipartFile imageData = new MockMultipartFile("thumbnailImage", "updated.jpg", "image/jpeg", imageBytes);

        when(s3Storage.getThumbnail(anyString())).thenReturn("https://test-bucket/updated.jpg");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/books/" + targetBookId)
                .file(bookData)
                .file(imageData)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        String ResponseBody = result.getResponse().getContentAsString();
        BookDto bookDto = objectMapper.readValue(ResponseBody, BookDto.class);

        assertThat(bookDto.getTitle()).isEqualTo("updated title");
        assertThat(bookDto.getThumbnailUrl()).isEqualTo("https://test-bucket/updated.jpg");
        assertThat(bookDto.getAuthor()).isEqualTo("updated author");
        assertThat(bookDto.getDescription()).isEqualTo("updated description");
        assertThat(bookDto.getPublishedDate()).isEqualTo(LocalDate.now());
        assertThat(bookDto.getPublisher()).isEqualTo("updated publisher");

        verify(s3Storage, times(1)).deleteThumbnail(anyString());
        verify(s3Storage, times(1)).putThumbnail(anyString(), any(byte[].class), anyString());
        verify(s3Storage, times(1)).getThumbnail(anyString());
    }
}
