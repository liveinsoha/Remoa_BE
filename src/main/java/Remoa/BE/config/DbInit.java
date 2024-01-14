package Remoa.BE.config;

import Remoa.BE.Post.Domain.Category;
import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Post.Service.CategoryService;
import Remoa.BE.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbInit {
    private final CategoryService categoryService;

    public static final List<String> categoryList = List.of("idea", "marketing", "design", "video", "digital", "etc");
    /**
     * @PostConstruct로 Category를 세팅해주는 메서드
     */
    @PostConstruct
    public void initCategories() {
        if (!categoryService.findAllCategories().isEmpty()) {
            //do nothing
            log.info("==========Categories are already set==========");
        } else {
            Category idea = new Category(categoryList.get(0));
            Category marketing = new Category(categoryList.get(1));
            Category design = new Category(categoryList.get(2));
            Category video = new Category(categoryList.get(3));
            Category digital = new Category(categoryList.get(4));
            Category etc = new Category(categoryList.get(5));
            this.categoryService.persistCategory(idea, marketing, design, video, digital, etc);
            log.info("==========Setting Categories completely==========");
        }
    }

}
