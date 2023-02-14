package Remoa.BE.Post.Repository;

import Remoa.BE.Post.Domain.Post;
import Remoa.BE.Post.Domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<UploadFile,String> {
}
