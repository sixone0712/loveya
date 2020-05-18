package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.util.ArrayList;

public class GetFileParamList {
    private ArrayList<GetFileParam> list = new ArrayList<>();
    public void add(String server, String path, String filename){
        GetFileParam param = new GetFileParam();
        param.setServer(server);
        param.setPath(path);
        param.setFilename(filename);
        list.add(param);
    }

    public GetFileParam[] toArray(){
        return list.toArray(new GetFileParam[0]);
    }
}