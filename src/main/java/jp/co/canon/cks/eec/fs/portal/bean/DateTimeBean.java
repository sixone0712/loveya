/**
 * Title: Equipment Engineering Support System.
 *         - Log Upload Service
 *
 * File : DateTimeBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 * 2011/01/06 メソッドコメント追加。setXXX()の例外処理の追加。
 * 2011/01/08 setXXX()でエラーになる場合に、入力された文字をそのまま設定するように修正した。
 *            日付の場合は、getDate()のみエラーになる入力をそのまま出力するように修正した。
 * 2011/01/12 setDate()の判定を厳密に行うように修正した。
 */
package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 日付時間を保持するJavaBeans
 * @author Tomokazu OKUMURA
 * ======================================================================
 * Change History:
 * 2011-01-13	T.Tateyama	クラス変数のスコープを変更する
 */
public class DateTimeBean implements Serializable {

	/** シリアライズバージョンID */
	private static final long serialVersionUID = 7874682380149190961L;
	/** 日付フォーマット文字列 */
    private final String DATE_PATTERN = "yyyy/MM/dd";
    /** ゼロ埋めの書式化定義(4桁) */
    private final DecimalFormat N4 = new DecimalFormat("0000");
    /** ゼロ埋めの書式化定義(2桁) */
    private final DecimalFormat N2 = new DecimalFormat("00");

    /** 年 */ 
    private String year_;
    /** 月 */
    private String month_;
    /** 日 */ 
    private String day_;
    /** 時 */
    private String hour_;
    /** 分 */
    private String minute_;
    /** 秒 */
    private String second_;
    /** ミリ秒 */
    private String msec_;
    /** 日付 */
    protected String date_;

    /**
     * デフォルトコンストラクタ　現在時刻で日付を初期化する
     */
    public DateTimeBean() {
        Calendar cal = Calendar.getInstance();
        setYear(String.valueOf(cal.get(Calendar.YEAR)));
        setMonth(String.valueOf(cal.get(Calendar.MONTH) + 1));
        setDay(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        setHour(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
        setMinute(String.valueOf(cal.get(Calendar.MINUTE)));
        setSecond(String.valueOf(cal.get(Calendar.SECOND)));
        setMsec(String.valueOf(cal.get(Calendar.MILLISECOND)));
    }

    /**
     * 年を取得します。
     * @return 年
     */
    public String getYear() {
        return year_;
    }

    /**
     * 年をint型で取得します。
     * @return 年
     */
    public int getIntYear() {
        try {
            return Integer.parseInt(year_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 年を設定します。
     * @param year 年
     */
    public void setYear(String year) {
        int value = 0;
        try {
            value = Integer.parseInt(year);
            this.year_ = N4.format(value);
        } catch (NumberFormatException ex) {
            this.year_ = year;
        }
    }

    /**
     * 月を取得します。
     * @return 月
     */
    public String getMonth() {
        return month_;
    }

    /**
     * 月をint型で取得します。
     * @return 月
     */
    public int getIntMonth() {
        try {
            return Integer.parseInt(month_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 月を設定します。
     * @param month 月
     */
    public void setMonth(String month) {
        int value = 0;
        try {
            value = Integer.parseInt(month);
            this.month_ = N2.format(value);
        } catch (NumberFormatException ex) {
            this.month_ = month;
        }
    }

    /**
     * 日を取得します。
     * @return 日
     */
    public String getDay() {
        return day_;
    }

    /**
     * 日をint型で取得します。
     * @return 日
     */
    public int getIntDay() {
        try {
            return Integer.parseInt(day_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 日を設定します。
     * @param day 日
     */
    public void setDay(String day) {
        int value = 0;
        try {
            value = Integer.parseInt(day);
            this.day_ = N2.format(value);
        } catch (NumberFormatException ex) {
            this.day_ = day;
        }
    }

    /**
     * 時を取得します。
     * @return 時
     */
    public String getHour() {
        return hour_;
    }

    /**
     * 時をint型で取得します。
     * @return 時
     */
    public int getIntHour() {
        try {
            return Integer.parseInt(hour_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 時を設定します。
     * @param hour 時
     */
    public void setHour(String hour) {
        int value = 0;
        try {
            value = Integer.parseInt(hour);
            this.hour_ = N2.format(value);
        } catch (NumberFormatException ex) {
            this.hour_ = hour;
        }
    }

    /**
     * 分を取得します。
     * @return 分
     */
    public String getMinute() {
        return minute_;
    }

    /**
     * 分をint型で取得します。
     * @return 分
     */
    public int getIntMinute() {
        try {
            return Integer.parseInt(minute_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 秒を設定します。
     * @param minute 秒
     */
    public void setMinute(String minute) {
        int value = 0;
        try {
            value = Integer.parseInt(minute);
            this.minute_ = N2.format(value);
        } catch (NumberFormatException ex) {
            this.minute_ = minute;
        }
    }

    /**
     * 秒を取得します。
     * @return 秒
     */
    public String getSecond() {
        return second_;
    }

    /**
     * 秒をint型で取得します。
     * @return 秒
     */
    public int getIntSecond() {
        try {
            return Integer.parseInt(second_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 秒を設定します。
     * @param sec 秒
     */
    public void setSecond(String sec) {
        int value = 0;
        try {
            value = Integer.parseInt(sec);
            this.second_ = N2.format(value);
        } catch (NumberFormatException ex) {
            this.second_ = sec;
        }
    }

    /**
     * ミリ秒を取得します。
     * @return ミリ秒
     */
    public String getMsec() {
        return msec_;
    }

    /**
     * ミリ秒をint型で取得します。
     * @return ミリ秒
     */
    public int getIntMsec() {
        try {
            return Integer.parseInt(msec_);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * ミリ秒を設定します。
     * @param msec ミリ秒
     */
    public void setMsec(String msec) {
        this.msec_ = msec;
    }

    /**
     * 日付をyyyy/MM/dd形式で取得します。
     * @return 日付
     */
    public String getDate() {
        String value = year_ + "/" + month_ + "/" + day_;
        if (date_ != null) {
            value = date_;
        }
        return value;
    }

    /**
     * 日付と時間をyyyyMMddHHmmss形式で取得します。
     * @return 日付
     */
    public String getDateTime() {
        String value = year_ + month_ + day_ + hour_ + minute_ + second_;
        return value;
    }

    /**
     * 日付を設定します。
     * @param date 日付
     */
    public void setDate(String date) {
        // 2010.12.22 英語ロケールでも問題出ないようにフォーマットを固定する
        //Locale.setDefault(Locale.ENGLISH);
        //DateFormat df = SimpleDateFormat.getDateInstance();
        DateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        ParsePosition pos = new ParsePosition(0);
        df.setLenient(false);   // 日付判定を厳密に行う

        Date newDate = df.parse(date, pos);
        if (pos.getErrorIndex() != -1 || date.length() > 10) {
            // システム日付を設定
            newDate = Calendar.getInstance().getTime();
            this.date_ = date;
            return;
        }
        this.date_ = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(newDate);
        year_ = N4.format(cal.get(Calendar.YEAR));
        month_ = N2.format(cal.get(Calendar.MONTH) + 1);
        day_ = N2.format(cal.get(Calendar.DAY_OF_MONTH));
    }
}
