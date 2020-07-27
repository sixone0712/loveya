package jp.co.canon.cks.eec.fs.rssportal.model.vftp;

import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import lombok.Getter;
import lombok.Setter;

public class VFtpFileInfoExtends extends VFtpFileInfo {
    @Getter @Setter
    private String fabName;

    @Getter @Setter
    private String machineName;

    @Getter @Setter
    private String command;
}
