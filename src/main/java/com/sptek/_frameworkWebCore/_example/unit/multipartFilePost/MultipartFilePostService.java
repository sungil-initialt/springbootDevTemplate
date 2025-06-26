package com.sptek._frameworkWebCore._example.unit.multipartFilePost;

import com.sptek._frameworkWebCore._example.dto.ExamplePostDto;
import com.sptek._frameworkWebCore._example.dto.FileUploadDto;
import com.sptek._frameworkWebCore._example.dto.PostExDto;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import com.sptek._frameworkWebCore.util.FileUtil;
import com.sptek._frameworkWebCore.util.SpringUtil;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import com.sptek._projectCommon.commonDtos.PostBaseDto;
import com.sptek._projectCommon.commonDtos.UploadFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service

public class MultipartFilePostService {

    private final MyBatisCommonDao myBatisCommonDao;

    @Transactional(readOnly = false)
    public ExamplePostDto createPost(ExamplePostDto examplePostDto, List<MultipartFile> multipartFiles) throws Exception {
        checkFileValidation(multipartFiles);

        //파일외 정보 저장
        this.myBatisCommonDao.insert("framework_example.insertPostEx", examplePostDto);
        examplePostDto.setUploadFileDtos(this.makeUploadFileDtoReady(examplePostDto, multipartFiles));

        return examplePostDto;
    }

    public List<UploadFileDto> makeUploadFileDtoReady(PostBaseDto postBaseDto, List<MultipartFile> multipartFiles) throws Exception {
        Path rootFilePath = Path.of((String) SpringUtil.getApplicationProperty("storage.localMultipartFilesBasePath"));

        Path postFilePath = Path.of(
                postBaseDto.getBoardName()
                , LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                , String.valueOf(postBaseDto.getPostId())
        );

        Path realPostFilePath = rootFilePath.resolve(postFilePath);
        Files.createDirectories(realPostFilePath);

        Optional.ofNullable(postBaseDto.getUploadFileDtos()).ifPresent(uploadFileDtos ->
                uploadFileDtos.forEach(uploadFileDto -> {
                    uploadFileDto.setPostId(postBaseDto.getPostId());
                    uploadFileDto.setFilePath(postFilePath.toString());

                    Path realPostFilePathName = realPostFilePath.resolve(uploadFileDto.getFileName());
                    if (Files.notExists(realPostFilePathName)) {
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
                })
        );
        return postBaseDto.getUploadFileDtos();
    }


    @Transactional(readOnly = false)
    public List<FileUploadDto> updatePost(@Nullable MultipartFile[] multipartFiles) throws Exception {
        return null;
    }

    @Transactional(readOnly = false)
    public List<FileUploadDto> deletePost(@Nullable MultipartFile[] multipartFiles) throws Exception {
        return null;
    }


    public int insertDb(PostExDto postExDto) {
        return this.myBatisCommonDao.insert("framework_example.insertPostEx", postExDto);
    }

    public int updateDb(PostExDto postExDto) {
        return 0;
    }

    public int deleteDb(PostExDto postExDto) {
        return 0;
    }

    public PostExDto selectDb(PostExDto postExDto) {
        return postExDto;
    }

    public void checkFileValidation(@Nullable List<MultipartFile> multipartFiles) throws Exception{
        long maxSize = 5 * 1024 * 1024;

        if (multipartFiles != null) {
            for(MultipartFile multipartFile : multipartFiles) {
                if (!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image/")) {
                    throw new ServiceException(ServiceErrorCodeEnum.MULTIPARTFILE_UPLOAD_ERROR, "이미지 파일만 업로드 가능 합니다.");
                }

                if (multipartFile.getSize() > maxSize) {
                    throw new ServiceException(ServiceErrorCodeEnum.MULTIPARTFILE_UPLOAD_ERROR, "파일 크기 제한에 초과 되었 습니다. (최대 " + maxSize + ")");
                }
            }
        }
    }


    public List<String> saveMultipartFiles(MultipartFile[] multipartFiles, Path finalFilePath) throws Exception {
        List<String> savedFileNames = new ArrayList<>();

        if(multipartFiles != null) {
            String rootDir = (String) SpringUtil.getApplicationProperty("storage.localMultipartFilesBasePath");

            for (MultipartFile multipartFile : multipartFiles) {
                String originFileName = FileUtil.extractFileNameOnly(multipartFile.getOriginalFilename()); //브라우저에따라 파일명에 경로가 포함되는 경우가 있어 제거 추가
                FileUtil.createDirectories(Path.of(rootDir).resolve(finalFilePath));

                Path finalFile = finalFilePath.resolve(Path.of(originFileName));
                multipartFile.transferTo(finalFile);
                savedFileNames.add(finalFile.toString());
            }
        }
        return  savedFileNames;
    }
}
