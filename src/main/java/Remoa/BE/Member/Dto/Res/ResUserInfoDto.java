package Remoa.BE.Member.Dto.Res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResUserInfoDto {

    private String email;

    private String nickname;
    private String phoneNumber;

    private String university;

    private String oneLineIntroduction;

}
