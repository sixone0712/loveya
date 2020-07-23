package jp.co.canon.ckbs.eec.fs.collect;

public class DefaultFileServiceCollectConnectorFactory implements FileServiceCollectConnectorFactory{
    @Override
    public FileServiceCollectConnector getConnector(String host) {
        return new DefaultFileServiceCollectConnector(host);
    }
}
