package jp.co.canon.cks.eec.fs.rssportal.common;

import java.util.List;

public class Tools {

    static public int getCollectTypeNumber(String collectType) {
        if(collectType==null || collectType.isEmpty()) {
            return -1;
        }
        switch (collectType) {
            case "cycle": return 1;
            case "continuous": return 2;
            default:
        }
        return -1;
    }

    static public String toCSVString(List<String> list) {
        StringBuilder sb = new StringBuilder("");
        boolean comma = false;
        for(String item: list) {
            if(comma==false) {
                comma = true;
            } else {
                sb.append(",");
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
