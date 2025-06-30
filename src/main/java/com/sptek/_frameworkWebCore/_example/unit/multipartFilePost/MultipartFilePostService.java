package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import com.sptek._frameworkWebCore.springSecurity.spt.CustomUserDetails;
import com.sptek._frameworkWebCore.util.FileUtil;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import com.sptek._projectCommon.commonDtos.PostBaseDto;
import com.sptek._projectCommon.commonDtos.UploadFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service

public class MultipartFilePostService {

    private final MyBatisCommonDao myBatisCommonDao;
    @Value("${storage.multipartFile.localRootFilePath}")
    private String localRootFilePath;

    @Transactional(readOnly = false)
    public ExamplePostDto createPost(ExamplePostDto examplePostDto, List<MultipartFile> multipartFiles) throws Exception {
        //임시로 셋팅
        examplePostDto.setBoardId(1);
        examplePostDto.setBoardName("POST_EX");

        //작성자 정보 셋팅
        setCurrentUserInfo(examplePostDto);
        
        //text insert
        this.insertExamplePostDto(examplePostDto);

        //파일과 관련한 처리 후 UploadFileDtos 재 설정
        examplePostDto.setUploadFileDtos(this.doFileJob(examplePostDto, multipartFiles));

        //UploadFileDtos insert
        this.insertUploadFileDtos(examplePostDto.getUploadFileDtos());
        return examplePostDto;
    }
    
    public void setCurrentUserInfo(PostBaseDto postBaseDto) {
        // SecurityUtil.getUserAuthentication().isAuthenticated() 을 사용 하지 않는 이유는 spring-security 가 비 로그인 상태도 anonymousUser 상태의 로그인 으로 간주 하는게 default 이기 때문
        // 디볼트 동작 으로 인한 장점이 있기 때문에.. 그대로 유지 시킴
        if (!CommonConstants.ANONYMOUS_USER.contains(SecurityUtil.getUserAuthentication().getPrincipal().toString())) {
            postBaseDto.setUserId(((CustomUserDetails) SecurityUtil.getUserAuthentication().getPrincipal()).getUserDto().getId());
            postBaseDto.setUserName(((CustomUserDetails) SecurityUtil.getUserAuthentication().getPrincipal()).getUserDto().getName());
            postBaseDto.setUserEmail(((CustomUserDetails) SecurityUtil.getUserAuthentication().getPrincipal()).getUserDto().getEmail());
        }
    }

    public List<UploadFileDto> doFileJob(PostBaseDto postBaseDto, List<MultipartFile> orignMultipartFiles) throws Exception {

        // 공통 작업 -------
        // uploadFileDtos 가 없는 경우 새로 생성
        List<UploadFileDto> uploadFileDtos = Optional.ofNullable(postBaseDto.getUploadFileDtos())
                .orElseGet(ArrayList::new);
        postBaseDto.setUploadFileDtos(uploadFileDtos);

        // multipartFiles 가 없는 경우 새로 생성
        List<MultipartFile> multipartFiles = Optional.ofNullable(orignMultipartFiles)
                .orElseGet(ArrayList::new);

        // 각 파일 들의 개별 크기 및 타입 확인
        long maxSize = 5 * 1024 * 1024;
        multipartFiles.forEach(multipartFile -> {
            if (!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image/")) {
                throw new ServiceException(ServiceErrorCodeEnum.MULTIPARTFILE_UPLOAD_ERROR, "이미지 파일만 업로드 가능 합니다.");
            }

            if (multipartFile.getSize() > maxSize) {
                throw new ServiceException(ServiceErrorCodeEnum.MULTIPARTFILE_UPLOAD_ERROR, "파일 크기 제한에 초과 되었 습니다. (최대 " + maxSize + ")");
            }
        });

        // 공용 변수들
        final Integer uploadFileDtoMaxFileOrder; //null 인 경우는 평가를 할수 없는 경우임
        if (uploadFileDtos.isEmpty()) {
            uploadFileDtoMaxFileOrder = 0;

        } else if (uploadFileDtos.stream().anyMatch(dto -> dto.getFileOrder() == null)) {
            uploadFileDtoMaxFileOrder = null; // null 포함 → 평가 불가

        } else {
            Set<Integer> unique = new HashSet<>();
            boolean hasDuplicate = uploadFileDtos.stream()
                    .map(UploadFileDto::getFileOrder)
                    .anyMatch(fileOrder -> !unique.add(fileOrder));

            uploadFileDtoMaxFileOrder = hasDuplicate
                    ? null
                    : uploadFileDtos.stream()
                    .map(UploadFileDto::getFileOrder)
                    .max(Integer::compareTo)
                    .orElse(0);
        }

        // 저장 경로 조합
        Path rootFilePath = Path.of(localRootFilePath);
        Path postOwnFilePath = Path.of(
                postBaseDto.getBoardName()
                , LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                , String.valueOf(postBaseDto.getPostId())
        );
        Path realPostFilePath = rootFilePath.resolve(postOwnFilePath);

        // 멀티 파일의 내용이 uploadFileDtos 에 없으면 uploadFileDtos 에 추가 (fileName 과 fileOrder 값 입력)
        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile multipartFile = multipartFiles.get(i);
            String multipartFileName = multipartFile.getOriginalFilename();
            boolean alreadyExists = uploadFileDtos.stream().anyMatch(uploadFileDto -> multipartFileName != null && multipartFileName.equals(uploadFileDto.getFileName()));

            if (!alreadyExists) {
                UploadFileDto newUploadFileDto = new UploadFileDto();
                newUploadFileDto.setFileName(multipartFileName);

                if (uploadFileDtoMaxFileOrder == null) {
                    newUploadFileDto.setFileOrder(null);
                } else {
                    newUploadFileDto.setFileOrder(uploadFileDtoMaxFileOrder + i);
                }
                uploadFileDtos.add(newUploadFileDto);
            }
        }

        // boardId, postId 셋팅
        uploadFileDtos.forEach(uploadFileDto -> {
            uploadFileDto.setBoardId(postBaseDto.getBoardId());
            uploadFileDto.setPostId(postBaseDto.getPostId());
        });

        // fileOrder 의 평가가 어려운 상태 일때는 모두 null 처리함
        if (uploadFileDtoMaxFileOrder == null) {
            uploadFileDtos.forEach(uploadFileDto -> uploadFileDto.setFileOrder(null));
        }

        // 모든 path 값 지정
        uploadFileDtos.forEach(uploadFileDto -> {
            uploadFileDto.setFilePath(postOwnFilePath.toString());
        });

        // 새로 저장 해야 할 파일이 있으면 저장 dir 생성
        if (!multipartFiles.isEmpty()) {
            Files.createDirectories(realPostFilePath);
        }

        // multipartFile 저장 처리와 uploadFileDto 부가 정보 셋팅
        uploadFileDtos.forEach(uploadFileDto -> {
            Path realPostFilePathName = realPostFilePath.resolve(uploadFileDto.getFileName());

            //uploadFileDto 의 파일 이름이 물리적으로 저장되어 있지 않으면
            if (Files.notExists(realPostFilePathName)) {
                //해당 파일명 과 동일한 이름의 multiPartFile 을 찾아서 uuid 파일명 으로 저장 하고 uploadFileDto 의 파일 명도 uuid 로 변경
                multipartFiles.stream()
                        .filter(multipartFile -> uploadFileDto.getFileName().equals(multipartFile.getOriginalFilename()))
                        .findFirst()
                        .ifPresentOrElse(multipartFile -> {
                            String multipartFileName = FileUtil.extractFileNameOnly(multipartFile.getOriginalFilename());
                            try {
                                String finalFileName = UUID.randomUUID() + multipartFileName.substring(multipartFileName.lastIndexOf("."));
                                multipartFile.transferTo(Path.of(realPostFilePath.toString(), finalFileName));
                                uploadFileDto.setFileName(finalFileName);
                                log.info("파일 저장 완료: {} -> {}", multipartFileName, finalFileName);

                            } catch (IOException e) {
                                throw new RuntimeException(String.format("%s 파일 저장 중 오류 발생", multipartFileName), e);
                            }
                        }, () -> {
                            log.warn("{} 파일이 Multipart 목록에 없음", uploadFileDto.getFileName());
                        });
            }
        });

        //삭졔 되야 할 파일 삭제 처리
        Set<String> fileNamesToKeep = uploadFileDtos.stream()
                .map(UploadFileDto::getFileName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (Files.exists(realPostFilePath) && Files.isDirectory(realPostFilePath)) {
            try (Stream<Path> files = Files.list(realPostFilePath)) {
                files.filter(Files::isRegularFile)
                        .filter(path -> !fileNamesToKeep.contains(path.getFileName().toString()))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                log.debug("{} 삭제됨", path);
                            } catch (IOException e) {
                                log.debug("{} 삭제 실패 : {}", path, e.getMessage());
                            }
                        });
            }
        } else {
            log.debug("디렉토리가 존재하지 않음: {}", realPostFilePath);
        }

        return postBaseDto.getUploadFileDtos();
    }

    public int insertExamplePostDto(ExamplePostDto examplePostDto) {
        return this.myBatisCommonDao.insert("framework_example.insertExamplePostDto", examplePostDto);
    }

    public ExamplePostDto selectExamplePostDto(ExamplePostDto examplePostDto) {
        return this.myBatisCommonDao.selectOne("framework_example.selectExamplePostDto", examplePostDto);
    }

    public int insertUploadFileDtos(List<UploadFileDto> uploadFileDtos) {
        if (uploadFileDtos != null && !uploadFileDtos.isEmpty()) {
            return this.myBatisCommonDao.insert("framework_example.insertUploadFileDtos", uploadFileDtos);
        }
        return 0;
    }

    public List<UploadFileDto> selectUploadFileDtos(ExamplePostDto examplePostDto) {
        return this.myBatisCommonDao.selectList("framework_example.selectUploadFileDtos", examplePostDto);
    }
}
