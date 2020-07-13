export const REST_API_URL = "/rss/rest"
export const PAGE_REFRESH = "/rss/page/refresh";
export const PAGE_LOGIN = "/rss/page/login";
export const PAGE_MANUAL = "/rss/page/manual";
export const PAGE_MANUAL2 = "/rss/page/VftpCompat";
export const PAGE_MANUAL3 = "/rss/page/VftpSss";
export const PAGE_AUTO = "/rss/page/auto";
export const PAGE_AUTO_PLAN_ADD = "/rss/page/auto/plan/add";
export const PAGE_AUTO_PLAN_EDIT = "/rss/page/auto/plan/edit";
export const PAGE_AUTO_STATUS = "/rss/page/auto/status";
export const PAGE_AUTO_DOWNLOAD = "/rss/page/auto/download";
export const PAGE_ADMIN_ACCOUNT = "/rss/page/admin/account";
export const PAGE_ADMIN_DL_HISTORY = "/rss/page/admin/history";

export const PAGE_REFRESH_MANUAL = PAGE_REFRESH + "?target=" + PAGE_MANUAL;
export const PAGE_REFRESH_AUTO_PLAN_ADD = PAGE_REFRESH + "?target=" + PAGE_AUTO_PLAN_ADD;
export const PAGE_REFRESH_AUTO_PLAN_EDIT = PAGE_REFRESH + "?target=" + PAGE_AUTO_PLAN_EDIT;
export const PAGE_REFRESH_AUTO_STATUS = PAGE_REFRESH + "?target=" + PAGE_AUTO_STATUS;
export const PAGE_REFRESH_ADMIN_ACCOUNT = PAGE_REFRESH + "?target=" + PAGE_ADMIN_ACCOUNT;
export const PAGE_REFRESH_ADMIN_DL_HISTORY = PAGE_REFRESH + "?target=" + PAGE_ADMIN_DL_HISTORY;

export const REST_INFOS = "/rss/api/infos";
export const REST_INFOS_GET_FABS =  REST_INFOS + "/fabs";
export const REST_INFOS_GET_MACHINES =  REST_INFOS + "/machines";
export const REST_INFOS_GET_CATEGORIES =  REST_INFOS + "/categories";

export const REST_FTP = "/rss/api/ftp"
export const REST_FTP_POST_FILELIST = REST_FTP;
export const REST_FTP_POST_DOWNLOAD = REST_FTP + '/download';
export const REST_FTP_DELETE_DOWNLOAD = REST_FTP + '/download';



