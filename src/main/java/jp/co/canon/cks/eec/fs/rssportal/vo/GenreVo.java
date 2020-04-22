package jp.co.canon.cks.eec.fs.rssportal.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GenreVo {
    private int id;
    private String name;
    private String category;
    private Date created;
    private Date modified;
    private boolean validity;
}
