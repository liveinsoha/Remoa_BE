package Remoa.BE.Post.Repository;

import java.util.List;

import Remoa.BE.Post.Dto.Response.ThumbnailReferenceDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<ThumbnailReferenceDto, Long> {
    List<ThumbnailReferenceDto> findByTitleContaining(String name);
}