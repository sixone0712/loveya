package jp.co.canon.ckbs.eec.fs.collect.service;

import lombok.Getter;
import lombok.Setter;

public class VFtpFileInfo {
    @Getter @Setter
    private String fileType;

    @Getter @Setter
    private String fileName;

    @Getter @Setter
    private long fileSize;
}
