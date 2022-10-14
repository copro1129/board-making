package copro.projectboard.service;

import copro.projectboard.domain.Article;
import copro.projectboard.domain.type.SearchType;
import copro.projectboard.dto.ArticleDto;
import copro.projectboard.dto.ArticleUpdateDto;
import copro.projectboard.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;



    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void 게시글검색_게시글반환() {
        // Given
        // When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // Then
        Assertions.assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void 게시글조회시_게시글반환() {
        // Given

        // When
        ArticleDto articles = sut.searchArticle(1L);

        // Then
        Assertions.assertThat(articles).isNotNull();
    }


    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void 게시글정보입력_게시글생성() {
        // Given
        ArticleDto dto = ArticleDto.of(LocalDateTime.now(), "Copro", "title", "conent", "#spring");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void 게시글수정정보입력_게시글수정() {
        // Given
        ArticleUpdateDto dto = ArticleUpdateDto.of("title", "content", "#java");
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.updateArticle(1L, dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글을 삭제하면, 게시글을 삭제한다.")
    @Test
    void 게시글삭제() {
        // Given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().delete(any(Article.class));
    }

}