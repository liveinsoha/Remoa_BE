package Remoa.BE.Member.Dto.Res;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResMemberInfoDto {

    private Long memberId;
    private String nickname;
    private String profileImage;
    private Boolean isFollow;
}
