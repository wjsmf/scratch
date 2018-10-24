# pom.xml中加入如下部分解决无法打war包的问题
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-war-plugin</artifactId>
                    <version>3.0.0</version>
                </plugin>
            </plugins>
        </build>

# project directory construct 
webapp目录位于src/main下,和java及resources 同级,webapp下面放静态资源文件及所有的jsp等

        项目root/
            |
            |-src/
            |   |-main/  
            |       |-java/
            |       |-webapp/
            |       |      |-WEB-INF/
            |       |      |      |-web.xml
            |       |      |      |-predefinessdata-servlet.xml 
            |       |      |
            |       |      |-META-INF
            |       |-resources/
            |       
            |
            |-target/
            |-pom.xml

 

## open multi project in IDEA
  ![设置idea可以同时开多个项目](/com/syc/am/cntr/openMultiProjInIdeaSet.PNG "set idea to open multi projects at the same time")

## non maven project to maven project
 idea中将一个非maven项目 转成 maven项目
  选中pom.xm右键 > Add as Maven Project

## Internal java compiler error
 IntelliJ Idea编译报错：Error:java: Compilation failed: internal java compiler error 
 选中项目，右击选择Maven-->Reimport, 再次编译，问题解决。

## config spring mvc
        <servlet>
        <servlet-name>predefinessdata</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
        </servlet>

        <servlet-mapping>
            <servlet-name>predefinessdata</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>

上面的xml片段配置Spring mvc,必须在WEB-INF/下创建一个以上xml片段中`servlet-name`值`predefinessdata`开头的名为`predefinessdata-servlet.xml`的Spring bean xml配置文件,参考[project directory construct](#project-directory-construct)

