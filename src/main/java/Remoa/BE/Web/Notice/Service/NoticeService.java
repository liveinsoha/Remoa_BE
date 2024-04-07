package Remoa.BE.Web.Notice.Service;

import Remoa.BE.Web.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Web.Notice.Dto.Res.NoticeResponseDto;
import Remoa.BE.Web.Notice.Dto.Res.ResAllNoticeDto;
import Remoa.BE.Web.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Web.Notice.Repository.NoticeRepository;
import Remoa.BE.Web.Notice.domain.Notice;
import Remoa.BE.exception.CustomMessage;
import Remoa.BE.exception.response.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void registerNotice(ReqNoticeDto reqNoticeDto, String enrollNickname) {
        noticeRepository.save(reqNoticeDto.toEntityNotice(enrollNickname)); //builder를 이용해 객체를 직접 생성하지 않고 Notice 저장
    }

    public NoticeResponseDto getNotice(int pageNumber) {

        HashMap<String, Object> resultMap = new HashMap<>();

        int NOTICE_NUMBER = 5;

        Page<Notice> notices = noticeRepository.findAll(PageRequest.of(pageNumber, NOTICE_NUMBER, Sort.by("postingTime").descending()));

        List<ResNoticeDto> noticeDtos = notices.stream().map(ResNoticeDto::new).toList();

        return NoticeResponseDto.builder()
                .notices(noticeDtos)
                .totalPages(notices.getTotalPages())
                .totalOfAllNotices(notices.getTotalElements())
                .totalOfPageElements(notices.getNumberOfElements())
                .build();
    }

    @Transactional(readOnly = true)
    public ResAllNoticeDto getNoticeView(int view) {
        return noticeRepository.findById((long) view).map(ResAllNoticeDto::new).orElseThrow(() ->
                new BaseException(CustomMessage.NO_ID));
    }

    @Transactional
    public void NoticeViewCount(int view) {
        Notice notice = noticeRepository.findById((long) view).orElseThrow(() ->
                new BaseException(CustomMessage.NO_ID));
        notice.addNoticeViewCount();
        noticeRepository.save(notice);

    }

    @Transactional
    public void modifying_Notice_NickName(String newNick, String oldNick) {
        noticeRepository.modifyingNoticeAuthor(newNick, oldNick);
    }
}
