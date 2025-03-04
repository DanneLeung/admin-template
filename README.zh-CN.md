# 管理后台模板

## 项目概述

这是一个基于Vue 3（前端）和Spring Boot（后端）构建的现代化管理后台模板，包含用户认证、基于角色的访问控制等功能。

## 系统架构

### 前端

- **框架**: Vue 3 + Vite
- **UI组件**: 现代化组件架构
- **状态管理**: Vuex状态管理
- **路由**: Vue Router导航

### 后端

- **框架**: Spring Boot 3.1.0
- **安全框架**: Spring Security + JWT认证
- **数据库**: MySQL + JPA/Hibernate
- **缓存**: Redis实现
- **API文档**: SpringDoc（Swagger UI）

## 核心功能

### 用户管理

- 用户认证与授权
- 基于角色的访问控制
- 菜单权限管理
- 密码策略实施
- 登录尝试监控

### 系统配置

#### 后端配置

##### 开发环境

- 数据库：MySQL 8.0
- Redis缓存
- Hibernate自动DDL生成
- 详细SQL日志

##### 生产环境

- 优化数据库连接池
- 禁用自动DDL生成
- 生产级日志配置
- 禁用Swagger UI

#### 安全特性

- JWT认证机制
- 前端跨域配置
- 密码强度策略
- 登录尝试限制

## 技术栈

### 后端依赖

- Spring Boot Starter（Web/Security/Data JPA/Validation/AOP/Cache/Redis）
- MySQL连接驱动
- JWT实现（jjwt）
- Lombok代码简化
- MapStruct对象映射

### 前端配置

- Vue 3 + Vite构建工具
- 现代化JavaScript特性
- 响应式设计实现

## 配置说明

### 应用配置

- 服务端口：8080
- API上下文路径：/api
- 可配置CORS设置
- 自定义密码策略
- 可调登录尝试限制

### 开发环境搭建

1. 配置MySQL数据库
2. 部署Redis实例
3. 配置application-dev.yml
4. 启动后端服务
5. 运行前端开发服务器

## API文档

开发环境可通过/api/swagger-ui.html访问Swagger UI

## 安全规范

- 密码安全存储
- JWT令牌管理
- 限流机制实现
- 环境差异化安全配置

## 性能优化

- HikariCP连接池
- Redis缓存实现
- 生产环境优化配置
- 高效资源管理
