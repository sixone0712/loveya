export const REST_API_URL = "/rss/rest"
export const PAGE_REFRESH = "/rss/page/refresh";
export const PAGE_LOGIN = "/rss/page/login";
export const PAGE_MANUAL_FTP = "/rss/page/manual";
export const PAGE_MANUAL_VFTP_COMPAT = "/rss/page/VftpCompat";
export const PAGE_MANUAL_VFTP_SSS = "/rss/page/VftpSss";
export const PAGE_AUTO = "/rss/page/auto";
export const PAGE_AUTO_PLAN_ADD = "/rss/page/auto/plan/add";
export const PAGE_AUTO_PLAN_EDIT = "/rss/page/auto/plan/edit";
export const PAGE_AUTO_STATUS = "/rss/page/auto/status";
export const PAGE_AUTO_DOWNLOAD = "/rss/page/auto/download";
export const PAGE_ADMIN_ACCOUNT = "/rss/page/admin/account";
export const PAGE_ADMIN_DL_HISTORY = "/rss/page/admin/history";

export const PAGE_REFRESH_MANUAL_FTP = PAGE_REFRESH + "?target=" + PAGE_MANUAL_FTP;
export const PAGE_REFRESH_MANUAL_VFTP_COMPAT = PAGE_REFRESH + "?target=" + PAGE_MANUAL_VFTP_COMPAT;
export const PAGE_REFRESH_MANUAL_VFTP_SSS = PAGE_REFRESH + "?target=" + PAGE_MANUAL_VFTP_SSS;
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

export const REST_VFTP = "/rss/api/vftp"
export const REST_VFTP_COMPAT = "/rss/api/vftp/compat"
export const REST_VFTP_COMPAT_POST_DOWNLOAD = REST_VFTP_COMPAT + '/download';

export const REST_PLANS = '/rss/api/plans'
export const REST_PLANS_POST_PLANS = REST_PLANS;
export const REST_PLANS_GET_PLANS = REST_PLANS;
export const REST_PLANS_DELETE_PLANS = REST_PLANS;
export const REST_PLANS_MODIFY_PLAN = REST_PLANS;
export const REST_PLANS_CHANGE_PLAN_STATUS = REST_PLANS;

export const REST_PLANS_GET_FILELIST = REST_PLANS;
export const REST_PLANS_GET_DOWNLOAD_FILE = REST_PLANS;
export const REST_PLANS_DELETE_FILE = REST_PLANS;

export const REST_AUTHS = "/rss/api/auths";
export const REST_AUTHS_GET_LOGIN = REST_AUTHS + "/login"
export const REST_AUTHS_GET_LOGOUT = REST_AUTHS + "/logout"
export const REST_AUTHS_GET_ME = REST_AUTHS + "/me"

export const REST_USERS = "/rss/api/users"
export const REST_USERS_GET_LIST = REST_USERS;
export const REST_USERS_PATCH_CHANGE_PERMISSION = REST_USERS;
export const REST_USERS_PATCH_CHANGE_PASSWORD = REST_USERS;
export const REST_USERS_POST_CREATE_USER = REST_USERS;
export const REST_USERS_POST_DELETE_USER = REST_USERS;

export const REST_HISTORIES = "/rss/api/histories"
export const REST_HISTORIES_GET_DOWNLOAD_LIST = REST_HISTORIES + "/downloads";
export const REST_HISTORIES_POST_DOWNLOAD_ADD = REST_HISTORIES + "/downloads";

