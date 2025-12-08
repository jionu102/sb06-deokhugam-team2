package com.codeit.sb06deokhugamteam2.book.repository;

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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest가 h2 db로 자동대체하는 것을 막기위해 NONE 설정
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("test")
//class BookRepositoryTest {
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @PersistenceContext
//    private EntityManager em;
//
//    // Slice 테스트는 전체 로딩하지 않기 때문에 빈 주입 필요
//    @TestConfiguration
//    static class TestConfig {
//        @PersistenceContext
//        private EntityManager em;
//
//        @Bean
//        public JPAQueryFactory jpaQueryFactory() {
//            return new JPAQueryFactory(em);
//        }
//    }
//
//    @Test
//    @DisplayName("EntityManager REMOVE 호출 시 소프트 삭제 되는지 테스트")
//    void softDelete_Using_EntityManager_Remove() {
//        //given
//        Book book = BookFixture.createBook(1);
//        Book savedBook = bookRepository.save(book);
//        em.flush();
//
//        // when
//        em.remove(savedBook);
//        em.flush(); // Update 쿼리 발생 확인
//        em.clear();
//
//        // then
//        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Repository DELETE 호출 시 소프트 삭제 되는지 테스트")
//    void softDelete_Using_Repository_Delete() {
//        //given
//        Book book = BookFixture.createBook(1);
//        Book savedBook = bookRepository.save(book);
//        em.flush();
//
//        // when
//        bookRepository.deleteById(savedBook.getId());
//        em.flush();
//        em.clear();
//
//        // then
//        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Native Query로 하드 삭제 되는지 테스트")
//    void deleteBook_By_EntityManage_Success() {
//        // given
//        Book book = BookFixture.createBook(1);
//        Book savedBook = bookRepository.save(book);
//        em.flush();
//
//        // when
//        // 논리삭제가 되어야 물리삭제 가능하도록 Native Query로 구현된 메서드 호출
//        bookRepository.deleteSoftById(savedBook.getId());
//        bookRepository.deleteHardById(savedBook.getId());
//        em.flush();
//        em.clear();
//
//        System.out.println(savedBook.getAuthor());
//
//        // then
//        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
//    }
//
//
//    @Test
//    @DisplayName("JPQL DELETE 쿼리가 UPDATE 쿼리로 변환되고 where deleted = false 조건이 붙어 소프트 삭제 되는지 테스트")
//    void softDelete_Using_JPQL_Delete_Query() {
//        //given
//        Book book = BookFixture.createBook(1);
//        Book savedBook = bookRepository.save(book);
//        em.flush();
//
//        // when
//        bookRepository.deleteSoftById(savedBook.getId());
//        em.flush();
//        em.clear();
//
//        // then
//        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
//    }
//}
