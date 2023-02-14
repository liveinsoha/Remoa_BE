package Remoa.BE.Post.Domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "FILE")
public class UploadFile {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long uploadFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 업로드된 파일의 원본 이름
     */
    @Column(name = "original_file_name")
    private String originalFileName;

    /**
     * UUID.randomUUID()를 통해 받은 랜덤값을 통해 S3파일 서버에 저장할 파일의 이름
     */
    @Column(name = "save_file_name")
    private String saveFileName;

    /**
     * 파일의 확장자
     */
    private String extension;

    /**
     * 파일이 저장된 시간 저장
     */
    @Column(name = "store_file_url")
    private String storeFileUrl;

    //이후 업로드 날짜 및 시간, 컨텐츠 타입, 사이즈 등의 필드등이 필요할 때 손봐야할듯.
}
