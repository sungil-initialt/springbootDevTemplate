package com.sptek._frameworkWebCore._example.dto.IF;

import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
public abstract class MultiPartPostBaseIF extends PostBaseIF{
    private String rootDir = (String) SpringUtil.getApplicationProperty("storage.localMultipartFilesBasePath");
    private Path baseFilePath;
    private Path finalFilePath;
    private List<String> savedFileNames;

    public abstract Path getBaseFilePath();
    public abstract Path getFinalFilePath();
}
