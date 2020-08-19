#!/bin/sh
#
# 名称：
#    service_start - OTS起動スクリプト
#

#
# サービス開始関数
#
ServiceStart() {
	# PostgreSQL開始
	echo "starting postgresql..."
	systemctl start postgresql

	RET=$?
	if [ ${RET} -ne 0 ]
	then
	        return ${RET}
	fi

	# ees-ftp開始
	echo "starting ees-ftp..."
	systemctl start ees-ftp

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# fcs開始
	echo "starting fcs..."
	systemctl start fcs

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# ees-fw開始
	echo "starting ees-fw..."
	systemctl start ees-fw

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# tomcat開始
	echo "starting tomcat..."
	systemctl start tomcat

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# tomcat9 Startup
	echo "starting tomcat9..."
	systemctl start tomcat9
	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# httpd開始
	echo "starting httpd..."
	systemctl start httpd

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

}

#
# サービス停止関数
#
ServiceStop() {
	# httpd停止
	echo "stopping httpd..."
	systemctl stop httpd

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# tomcat9 Shutdown
	echo "stopping tomcat9..."
	systemctl stop tomcat9
	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# tomcat停止
	echo "stopping tomcat..."
	systemctl stop tomcat

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# ees-fw停止
	echo "stopping ees-fw..."
	systemctl stop ees-fw

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# fcs停止
	echo "stopping fcs..."
	systemctl stop fcs

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# ees-ftp停止
	echo "stopping ees-ftp..."
	systemctl stop ees-ftp

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# PostgreSQL停止
	echo "stopping postgresql..."
	systemctl stop postgresql

	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

}


# メイン処理

# ユーザチェック
user=`whoami`
if [ ${user} != "root" ]
then
	echo
	echo "Error: Execution user must be root user !"
	echo
	exit 1
fi

# 引数に応じて開始、停止を見分ける
case "$1" in
	start)
		# サービスの開始
		ServiceStart

		RET=$?
		if [ ${RET} -ne 0 ]
		then
			echo
			echo "Error: Service is not started !"
			echo
			exit 2
		fi
		;;
	stop)
		# サービスの停止
		ServiceStop

		RET=$?
		if [ ${RET} -ne 0 ]
		then
			echo
			echo "Error: Service is not stopped !"
			echo
			exit 2
		fi
		;;
	*)
		echo
		echo "Usage: service_ctrl {start|stop}"
		echo
		;;
esac
