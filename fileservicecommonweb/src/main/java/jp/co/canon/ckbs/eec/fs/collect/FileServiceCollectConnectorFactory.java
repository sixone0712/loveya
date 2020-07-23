package jp.co.canon.ckbs.eec.fs.collect;

public interface FileServiceCollectConnectorFactory {
    FileServiceCollectConnector getConnector(String host);
}
