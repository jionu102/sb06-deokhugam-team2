package com.codeit.sb06deokhugamteam2.book;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.dto.data.BookDto;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookCreateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.request.BookUpdateRequest;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponseBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.sb06deokhugamteam2.book.dto.response.NaverBookDto;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.fixture.BookFixture;
import com.codeit.sb06deokhugamteam2.book.repository.BookRepository;
import com.codeit.sb06deokhugamteam2.book.storage.S3Storage;
import com.codeit.sb06deokhugamteam2.common.enums.PeriodType;
import com.codeit.sb06deokhugamteam2.common.enums.RankingType;
import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import com.codeit.sb06deokhugamteam2.dashboard.repository.DashboardRepository;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
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

    @Autowired
    private DashboardRepository dashBoardRepository;

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
        Book book = BookFixture.createBook(1);
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

    @Test
    @DisplayName("인기도서 조회 API 호출 통합 테스트 - 점수는 더미 데이터")
    void popularBooks_Success() throws Exception {
        //given
        for (int i = 1; i <= 5; i++) {
            Book book = Book.builder()
                    .title("title" + i)
                    .author("author" + i)
                    .isbn("12345678" + i)
                    .publisher("publisher" + i)
                    .publishedDate(LocalDate.now())
                    .description("description" + i)
                    .thumbnailUrl("https://test-bucket/test" + i + ".jpg")
                    .build();
            UUID bookId = bookRepository.saveAndFlush(book).getId();

            Dashboard dashBoard = Dashboard.builder()
                    .entityId(bookId)
                    .rank((long) i)
                    .score(100 - i)
                    .createdAt(Instant.now())
                    .rankingType(RankingType.BOOK)
                    .periodType(PeriodType.ALL_TIME)
                    .build();
            dashBoardRepository.saveAndFlush(dashBoard);
        }

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/api/books/popular?period=ALL_TIME&direction=ASC&limit=4")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        String responseBody = result.getResponse().getContentAsString();
        CursorPageResponsePopularBookDto cursorDto = objectMapper.readValue(responseBody, CursorPageResponsePopularBookDto.class);

        assertThat(cursorDto.size()).isEqualTo(4);
        assertThat(cursorDto.content().get(0).title()).isEqualTo("title1");
        assertThat(cursorDto.content().get(3).title()).isEqualTo("title4");
        assertThat(cursorDto.hasNext()).isTrue();
        assertThat(cursorDto.nextCursor()).isEqualTo("4");
        assertThat(cursorDto.nextAfter()).isEqualTo(cursorDto.content().get(3).createdAt());
    }

    @Test
    @DisplayName("도서 목록 조회 API 통합 테스트")
    void findBooks_Success() throws Exception {
        List<Book> books = BookFixture.createBooks(5);
        bookRepository.saveAll(books);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keyword", "title");
        params.add("orderBy", "title");
        params.add("direction", "DESC");
        params.add("limit", "3");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .params(params))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        String responseBody = result.getResponse().getContentAsString();
        CursorPageResponseBookDto cursorDto = objectMapper.readValue(responseBody, CursorPageResponseBookDto.class);

        assertThat(cursorDto.getSize()).isEqualTo(3);
        assertThat(cursorDto.getContent().size()).isEqualTo(3);
        assertThat(cursorDto.getNextCursor()).isNotNull();
        assertThat(cursorDto.getNextAfter()).isNotNull();
        assertThat(cursorDto.getTotalElements()).isEqualTo(5);
        assertThat(cursorDto.getContent().get(0).getTitle()).isGreaterThan(cursorDto.getContent().get(1).getTitle());
    }

    @Test
    @DisplayName("도서 상세 조회 API 통합 테스트")
    void findBookById_Success() throws Exception {
        Book book = BookFixture.createBook(1);
        bookRepository.save(book);
        UUID bookId = book.getId();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/" + bookId))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        String responseBody = result.getResponse().getContentAsString();
        BookDto bookDto = objectMapper.readValue(responseBody, BookDto.class);

        assertThat(bookDto.getId()).isEqualTo(book.getId());
        assertThat(bookDto.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookDto.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookDto.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(bookDto.getPublisher()).isEqualTo(book.getPublisher());
        assertThat(bookDto.getPublishedDate()).isEqualTo(book.getPublishedDate());
        assertThat(bookDto.getDescription()).isEqualTo(book.getDescription());
        assertThat(bookDto.getThumbnailUrl()).isEqualTo(book.getThumbnailUrl());
    }
}
