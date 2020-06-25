package jp.co.canon.cks.eec.fs.rssportal.model.Info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSInfoMpa {
	private String fabId = "";
	private String mpaName = "";
	private String mpaType = "";
	//private String collectServerId = "0";		//Not currently in use
	//private String collectHostName = null;	//Not currently in use
}
