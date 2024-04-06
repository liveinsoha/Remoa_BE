package Remoa.BE.Post.Domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Category {

    public Category(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Category별 작성되어진 Post
     */
    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    /**
     * Category 이름
     */
    private String name;

}
