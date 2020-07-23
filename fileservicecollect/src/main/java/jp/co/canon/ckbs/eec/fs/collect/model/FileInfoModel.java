package jp.co.canon.ckbs.eec.fs.collect.model;

import java.util.Date;

public interface FileInfoModel {
	static final String DIRECTORY = "D";
	static final String FILE = "F";
	
	public String getName();
	public long getSize();
	public String getTimestamp();
	public String getType();
	public boolean isExists();
}
