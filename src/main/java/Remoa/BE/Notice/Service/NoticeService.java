package Remoa.BE.Notice.Service;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Dto.Res.ResNoticeDto;
import Remoa.BE.Notice.Repository.NoticeRepository;
import Remoa.BE.Notice.domain.Notice;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void registerNotice(ReqNoticeDto reqNoticeDto) {
        noticeRepository.save(reqNoticeDto.toEntityNotice()); //builder를 이용해 객체를 직접 생성하지 않고 Notice 저장
    }
    public HashMap<String, Object> getNotice(int pageNumber) {

        HashMap<String, Object> resultMap = new HashMap<>();

        int NOTICE_NUMBER = 5;

        Page<Notice> notices = noticeRepository.findAll(PageRequest.of(pageNumber, NOTICE_NUMBER, Sort.by("postingTime").descending()));

        resultMap.put("notices", notices.stream().map(ResNoticeDto::new).collect(Collectors.toList())); //조회한 레퍼런스들
        resultMap.put("content", notices.getContent());
        resultMap.put("totalPages", notices.getTotalPages()); //전체 페이지의 수
        resultMap.put("totalOfAllNotices", notices.getTotalElements()); //모든 레퍼런스의 수
        resultMap.put("totalOfPageElements", notices.getNumberOfElements()); //현 페이지의 레퍼런스 수

        return resultMap;

    }

    public Notice getNoticeView(int view) {
        return noticeRepository.findById((long) view).orElseThrow(() ->
                new NotFoundException("해당 공지를 찾을 수 없습니다."));
    }

    public void NoticeViewCount(int view) {
        Notice notice = noticeRepository.findById((long) view).orElseThrow(() ->
                new NotFoundException("해당 공지를 찾을 수 없습니다."));
        notice.addNoticeViewCount(notice.getView());
        noticeRepository.save(notice);

    }
}
