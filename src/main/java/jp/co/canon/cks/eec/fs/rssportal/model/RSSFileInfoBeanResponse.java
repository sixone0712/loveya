package jp.co.canon.cks.eec.fs.rssportal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RSSFileInfoBeanResponse extends RSSFileInfoBean{
    private String structId;
    private String targetName;
    private String logName;
}
