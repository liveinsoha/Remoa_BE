package Remoa.BE.Member.Dto.Res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResMypageFollowing {

    private String userName;

    private int followNum;

    private List<ResMypageList> resMypageList;

}
