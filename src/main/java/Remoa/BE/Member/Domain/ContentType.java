package Remoa.BE.Member.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CommentFeedback에 comment와 Feedback 구분을 위한 enum 클래스.
 */
@AllArgsConstructor
@Getter
public enum ContentType {
    COMMENT,
    FEEDBACK;
}
