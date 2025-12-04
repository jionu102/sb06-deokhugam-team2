package com.codeit.sb06deokhugamteam2.book.repository;

import com.codeit.sb06deokhugamteam2.book.client.NaverSearchClient;
import com.codeit.sb06deokhugamteam2.book.entity.Book;
import com.codeit.sb06deokhugamteam2.book.fixture.BookFixture;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// @DataJpaTest가 h2 db로 자동대체하는 것을 막기위해 NONE 설정
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager em;

    // Slice 테스트는 전체 로딩하지 않기 때문에 빈 주입 필요
    @TestConfiguration
    static class TestConfig {
        @PersistenceContext
        private EntityManager em;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    @DisplayName("도서 softdelete, harddelete 동작 테스트")
    void deleteBook_Success() {
        //given
        System.out.println("---BookRepositoryTest.deleteBook_Success 시작---");
        System.out.println("Book1 생성 및 저장");
        Book book1 = BookFixture.createBook(1);
        Book savedBook1 = bookRepository.save(book1);
        em.flush();

        System.out.println("영속성 컨텍스트에서 삭제 테스트 시작");
        em.remove(savedBook1);
        em.flush(); // Update 쿼리 발생 확인

        System.out.println("Book2 생성 및 저장");
        Book book2 = BookFixture.createBook(2);
        Book savedBook2 = bookRepository.save(book2);
        em.flush();

        System.out.println("리포지토리에서 논리삭제 테스트 시작");
        bookRepository.deleteById(savedBook2.getId());
        em.flush();

        System.out.println("Book3 생성 및 저장");
        Book book3 = BookFixture.createBook(3);
        Book savedBook3 = bookRepository.save(book3);
        em.flush();

        System.out.println("리포지토리에서 물리삭제 테스트 시작");
        bookRepository.deleteHardById(savedBook3.getId());
        em.flush();
    }
}
