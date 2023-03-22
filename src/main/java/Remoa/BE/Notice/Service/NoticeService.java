package Remoa.BE.Notice.Service;

import Remoa.BE.Notice.Dto.Req.ReqNoticeDto;
import Remoa.BE.Notice.Repository.NoticeRepository;
import Remoa.BE.Notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void registerNotice(ReqNoticeDto reqNoticeDto){
        Notice notice = new Notice();
        notice.setTitle(reqNoticeDto.getTitle());
        notice.setContent(reqNoticeDto.getContent());

        noticeRepository.save(notice);
    }

    public Page<Notice> getNotice(int pageNumber){
        int NOTICE_NUMBER = 5;
        Pageable pageable = PageRequest.of(pageNumber-1, NOTICE_NUMBER);
        return noticeRepository.findAll(pageable);
    }
}
