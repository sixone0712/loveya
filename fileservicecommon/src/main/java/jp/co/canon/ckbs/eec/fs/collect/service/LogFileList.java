package jp.co.canon.ckbs.eec.fs.collect.service;

import lombok.Getter;
import lombok.Setter;

public class LogFileList {
    @Getter @Setter
    private FileInfo[] list = null;

    @Getter @Setter
    private String errorCode = null;

    @Getter @Setter
    private String errorMessage = null;
}
