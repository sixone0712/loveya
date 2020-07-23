package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class DefaultLogCommandDefinitionModel implements LogCommandDefinitionModel {
	/** コマンド文字列 */
	@Getter @Setter
	private String command = null;
	/** 定期収集時のコマンド文字列 */
	@Getter @Setter
	private String commandCA = null;
	/** ログID */
	@Getter @Setter
	private String logId = null;

	@Getter @Setter
	private LogUrlModel logUrl = null;
}
