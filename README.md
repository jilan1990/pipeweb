# pipeweb

本工具主要用来将 内网服务端口 映射到 外网的端口(普通端口映射自然也可以完成)

至此完成第一稿，本工具使用十分简单，需要环境安装java

1.首先开启服务端
java -jar luluouter-0.1.0.jar(通过 mvn clean install 打包)
需注意和luluouter-0.1.0.jar同路径下有配置文件

conf/pipe.properties

msg_port=5273

data_port=5266


2.首先开启客户端
java -jar luluinner.jar(通过export打包可运行jar)
需注意和luluouter.jar同路径下有配置文件

conf/pipe.properties

outer_ip=192.168.3.17

msg_port=5273

data_port=5266


其中outer_ip为服务端ip，msg_port和data_port两个端口和服务端配置文件一致即可

3.通过网页http://192.168.3.17:1072/greeting 就可以灵活的配置端口映射关系

欢迎大家和我通过 85045795@qq.com 交流
