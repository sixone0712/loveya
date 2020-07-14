/**
 * 
 */
package jp.co.canon.cks.ees.service.lastpoint;

/**
 * 
　* ファイル上での位置情報
 * 
 * @author Mitsuhiro Masuda
 *
 */
public class FilePosition<T extends ILogFile> implements Comparable<FilePosition<T>> {
	
	private T log;
	private long lineNo = -1;
	
	/**
	 * 
	 * 対象ログを取得します。
	 * 
	 * @return 対象ログ
	 */
	public T getLog() {
		return log;
	}



	/**
	 * 
	 * 対象ログを設定します。
	 * 
	 * @param currentLog 対象ログ
	 */
	public void setLog(T log) {
		this.log = log;
	}

	/**
	 * 
	 * 行番号を取得します。
	 * 
	 * @return 行番号
	 */
	public long getLineNo() {
		return lineNo;
	}

	/**
	 * 
	 * 行番号を設定します。
	 * 
	 * @param lineNo 行番号
	 */
	public void setLineNo(long lineNo) {
		this.lineNo = lineNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "FilePosition [log=" + log + ", lineNo=" + lineNo + "]";
	}


	public int compareTo(FilePosition<T> o) {
		if(getLog().compareTo(o.getLog()) > 0) return 1;
		if(getLog().compareTo(o.getLog()) == 0){
			if(getLineNo() == o.getLineNo()){
				return 0;
			} else if(getLineNo() > o.getLineNo()) {
				return 1;
			}
		}
		return -1;
	}
		
}
