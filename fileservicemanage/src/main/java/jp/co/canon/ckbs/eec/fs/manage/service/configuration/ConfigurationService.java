package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

import jp.co.canon.ckbs.eec.fs.configuration.Category;

public interface ConfigurationService {
    Machine[] getMachineList();
    Category[] getCategories(String machineName);
    String[] getAllFileServiceHost();
    String getFileServiceHost(String machineName);
    String getFileServiceDownloadUrlPath(String machineName, String filePath);
}
