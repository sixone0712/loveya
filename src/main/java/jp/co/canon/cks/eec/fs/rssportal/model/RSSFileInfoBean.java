package jp.co.canon.cks.eec.fs.rssportal.model;

import jp.co.canon.cks.eec.fs.portal.bean.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class RSSFileInfoBean {
    private long fileId;
    /** 状態 */
    private String fileStatus;
    /** ログ種別 */
    private String logId;
    /** ファイル名 */
    private String fileName;
    /** ファイルサイズ */
    private long fileSize;
    /** ファイル日付 */
    private String fileDate;
    /** ファイルパス */
    private String filePath;
    /** ファイルかディレクトリかを示す変数　*/
    private boolean file = true;

    /**
     *	compareToの実装
     *
     * Parameters:
     *　 @param info 対象
     * Result:
     *　 @return 結果
     */
    public final int compareTo(Object info) {
        if (info instanceof RSSFileInfoBean) {
            RSSFileInfoBean temp = (RSSFileInfoBean)info;
            return fileDate.compareTo(temp.fileDate);
        }
        throw new IllegalArgumentException("not support object");
    }

    /**
     *	ファイルサイズをKBで取得します。
     *
     * Result:
     *　 @return ファイルサイズ[KB]
     */
    public final long getSizeKB() {
        BigDecimal decimal = new BigDecimal(String.valueOf(fileSize));
        return decimal.divide(new BigDecimal(Constants.FILESIZE_DIVIDE),
                Constants.FILESIZE_SCALE, BigDecimal.ROUND_UP).longValue();
    }
}
