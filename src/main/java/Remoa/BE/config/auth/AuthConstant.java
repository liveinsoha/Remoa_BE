package Remoa.BE.config.auth;

public class AuthConstant {

    // 인증이 필요하지 않은 경로
    public static final String[] AUTH_WHITELIST = {
            "/api/**", "/graphiql", "/graphql",
            "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html"
    };

    // 인증이 필요한 경로
    public static final String[] AUTH_BLACKLIST = {
            "/user/logout", "/following", "/follower", "/follow/{member_id}", "/user", "/user/img", "/reference/{reference_id}/comment",
            "/reference/{reference_id}/comment/{comment_id}", "/reference/comment/{comment_id}", "/reference/comment/{comment_id}", "/comment/{comment_id}/like",
            "/reference/{reference_id}/{page_number}", "/reference/{reference_id}/feedback/{feedback_id}", "/reference/feedback/{feedback_id}", "/reference/feedback/{feedback_id}/like"
            , "/user/activity", "/user/scrap", "/user/comment", "/user/receive", "/user/feedback", "/user/reference", "/reference", "/reference", "/reference/{reference_id}",
            "/reference/{reference_id}/like", "/reference/{reference_id}/scrap", "/user/reference/{reference_id}", "/user/referenceCategory/{category}",
            "/delete", "/delete/{member_id}", "/inquiry"
    };

    // GET 메서드에 대한 인증이 필요한 경로
    public static final String[] GET_AUTH_BLACKLIST
            = {"/follow/{member_id}", "/following", "/follower", "/user", "/user/img",
            "/inquiry", "/inquiry/view", "/user/activity", "/user/scrap", "/user/comment",
            "/user/receive", "/user/feedback", "/user/reference"};

    // POST 메서드에 대한 인증이 필요한 경로
    public static final String[] POST_AUTH_BLACKLIST
            = {"/follow/{member_id}", "/inquiry", "/reference/{reference_id}/comment",
            "/reference/{reference_id}/comment/{comment_id}", "/comment/{comment_id}/like",
            "/reference/{reference_id}/{page_number}", "/reference/{reference_id}/feedback/{feedback_id}",
            "/reference/feedback/{feedback_id}/like", "/reference", "/reference/{reference_id}/like",
            "/reference/{reference_id}/scrap"};

    // PUT 메서드에 대한 인증이 필요한 경로
    public static final String[] PUT_AUTH_BLACKLIST
            = {"/user", "/user/img", "/reference/comment/{comment_id}", "/reference/feedback/{feedback_id}",
            "/reference/{reference_id}", "/inquiry/{inquiryId}", "/api/member/logout"};

    // DELETE 메서드에 대한 인증이 필요한 경로
    public static final String[] DELETE_AUTH_BLACKLIST
            = {"/user/img", "/delete/{member_id}", "/delete", "/reference/comment/{comment_id}",
            "/reference/feedback/{feedback_id}", "/user/reference/{reference_id}", "/user/referenceCategory/{category}",
            "/inquiry/{inquiryId}"};

    // ADMIN 역할에 대한 인증이 필요한 경로
    public static final String[] ADMIN_POST_AUTH_BLACKLIST = {"/notice", "/inquiry/{inquiry_id}/reply", "/admin/**"};

    public static final String[] ADMIN_PUT_AUTH_BLACKLIST = {"/notice/{noticeId}", "/inquiry/reply/{reply_id}", "/admin/**"};

    public static final String[] ADMIN_DELETE_AUTH_BLACKLIST = {"/notice/{noticeId}", "/admin/**"};
}
