#环境配置：

前后端使用*VS CODE。创建“cpt202”文件夹来管理

在cpt202文件夹内创建“frontend”文件夹，其中放入html文件，网页

之后，前往链接: https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.2.5&packaging=jar&jvmVersion=17&groupId=com.example&artifactId=backend&name=backend&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.backend&dependencies=we

直接点击GENRERATE CTRL+，会提示下载backend.zip文件

把下载好的zid包解压到cpt202文件夹自动出现backend文件夹(若之前没下载过jdk请自己下载符合版本的jdk)。

打开vc code,在 VS Code 顶部的菜单栏，点击“终端 (Terminal)” -> “新建终端 (New Terminal)”。屏幕下方会出现一个黑框,输入 cd backend切换到对应路径.

在终端如果是 Windows，输入 mvnw spring-boot:run；如果是 Mac，输入 ./mvnw spring-boot:run下载依赖

下载完成之后,在backend/src 文件夹下,main/java负责写代码，main/resources用于数据库，test用于单元测试。
