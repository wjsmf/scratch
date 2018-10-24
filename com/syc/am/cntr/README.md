项目文件和 Tomcat 安装文件分离开.假设把 test 项目放在 D 盘的根目录下:
在 conf\Catalina\localhost 目录下新建一个名为 test1 的 XML 文件,内容 <Context docBase="D:\test1"/>
在地址栏输入：http://localhost:8080/test1/index.html 就可以访问项目的 index.html 文件了
虚拟目录的路径名可以任意,并不一定要和项目名称相同.
现在你已经购买了云服务器和域名,并做好了解析.你想要通过你的域名www.localhost.cn访问你的网站,该怎么做呢?
你需要在 server.xml 文件的 <Engine>标签下新建一个<Host>标签,内容如下：

	<Host name="www.localhost.cn" appBase="webapps" unpackWARs="true" autoDeploy="true">
		<Context path="" docBase="D:\test"/>
	</Host>

再将 server.xml 中的第一个<Connector>标签改为如下所示,也就是把 8080 端口改为 80

<Connector port="80" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />

总结一下,就是新建了一个主机(Host) ,主机名为 www.localhost.cn
,因为 http 协议默认采用 80
端口,所以不用端口号了,在不给出任何路径时,就默认访问 D
盘下的 test 项目,至于访问 test 项目的那个页面这还需要指定.
一般是需要在对应 web 项目的 web.xml 文件中添加如下代码,就会默认访问 index.html 的页面,但是 Tomcat 的 web.xml 文件已经写好了,所以并不需要另外指定.

	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
	</welcome-file-list>

原来需要通过http://www.localhost.cn:8080/项目名/首页名访问首页的,现在只需要通过www.localhost.cn 就能访问了.
