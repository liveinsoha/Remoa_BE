package Remoa.BE.Web.Inquiry.Dto.Res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResInquiryPaging {

    private List<ResInquiryDto> inquiries;
    private int totalPages;
    private long totalOfAllInquiries;
    private int totalOfPageElements;

}