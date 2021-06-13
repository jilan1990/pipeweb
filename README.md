# pipeweb

本工具主要用来将 内网服务端口 映射到 外网的端口(普通端口映射自然也可以完成)

至此完成第二稿，本工具使用十分简单，仅需要环境安装java 8以上的jdk


1.编译服务端(luluouter-0.1.0.jar已经附在项目中)

通过 mvn clean install 打包luluouter这个maven工程


2.编译客户端程序(当前可以省略,luluinner.jar已经附在项目中)

将通过export打包可运行jar, 然后将luluinner.jar放到和luluouter-0.1.0.jar相同的路径


3.运行服务端

java -jar luluouter-0.1.0.jar

需注意和luluouter-0.1.0.jar同路径下有配置文件(不配置文件也可以,会使用默认值)

conf/pipe.properties 

outer_msg_port=5273

outer_data_port=5266


显示命令行最后会有以下内容

please try the follow ips:

127.0.0.1

192.168.2.202

192.168.56.1


4.内网下载连接脚本,并且运行

在nat内网运行 wget http://192.168.3.17:1072/luluinner.sh 命令(192.168.3.17需要替换为你自己的服务端IP,从please try the follow ips:下面选择能访问到的ip)

然后执行 sh luluinner.sh运行该脚本实现与服务器的对接


5.通过网页http://192.168.3.17:1072/ 就可以灵活的配置端口映射关系(192.168.3.17需要替换为你自己的服务端IP)


欢迎大家和我通过 jilan1990@gmail.com 交流
