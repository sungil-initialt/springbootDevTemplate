package com.sptek._frameworkWebCore.support.MultipartFileUploadSupport;

import com.sptek._frameworkWebCore._example.dto.PostExDto;
import com.sptek._frameworkWebCore._example.dto.FileUploadDto;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import com.sptek._frameworkWebCore.util.FileUtil;
import com.sptek._frameworkWebCore.util.SpringUtil;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service

public class MultipartFilesUploadServiceForEx {
    private final MyBatisCommonDao myBatisCommonDao;

    @Transactional(readOnly = false)
    public PostExDto composeInsertJobs(@Nullable PostExDto postExDto, @Nullable MultipartFile[] multipartFiles) throws Exception {
        checkFileValidation(multipartFiles);

        if(postExDto != null) {
            insertDb(postExDto);
            postExDto = selectDb(postExDto);
        }

        if(multipartFiles != null) {
            postExDto.setSavedFileNames(saveMultipartFiles(multipartFiles, postExDto.getFinalFilePath()));
        }

        return postExDto;
    }

    @Transactional(readOnly = false)
    public List<FileUploadDto> composeUpdateJobs(@Nullable MultipartFile[] multipartFiles) throws Exception {
        return null;
    }

    @Transactional(readOnly = false)
    public List<FileUploadDto> composeDeleteJobs(@Nullable MultipartFile[] multipartFiles) throws Exception {
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

    public void checkFileValidation(@Nullable MultipartFile[] multipartFiles) throws Exception{
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
            String rootDir = (String) SpringUtil.getApplicationProperty("storage.multipartFile.localRootFilePath");

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
