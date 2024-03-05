package com.scnujxjy.backendpoint.model.bo.cdn_file_manage;

import com.scnujxjy.backendpoint.constant.enums.FILEOperation;
import com.scnujxjy.backendpoint.constant.enums.FileOperationResponse;
import com.scnujxjy.backendpoint.model.vo.cdn_file_manage.FileInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 该类用于与外界需要与本 CDN 目录进行操作的协议类
 */
@Data
@AllArgsConstructor
public class FileCommuBO implements Serializable {
    /**
     * 不同实例与本 CDN 服务 通信 采用不同的消息队列
     */

    private static final long serialVersionUID = 1L;

    public FileCommuBO(){
        this.serialNumber = System.currentTimeMillis();
    }

    /**
     * 序号 用来表示 CDN 与指定实例通信的序号
     */
    Long serialNumber;

    /**
     * 操作 这是一个枚举类 非法的操作是不允许的
     */
    FILEOperation fileOperation;

    /**
     * 响应 这是一个枚举类 用来给应答者回复消息的
     */
    FileOperationResponse fileOperationResponse;

    /**
     * 查询指定相对目录下的文件信息时 所需要指定的这个相对路径 /video/...
     */
    String fileSearchRelativeURL;

    /**
     * 查询、删除指定文件是否存在的 绝对路径  eg. /video/123/dddd.mp4
     */
    String fileAbsoluteURL;

    /**
     * 上传 Minio 文件时，将 Minio 的桶名 + 文件目录存储过来
     * 并且把最后的文件名 哈希后存储
     */
    String uploadFileRelativeURL;

    /**
     * CDN 存储文件资源的路径
     */
    String cdnFileAbsoluteURL;

    /**
     * 响应 查询目录时的返回值 即指定目录下的各个文件、文件夹信息
     */
    List<FileInfoVO> files;

    /**
     * 当响应出现错误时的详细报错信息
     */
    String errorMsg;
}
