# docker安装
 windows上按照安装向导的提示,一路点击"next"即可;
 
# linux centos 下安装docker 

* **docker容器安装**
RedHat/CentOS必须要6.6版本以上,或者7.x才能安装docker,建议在RedHat/CentOS 7上使用docker,因为RedHat/CentOS 7的内核升级到了kernel 3.10,对lxc容器支持更好.

centos6.6安装docker
rpm -ivh http://mirrors.yun-idc.com/epel/6Server/x86_64/epel-release-6-8.noarch.rpm
 
yum install docker    
 
chkconfig docker on
 
service docker start
 
docker version

4. 开机启动
   
        chkconfig --add --level 245 docker
        service docker status
5. 要是出现docker dead but subsys locked 执行
   
        rm /var/run/docker.*
        rm /var/lock/subsys/docker
        rm -rf /var/lib/docker/*
6. rpm手动下载安装
   
        yum install https://get.docker.com/rpm/1.7.1/centos-6/RPMS/x86_64/docker-engine-1.7.1-1.el6.x86_64.rpm 
        service docker start
7. docker镜像加速
   
          sudo echo "DOCKER_OPTS=\"\$DOCKER_OPTS  --registry-mirror=http://hub-mirror.c.163.com\"" >>  
        /etc/default/docker
        service docker restart 
   
8. 启动遇到的问题
   问题：Docker#docker dead but pid file exists
   解决方法：

        yum-config-manager --enable public_ol6_latest
        yum install device-mapper-event-libs
    
9. 无法删除状态为Dead的container,或者 docker 磁盘问题：device or resource busy
 
            umount /var/lib/docker/devicemapper/mnt/<containerId>

# docker容器

  可以通过<a>docker ps</a>查看正在运行的容器,通过<a>docker ps -a</a>查看所有容器(包括非运行状态).
  可以通过<a>docker rm</a> 容器name/id 删除容器


# docker镜像

  通过<a>docker images</a>查看有哪些正常镜像,<a>docker images -a</a>查看所有镜像(包括中间镜像)
  通过<a>docker rmi 镜像name/id</a> 删除镜像


容器创建好了,中途退出该容器,但该容器在运行,想进入该容器就不应用docker run,而是

        docker exec -it containerName/id command

# 制作镜像
1. 写dockerfile,用"docker build -t 镜像名字 . "命令创建镜像
1. Maybe you need to write a .dockerignore file to prevent extra files from being sent to docker daemon, the .dockerignore file contents are paths relative to .dockerignore of the files which need to be ignored, one item per line.
2. Dockerfile文件,for example:

        FROM registry.youcomp.com/base-images
        COPY resource-on-the-host/ /path-on-the-image..
        COPY resource1-on-the-host/ /path1-on-the-image..
        COPY init.sh /opt/
        EXPOSE 8080
        CMD ["sh", "/opt/init.sh"]

    如上例中复制一个init.sh到镜像中,然后在最后一行中使用,这样的好处是可以一次执行多条命令,也不用设置环境变量,而是直接在init.sh中定义变量就可以.
    init.sh的内容:

        #!/bin/sh
        R CMD Rserve --RS-enable-remote --no-save
        sh /opt/apache-tomcat-8.5.24/bin/startup.sh
        tail -f /opt/apache-tomcat-8.5.24/logs/catalina.out

    init.sh中最后一行的tail -f,目的是当以-d参数启动容器时容器可以一直运行,如果没有最后的那行tail -f,当以-d参数启动容器后,容器会exit;






3. 在容器内安装了东西,执行docker commit containerId newImageName ,可以将这个容器固化到一个新镜像文件中

4. docker run -d --add-host local.db:10.10.176.157 --add-host local.memcached:10.10.248.193  -v /logs:/opt/apache-tomcat-8.5.24/logs/ -p 8081:8080 09fbb4832817

# 搭建私服

1.  Registry的部署
运行下面命令获取registry镜像,

        sudo docker pull registry:2.1.1

然后启动一个容器,

    sudo docker run -d -v /opt/registry:/var/lib/registry -p 5000:5000 --restart=always --name registry registry:2.1.1

Registry服务默认会将上传的镜像保存在容器的/var/lib/registry,将主机的/opt/registry目录挂载到该目录,即可实现将镜像保存到主机的/opt/registry目录了.
 运行docker ps看一下容器情况,

     sudo docker ps
    CONTAINER ID  IMAGE  COMMAND   CREATED  STATUS PORTS    NAMES
    f3766397a458 registry:2.1.1 "/bin/registry /etc/d" 46 seconds ago Up 45 seconds 0.0.0.0:5000->5000/tcp registry

说明registry服务已经启动了,打开浏览器输入<a>http://127.0.0.1:5000/v2</a>,出现{}说明registry运行正常


2. 验证
现在将镜像push到registry来验证一下.
我的机器上有个hello-world的镜像,我们要通过docker tag将该镜像标志为要推送到私有仓库,

        sudo docker tag hello-world 127.0.0.1:5000/hello-world

然后查看以下本地的镜像,
```

 sudo docker images
REPOSITORY  TAG  IMAGE ID  CREATED VIRTUAL  SIZE
registry 2.1.1     b91f745cd233 5 days ago  220.1 MB
ubuntu  14.04      a5a467fddcb8 6 days ago  187.9 MB
hello-world latest 975b84d108f1 2 weeks ago 960 B
127.0.0.1:5000/hello-world latest 975b84d108f1 2 weeks ago 960 B
```
接下来,我们运行docker push将hello-world镜像push到我们的私有仓库中,
```
 sudo docker push 127.0.0.1:5000/hello-world
The push refers to a repository [127.0.0.1:5000/hello-world] (len: 1)
975b84d108f1: Image successfully pushed 
3f12c794407e: Image successfully pushed 
latest: digest: sha256:1c7adb1ac65df0bebb40cd4a84533f787148b102684b74cb27a1982967008e4b size: 2744
```
现在查看本地/opt/registry目录下已经有了刚推送上来的hello-world.也可以在浏览器中输入http://127.0.0.1:5000/v2/_catalog,
出现 {"repositories":[]}
 
现在我们可以先将我们本地的127.0.0.1:5000/hello-world和hello-world先删除掉,
```
 sudo docker rmi hello-world
 sudo docker rmi 127.0.0.1:5000/hello-world
```
然后使用docker pull从我们的私有仓库中获取hello-world镜像,

        sudo docker pull 127.0.0.1:5000/hello-world
        Using default tag: latest
        latest: Pulling from hello-world
        b901d36b6f2f: Pull complete 
        0a6ba66e537a: Pull complete 
        Digest: sha256:1c7adb1ac65df0bebb40cd4a84533f787148b102684b74cb27a1982967008e4b
        Status: Downloaded newer image for 127.0.0.1:5000/hello-world:latest
        sudo docker images
        REPOSITORY  TAG  IMAGE ID CREATED VIRTUAL SIZE
        registry  2.1.1 b91f745cd233   5 days ago 220.1 MB
        ubuntu 14.04 a5a467fddcb8 6 days ago 187.9 MB
        127.0.0.1:5000/hello-world latest 0a6ba66e537a 2 weeks ago     960 B

docker容器技术参考 https://yeasy.gitbooks.io/docker_practice/content/image/dockerfile/user.html

# 控制registry的使用权限
使其只有在登录用户名和密码match之后才能使用,还需要做额外的设置.
registry的用户名密码文件可以通过htpasswd来生成:

        mkdir /opt/registry-var/auth/  
        docker run --entrypoint htpasswd registry:2.1.1 -Bbn admin  Password! >> /opt/registry-var/auth/htpasswd
上面这条命令是为admin用户名生成密码为Password1的一条用户信息,存在/opt/registry-var/auth/htpasswd文件里面,文件中的密码是被加密过的.
使用带用户权限的registry时候,容器的启动命令就跟上面不一样了,将之前的容器停掉并删除,然后执行下面的命令:

        docker run -d -p 5000:5000 --restart=always \  
          -v /opt/registry-var/auth/:/auth/ \  
          -e "REGISTRY_AUTH=htpasswd" \  
          -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \  
          -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \  
          -v /opt/registry-var/:/var/lib/registry/ \  
          registry:2.1.1 
这时,如果直接想查看仓库信息,pull或push都会出现权限报错.必须先使用docker login 命令来登录私有仓库:

        docker login 192.168.0.100:5000  
根据提示,输入用户名和密码即可.如果登录成功,会在<a>/root/.docker/config.json</a>文件中保存账户信息,这样就可以继续用了



# Possible Issue
* 可能会出现无法push镜像到私有仓库的问题.这是因为我们启动的registry服务不是安全可信赖的.需要修改docker的配置文件<a>/etc/default/docker</a>,添加下面的内容,
 
        DOCKER_OPTS="--insecure-registry xxx.xxx.xxx.xxx:5000"

    然后重启docker daemon进程,

        sudo service docker restart
    再push即可; Windows上 Settings>Daemon,在Insecure registries:中加入非安全的registry的url即可

* 容器内无法上网,增加容器运行参数:

        docker run -it --net host imageId bash

* 如果出现2375端口无法访问的异常

    Go to your notification tab, right click the Whale Icon and then click Settings.
    In the first page in the very end you will have “Expose daemon tcp://localhost:2375 without TLS” check this box

* 用nexus的docker private registry也可以搭建私有镜像仓库,坑也比较少!

* docker容器时区问题

	docker run -e TZ="Asia/Shanghai" -v /etc/localtime:/etc/localtime:ro --name=tomcat tomcat:8.0.35-jre8

* docker容器启动后若要执行多条命令,可在Dockfile里面加入:

        CMD ["sh", "/opt/init.sh"]
将要执行的命令统一放在/opt/init.sh这样的shell脚本中,要注意的是如果在windows下创建这样的sh文件,一定要将换行符换成 Unix 系统下的 \n

# pom.xml中版本号和docker plugin部分
    <project ...>

        <scm> <!-- 定义一个虚拟SCM避免构建时报[ERROR] Failed to execute goal org.codehaus.mojo:buildnumber-maven-plugin:1.0:create (default) on project projectname: Execution default of goal org.codehaus.mojo:buildnumber-maven-plugin:1.0:create failed: The scm url cannot be null.  -->

            <connection>scm:svn:http://127.0.0.1/dummy</connection>
            <developerConnection>scm:svn:https://127.0.0.1/dummy</developerConnection>
            <tag>HEAD</tag>
            <url>http://127.0.0.1/dummy</url>
        </scm>

        <build>
        <plugins>
        ....


     <!-- exec-maven-plugin start -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>0.4.13</version>
                <executions>
                    <execution>
                        <id>uc</id>
                        <phase>package</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                          <executable>
                            ${basedir}/uncompress.bat
                          </executable>
                          <arguments>
                            <argument>${project.build.directory}/${project.build.finalName}</argument>
                            <argument>${git.bin}</argument>
                            <argument>${current.profile}</argument>
                          </arguments>
                        </configuration>
                    </execution>

                </executions>


            </plugin>
            <!-- exec-maven-plugin end -->

             <!-- verNum plugin start, 用于生产版本号 -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>buildnumber-maven-plugin</artifactId>
                <version>1.2</version>
                <executions>
                    <execution>
                        <phase>validate</phase>
                        <goals>
                            <goal>create</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <doCheck>false</doCheck>
                    <doUpdate>false</doUpdate>
                    <buildNumberPropertiesFileLocation>${project.build.directory}</buildNumberPropertiesFileLocation>
                    <buildNumberPropertyName>verNum</buildNumberPropertyName>
                    <timestampFormat>{0,date,yyyyMMddHHmmss}</timestampFormat>
                    <items>
                        <item>timestamp</item>
                    </items>
                </configuration>
            </plugin>
            <!-- verNum plugin end -->


        </plugins>
    </build>

    <properties>
        <docker.registry>localhost:5000</docker.registry>
        <buildtimestamp>${timestamp}</buildtimestamp>
    </properties>
</project>

# snippet of maven's settings.xml
      <server>
        <id>mydocker</id>
        <username>admin</username>
        <password>Password1</password>
        <configuration>
          <email>abcd@qq.com</email>
        </configuration>
      </server>
    </servers>
