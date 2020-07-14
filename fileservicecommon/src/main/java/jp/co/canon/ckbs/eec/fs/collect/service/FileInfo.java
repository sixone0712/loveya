package jp.co.canon.ckbs.eec.fs.collect.service;

import lombok.Getter;
import lombok.Setter;

public class FileInfo {
    @Getter @Setter
    private String type;

    @Getter @Setter
    private String filename;

    @Getter @Setter
    private long size;

    @Getter @Setter
    private String timestamp;
}
