# 基础组件
    项目名称：
    项目类型：jar (基础jar包)
    创建时间：2021年06月01日 17:17:00

## 一、简介

#### 1.1 背景

    小班课存在N个微服务(项目)，各个微服务都有一些公共的组件，各种mapper、dao、entity的重复实体；存在多种http的调用；
    存在各种redis的工具类、配置；后续可能也会有各种通用化的配置

#### 1.2 目的
    提供基础的通用能力，赋能各个微服务；降本增效、提高研发效率；

## 二、说明

### 定位

<h5 style="color: firebrick">以提供基础能力为中心，吸收并辐射一些公共能力的基础服务，开箱即用，沉淀后可以外部输出！</h3>

### 项目结构

```
├── base
│   ├──xbk-base-parent
│   ├──xbk-common-core
│   │   ├──xbk-common-core-global
│   │   └──xbk-common-core-utils
│   ├──xbk-common-demo（示例项目）
│   └──xbk-common-starter
│       ├── xbk-common-starter-cache
│       ├── xbk-common-starter-datasource
│       ├── xbk-common-starter-fegin
│       ├── xbk-common-starter-es
│       ├── xbk-common-starter-httpclient
│       ├── xbk-common-starter-mybatis
│       ├── xbk-common-starter-redis
│       └── xbk-common-starter-web
```

### 快捷地址
<details>
<summary>点开有惊喜</summary>

* [`xbk-base-parent`](#xbk-base-parent)
* [`xbk-common-core-global`](#xbk-common-core-global)
* [`xbk-common-core-utils`](#xbk-common-core-utils)
* [`xbk-common-starter-cache`](#xbk-common-starter-cache)
* [`xbk-common-starter-datasource`](#xbk-common-starter-datasource)
* [`xbk-common-starter-feign`](#xbk-common-starter-feign)
* [`xbk-common-starter-es`](#xbk-common-starter-es)
* [`xbk-common-starter-httpclient`](#xbk-common-starter-httpclient)
* [`xbk-common-starter-mybatis`](#xbk-common-starter-mybatis)
* [`xbk-common-starter-redis`](#xbk-common-starter-redis)
* [`xbk-common-starter-web`](#xbk-common-starter-web)
</details>

---
## xbk-base-parent
<details>
<summary>jar版本管理</summary>

### 描述
1. 定义了所有`jar`包版本信息、`gradle`常用的组件信息、`gradle`配置信息。
2. 所有的工具包的`parent`。
3. 统一的`gradle`版本管理，有效的避免不同`gradle`组件包版本冲突、不同`gradle`组件包版本不一致导致不必要包引用。

### 规约
1. 所有项目`build.gradle`都应继承`xbk-base-parent`。
2. 应当避免在业务项目模块中二次声明`gradle`组件版本信息。

### 使用示例
1. 引用`praent`
```xml
<parent>
    <groupId>com.ziroom.crm.xbk</groupId>
    <artifactId>xbk-base-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
```
2. 引入`jar`
```xml
<dependencies>
    <dependency>
        <groupId>com.ziroom.crm.xbk</groupId>
        <artifactId>xbk-common-starter-web</artifactId>
    </dependency>
</dependencies>
```
</details>

---
## xbk-common-core-global
<details>
<summary>全局静态属性</summary>

### 描述
1. 定义了系统公共`Nacos`配置的`NameSpace`信息。
2. 定义了系统异常`BizException`。
3. 定义了系统交互所有已约定的`Head`信息。
4. 定义了`web`返回数据封装结果类型。

### 规约
1. 所有组件开发中使用的基础信息都应以此为准，不应该额外定义。

</details>

---
## xbk-common-core-utils
<details>
<summary>静态工具方法</summary>

### 描述
1. 提供常用的开发静态类

### 规约
1. 改模块中提供的方法必须都为静态方法。

### 使用示例
各种工具类吧，想用什么就往里面加吧，不过最主要的是不要重复引入一些想用技术的工具类，没必要；

</details>


---
## xbk-common-starter-cache
<details>
<summary>spring-boot-starter-redis改写</summary>

### 描述
1. 项目中的redis工具类很多配置都不太合理，没有统一配置的地方，这里提供一个项目基础的redis配置，
   服务如果有特殊需求也可不引人当前模块
2. 提供缓存组件封装工具，统一使用该组件操作缓存数据库。
3. 该模块集成了`xbk-common-starter-redis` 无需额外引入。
### 使用
1. 引用`Nacos`公共配置`XBK-Redis`
2. 该组件基于`Redis`数据类型提供支持，提供的数据类型如下：
   ```java
   key-value     : CacheValueTemplate
   hash          : CacheHashTemplate
   List          : CacheListTemplate
   Set           : CacheSetTemplate
   ZSet          : CacheZSetTemplate
   RedisTemplate : CacheTemplate
   ```

</details>


---
## xbk-common-starter-datasource
<details>
<summary>基于spring-boot配置的多数据源自动注入、mybatis组件自动装配</summary>

### 现状
1. 目前小班课是单点数据源，没有多数据源的情况；多数据源的情况适用于之后的分库分表、读写分离的情况，所以目前先预留空间；
2. 下面的方案是之前应用开源的工具和之前团队应用的整体总结；
### 描述
1. `dataSource` 提供了基于`spring-boot`配置的多自动数据源注入(这个功能在数据迁移的时候可能需要用到)、`mybatis`组件自动装配、`Druid`监控自动装配。
2. 依赖`com.alibaba.druid`：阿里的数据库连接工具。
3. 依赖`tk.mybatis.mapper-spring-boot-starter`：`mybatis`增强工具，如果引入该模块之后不使用`mybatis`自动装配可排除。
4. 自动集成`pagehelper`: `mybatis`分页组件。
### 使用
1. 强烈建议排除`DataSource`自动注入。
    ```java
    @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
    ```
2. 单数据源使用：引用`Nacos`公共配置`XBK-MySql`，覆盖原有配置中`url`、`username`、`password`。
3. 多数据源使用：引用`Nacos`公共配置`XBK-Mysql`，覆盖原有配置中`url`、`username`、`password`即可注入`master`数据源，其他数据源配置为`datasource.dynamicDataSource.xx`，其中`xx`为数据源名称。
   例如，配置`read`数据源。
    ```yml
    spring:
      datasource:
        dynamicDataSource:
          read:
            url: xx
            username: xxx
            password: xx
    ```
   在`Nacos`中默认提供了`read`/`write`两个扩展数据源配置。代码调用：
    ```java
    @DataSourceType("read")
    public List<Demo> selectFromReadDataSource(String name) {
        return demoMapper.customizeSqlSelectByName(name);
    }
    ```
   此处建议把数据源名称声明成静态常量，方便全局统一管理。
### 扩展

</details>

---
## xbk-common-starter-es
<details>
<summary>公共ES自动注入</summary>

### 描述
1. 目前因为还没有到使用ES的阶段，所以这里只是预留空间
2. 提供了`Es`自动注入。
3. 目前支持`XbkEs`、`DataCenterEs`.

### 使用
1. 关联`Nacos`配置`XBK-ES`。
2. 引入必要的`ES`配置。`@EnableDataCenterEs`: 开启`DataCenteres`;`@EnableXbkEs`开启`XbkEs`。
    ```java
    @Configuration
    @EnableDataCenterEs
    @EnableXbkEs
    public class EsConfig {
    }
    ```
   由于`ES`连接在启动时候会检测连接情况，因此建议按需引入，及时排除不必要的引入。

### 扩展

---
## xbk-common-starter-httpclient
<details>
<summary>Http网络请求工具</summary>

### 描述
1. 提供了`http`请求工具。
2. 提供了`httpClient`、`okhttp`两种实现方式。

### 使用
1. 关联`Nacos`配置`XBK-HttpClient`。
2. 使用`RestTemplate`发送请求

### 扩展
1. 切换当前`Http`请求实现。
```xml
spring.http.rest-client.type = okHttp3 //默认
spring.http.rest-client.type = httpClient //使用 httpClient
```
</details>

---
## rock-common-starter-web
<details>
<summary>spring-web 项目基础配置</summary>

### 描述
1. 提供全局统一返回结果集封装。
2. 提供全局异常处理机制。
3. 提供全局`json`序列化配置。
4. 提供`swagger`自动注入。
5. 提供日志打印配置。
6. 自动注入了两个任务线程池。
7. 提供全局参数校验配置。
+ **该包在各个项目组中应该在项目初始化时就定义所有信息，不建议该包进行扩展配置。**

### 使用
1. 关联`Apollo`基础配置`CRM-BASE`
2. `controller`正常返回的`objct`会被额外封装
    ```json
    {
        "code" : 200,
        "message" : "操作成功",
        "data" : {},
        "error_code" : 200,
        "error_message" : ""
    }
    ```
3. `controller`调用`void`方法
    ```java
    return SuccessResponse.defaultSuccessResponse();
    ```
4. 业务异常处理
   ```java
   throw new BizException(100101, "业务异常");
   ```
### 扩展
1. 自定义异常处理。异常处理提供了两个扩展`bean`：`ExceptionResolver`异常信息接受器，该`bean`可以注册多个，捕获异常后会根据`order`排序依次调用所有`bean`的处理方法。`ExceptionResultHandler` 异常结果处理器，该`bean`只可以注册一个，在所有`ExceptionResolver`处理完成之后调用该`bean`方法。系统现在默认提供：
	+ `DefaultExceptionResultHandler`：默认异常结果处理，**该`bean`不建议覆盖**。
	+ `DefaultBizExceptionResolver`：业务异常接收器。
	+ `DefaultDingDingExceptionResolver`: 需要钉钉报警的异常处理器。
	+ `DefaultRuntimeExceptionResolver`：系统异常接收器。
	  自定义异常接收器：
	  ```java
      @Configuration
      @Slf4j
      public class ExceptionLog implements ExceptionResolver {
          @Override
          public void resolve(HttpServletRequest request, Exception exception) {
            String prefix = exception instanceof BizException ? "【业务异常】" : "【系统异常】";
            log.error(prefix + ExceptionUtils.getStackTrace(exception));
          }
      
          @Override
          public boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
              return exception instanceof BizException || exception instanceof RuntimeException;
          }
      }
      ```
2. 扩展异常处理器，如果系统提供的异常处理规则不满足 当前项目时候可以扩展处理，也可以通过扩展`ExceptionAdviceAutoConfiguration`来达到相同的效果。
     ```java
     @Configuration
     @Order(Ordered.HIGHEST_PRECEDENCE + 101)
     public class ExceptionAdviceConfiguration extends ExceptionAdviceAutoConfiguration {
   
         @ExceptionHandler({HttpRequestMethodNotSupportedException.class, NoHandlerFoundException.class})
         @Override
         public Result notFoundHandler(HttpServletRequest request, Exception exception) {
             return ResultSuccess.defaultResultSuccess();
         }
     }
    ``` 
3. 扩展参数解析器，如果当前项目中集成了其他的`web`项目，例如`swagger`，统一的增强结果封装也会封装这类的请求接口从而导致`web`组件失效，此时可以通过扩展参数解析来解决。
      ```java
      @RestControllerAdvice
      public class ResponseAdviceConfiguration extends AbstractResponseAdviceTemplate {
    
          //定义需要 返回结果需要被封装的 包路径 建议直接返回`controller`路径
          private static final List<String> SUPPORT_PATH = Lists.newArrayList("com.xx.controller");
    
          @Override
          public List<String> supportPath() {
              return SUPPORT_PATH;
          }
      }
    
      
      如果如上依旧不能满足需求，可以考虑重写 supports 方法
      ```
</details>




------

## 三、技术架构



## 四、技术栈




## 五、最后

鸣谢：付志强、王涛、张明等等

