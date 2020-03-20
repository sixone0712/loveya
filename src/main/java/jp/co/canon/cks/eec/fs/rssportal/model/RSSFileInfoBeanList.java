package jp.co.canon.cks.eec.fs.rssportal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RSSFileInfoBeanList {
    private int totalCnt = 0;
    private List fileList = new ArrayList();

}
