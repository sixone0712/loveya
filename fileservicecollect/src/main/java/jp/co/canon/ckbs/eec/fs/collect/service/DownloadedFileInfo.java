package jp.co.canon.ckbs.eec.fs.collect.service;

import lombok.Getter;
import lombok.Setter;

public class DownloadedFileInfo {
    @Getter @Setter
    private String filename;

    @Getter @Setter
    private long size;
}
