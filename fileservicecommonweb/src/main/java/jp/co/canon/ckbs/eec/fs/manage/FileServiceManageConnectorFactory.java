package jp.co.canon.ckbs.eec.fs.manage;

public interface FileServiceManageConnectorFactory {
    FileServiceManageConnector getConnector(String host);
}
