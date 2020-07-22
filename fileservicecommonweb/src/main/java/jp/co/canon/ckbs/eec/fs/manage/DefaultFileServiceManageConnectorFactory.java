package jp.co.canon.ckbs.eec.fs.manage;

public class DefaultFileServiceManageConnectorFactory implements FileServiceManageConnectorFactory{

    @Override
    public FileServiceManageConnector getConnector(String host) {
        return new DefaultFileServiceManageConnector(host);
    }
}
