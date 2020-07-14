package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

public interface ConfigurationService {
    Machine[] getMachineList();
    Category[] getCategories(String machineName);
    String[] getAllFileServiceHost();
    String getFileServiceHost(String machineName);
    String getFileServiceDownloadUrlPrefix(String machineName);
}
