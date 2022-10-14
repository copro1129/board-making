package copro.projectboard.service;

import copro.projectboard.repository.ArticleCommentRepository;
import copro.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;


}
