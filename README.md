# Specialist Hub - CPT202 Group 25 Project

欢迎来到 **Specialist Hub** 项目仓库！本项目是一个基于 Java Spring Boot 后端与 Tailwind CSS 前端的专家预约系统。

---

## 🛠️ 环境准备

在运行项目之前，请确保您的电脑已安装以下基础环境：

- **JDK 17**: [下载地址](https://www.google.com/search?q=https://www.oracle.com/java/technologies/downloads/%23java17)
    
- **MySQL 8.0+**: [下载地址](https://dev.mysql.com/downloads/installer/)
    
- **VS Code**: [下载地址](https://code.visualstudio.com/)
    

---

## 📥 第一步：Git 安装与配置

如果您还没有安装 Git，请按照以下步骤操作：

### 1. 安装 Git

- **Windows**: 访问 [Git 官网](https://git-scm.com/download/win)，点击 **"Click here to download"**。下载后运行安装程序，一路点击 **Next** 即可。
    
- **Mac**: 在终端输入 `brew install git` 或访问 [官网下载](https://git-scm.com/download/mac)。
    

### 2. 重启 VS Code

安装完成后，请**彻底关闭并重新打开 VS Code**，以确保系统识别 `git` 命令。

### 3. 配置身份信息

打开 VS Code 终端，依次输入以下命令（替换引号内的内容为您的信息）：

Bash

```
git config --global user.email "您的GitHub邮箱"
git config --global user.name "您的真实姓名"
```

---

## 🚀 第二步：项目初始化与运行

### 1. 拉取代码

在本地文件夹打开终端，运行以下命令获取项目：

Bash

```
git clone https://github.com/yi3328/CPT202-Group25.git .
```

### 2. 导入数据库数据

1. 打开 **MySQL Workbench**。
    
2. 点击顶部菜单栏 **Server -> Data Import**。
    
3. 选择 **Import from Self-Contained File**。
    
4. 点击右侧的 `...` 按钮，选择项目路径下的 `backend/specialist_hub_backup.sql`。
    
5. 点击右下角的 **Start Import**。
    

### 3. 配置并启动后端 (Backend)

1. 在 VS Code 中打开 `backend` 文件夹。
    
2. 检查 `src/main/resources/application.properties`，确保数据库用户名和密码与您本地一致。
    
3. 在终端输入以下命令启动服务：
    
    - **Windows**: `.\mvnw clean spring-boot:run`
        
    - **Mac/Linux**: `./mvnw clean spring-boot:run`
        
    - _后端服务将运行在 `http://localhost:8081`_。
        

### 4. 运行前端 (Frontend)

直接在浏览器中双击打开 `frontend/login.html` 即可开始测试系统。

---

## 🔑 测试账号信息

系统已预设 20 个客户账号及 5 位专家，供测试预约和支付流程使用：

|**用户类型**|**账号 (Username)**|**密码 (Password)**|
|---|---|---|
|**客户 (Customer)**|`CUST001` 至 `CUST020`|`123456`|
|**专家 (Specialist)**|`SPEC001` 至 `SPEC005`|`123456`|

---

## 📂 项目结构说明

- `/backend`: Spring Boot 后端代码，包含 Controller、Repository 及数据模型。
    
- `/frontend`: 纯 HTML/JS 前端页面，采用 Tailwind CSS 样式。
    
- `specialist_hub_backup.sql`: 数据库初始化脚本，包含表结构及测试数据。
    

---

## ⚠️ 团队协作提示

- **代码同步**：在开始编写代码前，请务必先执行 `git pull origin main` 以同步最新进度。
    
- **提交更改**：
    
    Bash
    
    ```
    git add .
    git commit -m "描述您的修改内容"
    git push origin main
    ```
    
- **退出登录**：前端已集成 **Logout** 功能，点击后会清除本地缓存并返回登录页面。
