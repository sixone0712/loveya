

package jp.co.canon.cks.eec.fs.portal.bean;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogInfoBeanPortal {

    /** シリアルバージョンＩＤ */
    private static final long serialVersionUID = 8320653571273628372L;
    /** 収集ファイル検索時の検索種別 */
    private int logType;
    /** コンポーネント種別ID(2桁) */
    private String code;
    /** コンポーネント名前 */
    private String logName;
    /**　転送先のサーブレット名 */
    private String fileListForwarding = null;
}
