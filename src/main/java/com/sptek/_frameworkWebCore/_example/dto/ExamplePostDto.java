package com.sptek._frameworkWebCore._example.dto;

import com.sptek._frameworkWebCore._example.dto.IF.MultiPartPostBaseIF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.file.Path;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class ExamplePostDto extends MultiPartPostBaseIF {
    private String title;
    private String content;

    @Override
    public Path getBaseFilePath() {
        String categoryDir = "exPost";

        return Path.of(categoryDir
                , String.valueOf(LocalDate.now().getYear())
                , String.valueOf(LocalDate.now().getMonthValue())
                , String.valueOf(LocalDate.now().getDayOfMonth()));
    }

    @Override
    public Path getFinalFilePath() {
        return getBaseFilePath().resolve(Path.of(String.valueOf(getPostId() == null ? "" : getPostId())));
    }
}