package com.scnujxjy.backendpoint.model.vo.platform_message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformMessageVO {

    private List<DownloadMessageVO> downloadMessagePOList = new ArrayList<>();
}
