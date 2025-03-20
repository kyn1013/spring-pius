package org.example.expert.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserProfileResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public UserProfileResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getNickName(), user.getImageUrl());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    // 이미지 업로드
    @Transactional
    public UserProfileResponse uploadImage(long userId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        validImage(image);

        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
            amazonS3.putObject(putObjectRequest); // put image to S3
        }catch (Exception e){
            throw new InvalidRequestException("이미지 처리중 오류가 발생했습니다.");
        }finally {
            byteArrayInputStream.close();
            is.close();
        }

        String imageUrl = amazonS3.getUrl(bucketName, s3FileName).toString();
        user.updateImageUrl(imageUrl);
        User svaedUser = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserProfileResponse(svaedUser.getId(), svaedUser.getEmail(), svaedUser.getNickName(), svaedUser.getImageUrl());
    }

    // 이미지 업로드 검증
    private static void validImage(MultipartFile image) {
        // 이미지가 비어있는지 확인
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new InvalidRequestException("이미지 처리중 오류가 발생했습니다.");
        }
        int lastDotIndex = image.getOriginalFilename().lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new InvalidRequestException("이미지 처리중 오류가 발생했습니다.");
        }

        // 확장자 검증
        String extention = image.getOriginalFilename().substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new InvalidRequestException("이미지 처리중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void deleteImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        try{
            URL url = new URL(imageUrl);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            String key = decodingKey.substring(1); // 맨 앞의 '/' 제거
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new InvalidRequestException("이미지 처리중 오류가 발생했습니다.");
        }
        user.updateImageUrl(null);
    }

    public UserProfileResponse searchUser(String nickName) {
        User user = userRepository.findByNickName(nickName).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserProfileResponse(user.getId(), user.getEmail(), user.getNickName(), user.getImageUrl());
    }
}
