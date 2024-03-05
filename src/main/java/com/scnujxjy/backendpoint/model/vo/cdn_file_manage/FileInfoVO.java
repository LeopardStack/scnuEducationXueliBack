package com.scnujxjy.backendpoint.model.vo.cdn_file_manage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private boolean isDirectory;
}
