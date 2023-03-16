package Remoa.BE.Post.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MyFeedbackController {

    @GetMapping("/user/feedback")
    public ResponseEntity<Object> receivedFeedback(HttpServletRequest request,
                                                   @RequestParam(required = false, defaultValue = "all") String category) {


        return null;
    }

}
