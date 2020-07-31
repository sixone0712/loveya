package jp.co.canon.ckbs.eec.fs.collect.model;

import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssListRequest extends FtpRequest{
    @Getter @Setter
    String directory;

    @Getter @Setter
    VFtpFileInfo[] fileList;
}
