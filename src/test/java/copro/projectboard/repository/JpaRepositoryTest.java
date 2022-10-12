package copro.projectboard.repository;

import copro.projectboard.config.JpaConfig;
import copro.projectboard.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JpA 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private ArticleRepository articleRepository;
    private ArticleCommentRepository articleCommentRepository;


    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select테스트")
    @Test
    void select테스트(){
        //given

        //when
        List<Article> articles = articleRepository.findAll();

        //Then
        Assertions.assertThat(articles).isNotNull().hasSize(123);
    }

    @DisplayName("insert테스트")
    @Test
    void insert테스트(){
        long previousCount = articleRepository.count();
        Article article = Article.of("new article", "new content", "#spring");

        Article savedArticle = articleRepository.save(article);

        Assertions.assertThat(articleRepository.count()).isEqualTo(previousCount +1);
    }

    @DisplayName("update테스트")
    @Test
    void update테스트(){
    //given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

    //when
        Article savedArticle = articleRepository.saveAndFlush(article);


    //Then
    Assertions.assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }


    @DisplayName("delete테스트")
    @Test
    void delete테스트(){
        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        //when
        articleRepository.delete(article);


        //Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount-1);
        Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount -deletedCommentsSize);
    }
}
