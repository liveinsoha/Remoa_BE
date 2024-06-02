package Remoa.BE.Web.Post.Repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    void QueryTest() {
       assertThat( postRepository.existsById(1L)).isFalse();
    }

}