package jp.co.canon.cks.eec.fs.rssportal.model.Infos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSInfosCategory {
    //private int categoryType = 0;
    private String categoryCode = "";
    private String categoryName = "";
    //private String fileListForwarding = null;     //Not currently in use
}
