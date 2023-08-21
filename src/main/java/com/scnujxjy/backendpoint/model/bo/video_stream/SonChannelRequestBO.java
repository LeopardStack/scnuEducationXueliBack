package com.scnujxjy.backendpoint.model.bo.video_stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SonChannelRequestBO {

    private String nickname;

    private String role;

    private String actor;

    private String passwd;

}
