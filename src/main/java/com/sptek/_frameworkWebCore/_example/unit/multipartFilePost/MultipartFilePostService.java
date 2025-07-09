package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import com.sptek._frameworkWebCore.util.FileUtil;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import com.sptek._projectCommon.commonObject.dto.PostBaseDto;
import com.sptek._projectCommon.commonObject.dto.UploadFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    public void setCurrentUserInfo(PostBaseDto postBaseDto) throws Exception {
        if (AuthenticationUtil.isRealLogin()) {
            postBaseDto.setUserId(AuthenticationUtil.getMyId());
            postBaseDto.setUserName(AuthenticationUtil.getMyName());
            postBaseDto.setUserEmail(AuthenticationUtil.getMyEmail());
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

        // 전체 파일 용량 제한: 5MB
        long maxTotalSize = 5 * 1024 * 1024;
        long totalSize = multipartFiles.stream()
                .peek(multipartFile -> {
                    if (!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image/")) {
                        throw new ServiceException(ServiceErrorCodeEnum.FILE_UPLOAD_ERROR, "이미지 파일만 업로드 가능합니다.");
                    }
                })
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > maxTotalSize) {
            throw new ServiceException(ServiceErrorCodeEnum.PAYLOAD_TOO_LARGE_ERROR,
                    "전체 파일 크기 제한을 초과했습니다. (최대 " + maxTotalSize / (1024 * 1024) + " M)");
        }

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

        // 조건 별 경로 구성
        //Path postOwnFilePath = getPostOwnFilePathForAnyone(postBaseDto);
        //Path postOwnFilePath = getPostOwnFilePathForLogin(postBaseDto);
        Path postOwnFilePath = getPostOwnFilePathForUser(postBaseDto);
        //Path postOwnFilePath = getPostOwnFilePathForRole(postBaseDto, Set.of("ROLE_ADMIN", "ROLE_ADMIN_SPECIAL", "ROLE_SYSTEM"));
        //Path postOwnFilePath = getPostOwnFilePathForAuth(postBaseDto, Set.of(AuthorityEnum.AUTH_SPECIAL_FOR_TEST, AuthorityEnum.AUTH_RETRIEVE_USER_ALL_FOR_MARKETING));

        Path realPostFilePath = SecurityUtil.getStorageRootPath(postOwnFilePath).resolve(postOwnFilePath);
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

    public Path getPostOwnFilePathForAnyone(PostBaseDto postBaseDto) {
        return buildFilePath(SecurityUtil.getSecuredFilePathForAnyone(), postBaseDto);
    }

    public Path getPostOwnFilePathForLogin(PostBaseDto postBaseDto) {
        return buildFilePath(SecurityUtil.getSecuredFilePathForLogin(), postBaseDto);
    }

    public Path getPostOwnFilePathForUser(PostBaseDto postBaseDto) throws Exception {
        return buildFilePath(SecurityUtil.getSecuredFilePathForUser(), postBaseDto);
    }

    public Path getPostOwnFilePathForRole(PostBaseDto postBaseDto, Set<String> roles) throws Exception {
        return buildFilePath(
                SecurityUtil.getSecuredFilePathForRole(roles),
                postBaseDto
        );
    }

    public Path getPostOwnFilePathForAuth(PostBaseDto postBaseDto, Set<AuthorityEnum> authorities) throws Exception {
        return buildFilePath(
                SecurityUtil.getSecuredFilePathForAuth(authorities),
                postBaseDto
        );
    }

    private Path buildFilePath(Path secureFilePath, PostBaseDto postBaseDto) {
        Path extraFilePath = Path.of(
                postBaseDto.getBoardName(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                String.valueOf(postBaseDto.getPostId())
        );
        return secureFilePath.resolve(extraFilePath); // File DB에 저장되는 경로
    }
}
