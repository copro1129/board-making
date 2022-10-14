package copro.projectboard.controller;

import copro.projectboard.config.SecurityConfig;
import copro.projectboard.dto.ArticleWithCommentsDto;
import copro.projectboard.dto.UserAccountDto;
import copro.projectboard.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View 컨트롤러 -게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void 게시글리스트_페이지호출() throws Exception {
        //given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
                .willReturn(Page.empty());

        //when
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"));
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));


    }


    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void 게시글상세페이지_호출() throws Exception {
        //given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

        //when
        mvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        then(articleService).should().getArticle(articleId);
    }


    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void 게시글검색전용_페이지() throws Exception {
        //given

        //when
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void 해시태그검색_페이지() throws Exception {
        //given

        //when
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search-hashtag"));
    }


    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "Copro",
                LocalDateTime.now(),
                "Copro"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(1L,
                "copro",
                "pw",
                "copro@mail.com",
                "copro",
                "memo",
                LocalDateTime.now(),
                "copro",
                LocalDateTime.now(),
                "copro"
        );
    }
}