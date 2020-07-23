package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class DefaultFileInfoModel implements FileInfoModel {
	@Getter @Setter
	private String name = null;

	@Getter @Setter
	private long size = 0;

	@Getter @Setter
	private String timestamp = null;

	@Getter @Setter
	private String type = FILE;

	@Getter @Setter
	private boolean exists = true;
	
	public DefaultFileInfoModel() {
		this("unkonwn", 0);
	}
	
	public DefaultFileInfoModel(String name, long size) {
		setName(name);
		setSize(size);
	}
}
