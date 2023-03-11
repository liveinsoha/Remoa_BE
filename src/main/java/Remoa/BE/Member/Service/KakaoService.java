package Remoa.BE.Member.Service;

import Remoa.BE.Member.Domain.Member;
import Remoa.BE.Member.Repository.MemberRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    //카카오 로그인시 접속해야 할 링크 : https://kauth.kakao.com/oauth/authorize?client_id=139febf9e13da4d124d1c1faafcf3f86&redirect_uri=http://localhost:8080/login/kakao&response_type=code

    private final MemberRepository MemberRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    /**
     * 카카오 인증 서버에 code를 보내고 token을 발급받는 메서드
     * @param code
     * @return token
     * @throws IOException
     */
    public String getToken(String code) throws IOException {
        //토큰을 받아올 카카오 인증 서버. 레모아 서버가 클리아언트로, 카카오 인증 서버가 서버로 동작한다고 보면 됩니다.
        String host = "https://kauth.kakao.com/oauth/token";
        //카카오 인증 서버와 통신하기 위한 설정
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String token = "";

        try {
            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            //x-www-form-urlencoded 타입으로 Body에 담아 카카오 인증 서버에 Post로 요청하기 위한 버퍼 스트림 생성
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=139febf9e13da4d124d1c1faafcf3f86");

            // 02.26. 프론트와 연동하는데 여기 3000으로 바꿔달라고 하셔서 바꿔놓았습니다 -광휘
            sb.append("&redirect_uri=http://localhost:3000/login/kakao");
            sb.append("&code=" + code);
            sb.append("&client_secret=5IueqXws75WoH1e3gCSI2aNxQgOGMdBG");

            bw.write(sb.toString());

            //write 되어 버퍼에 있던 데이터를 flush를 통해 출력 스트림으로 출력하고, 카카오 인증 서버에 요청 전송
            bw.flush();

            //========카카오 인증 서버에 요청 후 응답 받음========//

            //카카오 인증 서버에서 응답으로 받은 response code 값
            int responseCode = urlConnection.getResponseCode();
            log.debug("responseCode = {}", responseCode);

            //카카오 인증 서버에서 받은 응답을 받기 위한 버퍼 스트림 생성(참고-요청과는 달리 JSON 데이터를 보내줌)
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String result = "";
            //다양한 형식(한 줄 이상의 JSON 데이터)를 받기 위한 작업
            while ((line = br.readLine()) != null) {
                result += line;
            }

            //JSON parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);

            //access 토큰값 -> 카카오 api 서버에 사용자 정보를 받아오기 위해서 사용될 토큰
            String access_token = elem.get("access_token").toString();
            //refresh 토큰은 현재 서비스 구조상 카카오 api 서버에서 사용자 정보만 가져오면 되므로 필요하지 않음
//            String refresh_token = elem.get("refresh_token").toString();

            token = access_token;

            //버퍼스트림 닫기
            br.close();
            bw.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        return token;
    }

    /**
     * 카카오 api 서버에 token을 보내고 사용자 정보를 발급받는 메서드
     * @param access_token
     * @return 카카오 사용자 정보((kakao)id, nickname, email, profileImage)
     * @throws IOException
     */
    public Map<String, Object> getUserInfo(String access_token) throws IOException {

        //사용자 정보를 받아올 카카오 api 서버. 레모아 서버가 클리아언트로, 카카오 api 서버가 서버로 동작한다고 보면 됩니다.
        String host = "https://kapi.kakao.com/v2/user/me";
        //사용자 정보를 받을 Map 객체 생성
        Map<String, Object> result = new HashMap<>();
        try {
            URL url = new URL(host);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //Request Header에 토큰 인증 관련 값을 설정하고, GET을 통해 카카오 api 서버에 요청한다는 옵션
            urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);
            urlConnection.setRequestMethod("GET");

            //========카카오 api 서버에 요청 후 응답 받음========//

            int responseCode = urlConnection.getResponseCode();
            log.debug("responseCode = {}", responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line = "";
            String res = "";
            //다양한 형식(한 줄 이상의 JSON 데이터)를 받기 위한 작업
            while ((line=br.readLine()) != null)
            {
                res+=line;
            }

            //JSON parsing
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(res);
            JSONObject properties = (JSONObject) obj.get("properties");

            String id = obj.get("id").toString();
            String nickname = properties.get("nickname").toString();

            String profileImage = properties.get("profile_image").toString();
            JSONObject kakao_account = (JSONObject) obj.get("kakao_account");
            String email = kakao_account.get("email").toString();

            result.put("id", id);
            result.put("nickname", nickname);
            result.put("image", profileImage);
            result.put("email", email);

            // 프로필사진을 jpg로 변환하기
            String imageUrl = profileImage;
            URL profileURL = new URL(imageUrl);
            InputStream is = profileURL.openStream();

            // 이미지 파일 생성
            BufferedImage image = ImageIO.read(is);
            File outputFile = new File("image.jpg");
            ImageIO.write(image, "jpg", outputFile);

            // 파일 다운로드
            byte[] fileContent = FileCopyUtils.copyToByteArray(outputFile);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "image.jpg");
            headers.setContentLength(fileContent.length);
            log.info("이미지 파일 생성 완료");

            // 사진으로 바꾼뒤 바로 S3로 업로드하기
            uploadToS3(outputFile);
            log.info("S3로 업로드 완료");

            br.close();


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 프로필 사진 초기설정 - S3에 저장하기
    public String uploadToS3(File file) throws IOException {
        String s3FileName = UUID.randomUUID() + "-" + file.getName();
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(file.length());
        amazonS3.putObject(bucket, s3FileName, file);
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }


    /**
     * 사용자의 카카오 api 동의 내역을 확인하는 메서드.
     * <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#check-consent">kakao developers 공식문서</a> <- 참고
     * @param access_token
     * @return 사용자의 동의항목 JSON 데이터
     */
    public String getAgreementInfo(String access_token)
    {
        String result = "";
        String host = "https://kapi.kakao.com/v2/user/scopes";
        try{
            URL url = new URL(host);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            while((line=br.readLine())!=null)
            {
                result += line;
            }

            int responseCode = urlConnection.getResponseCode();
            log.debug("responseCode = {}", responseCode);

            // result는 json 포멧.
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * kakaoId가 db에 있는지 확인해주는 메서드
     * @param kakaoId
     * @return db에 존재 -> Member, 없으면 -> null
     */
    public Member distinguishKakaoId(Long kakaoId) {

        if (!MemberRepository.findByKakaoId(kakaoId).isPresent()) {
            return null;
        }
        Member kakaoMember = MemberRepository.findByKakaoId(kakaoId).get();

        return kakaoMember;
    }
}