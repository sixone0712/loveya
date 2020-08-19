#!/bin/sh
#
# 名称：
#    service_start - ESP起動スクリプト
#

ESP_HOME=/usr/local/canon/esp


#
# サービス開始関数
#
ServiceStart() {
	# tomcat開始
	echo "starting tomcat..."
	systemctl start tomcat
	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi
	
	# tomcat9開始
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
	# tomcat停止
	echo "stopping tomcat..."
	systemctl stop tomcat
	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi
	
	# tomcat9停止
	echo "stopping tomcat9..."
	systemctl stop tomcat9
	RET=$?
	if [ ${RET} -ne 0 ]
	then
		return ${RET}
	fi

	# httpd停止
	echo "stopping httpd..."
	systemctl stop httpd
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
		# tomcatが起動していない場合のみ、ロックファイルの残骸を削除
		systemctl is-active tomcat --quiet
		RET=$?
		if [ ${RET} -ne 0 ]
		then
			rm -vf ${ESP_HOME}/Logs/AccessLogs/*.lck ${ESP_HOME}/Logs/SystemLogs/*.lck
		fi

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
