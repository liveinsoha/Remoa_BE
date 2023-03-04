package Remoa.BE.Member.Dto.Res;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
public class ResUserInfoDto {

    private String email;

    private String nickname;
    private String phoneNumber;

    private String university;

    private String oneLineIntroduction;

}
