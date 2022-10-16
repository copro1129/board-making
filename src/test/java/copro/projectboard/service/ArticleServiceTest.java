package copro.projectboard.service;

import copro.projectboard.domain.Article;
import copro.projectboard.domain.UserAccount;
import copro.projectboard.domain.type.SearchType;
import copro.projectboard.dto.ArticleDto;
import copro.projectboard.dto.ArticleWithCommentsDto;
import copro.projectboard.dto.UserAccountDto;
import copro.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비지니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;



    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void 게시글검색_게시글반환() {
                 // Given
            Pageable pageable = Pageable.ofSize(20);
            given(articleRepository.findAll(pageable)).willReturn(Page.empty());
            // When
            Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);
            assertThat(articles).isEmpty();
            then(articleRepository).should().findAll(pageable);
    }


        @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
        @Test
        void 검색어와게시글검색_페이지반환() {
            // Given
            SearchType searchType = SearchType.TITLE;
            String searchKeyword = "title";
            Pageable pageable = Pageable.ofSize(20);
            given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

            // When
            Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

            // Then
            assertThat(articles).isEmpty();
            then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
        }


    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void 게시글조회시_게시글반환() {
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void 게시글조회_예외() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void 게시글정보입력_게시글생성() {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());
        // When
        sut.saveArticle(dto);
        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void 게시글수정정보입력_게시글수정() {
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // When
        sut.updateArticle(dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }


    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void 없는게시글수정시_경고로그() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }


    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void 게시글삭제() {
            // Given
            Long articleId = 1L;
            willDoNothing().given(articleRepository).deleteById(articleId);
            // When
            sut.deleteArticle(1L);
            // Then
            then(articleRepository).should().deleteById(articleId);
        }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다")
    @Test
    void givenNothing_whenCountingArticles_thenReturnsArticleCount() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "Copro",
                "password",
                "Copro@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L, createUserAccountDto(), title, content, hashtag, LocalDateTime.now(), "Uno", LocalDateTime.now(), "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "Copro",
                "password",
                "Copro@mail.com",
                "Copro",
                "This is memo",
                LocalDateTime.now(),
                "Copro",
                LocalDateTime.now(),
                "Copro"
        );
    }

}

