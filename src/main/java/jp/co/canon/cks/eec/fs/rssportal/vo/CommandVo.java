package jp.co.canon.cks.eec.fs.rssportal.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CommandVo {

    private int id;
    private String cmd_name;
    private String cmd_type;
    private Date created;
    private Date modified;
    private boolean validity;

}
