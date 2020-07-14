package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class CreateFtpDownloadRequestParam {
    @Getter @Setter
    String category;

    @Getter @Setter
    String[] fileList;

    @Getter @Setter
    boolean archive;
}
