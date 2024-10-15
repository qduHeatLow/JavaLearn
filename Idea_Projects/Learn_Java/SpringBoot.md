# SpringBoot

SpringBoot 帮我们简单、快速地创建一个独立的、生产级别的 **Spring 应用（说明：SpringBoot底层是Spring）**

大多数 SpringBoot 应用只需要编写少量配置即可快速整合 Spring 平台以及第三方技术

**特性：**

- 快速创建独立 Spring 应用
  - 以前SSM：导包、写配置、启动运行

- 直接嵌入Tomcat、Jetty or Undertow（无需部署 war 包）【Servlet容器】

  - 原先：linux  java tomcat mysql： 将项目打包成war包 放到 tomcat 的 webapps下

  - 现在：将应用中嵌入Tomcat等Servlet容器，项目应用打包成jar包，在 java环境下直接 java -jar即可，无需再装Tomcat等

- **重点**：提供可选的starter，简化应用**整合**

  - **场景启动器**（starter）：

    - 项目可能会有很多功能（场景）：web、json、邮件、oss（对象存储）、异步、定时任务、缓存...

    - 原本：每种功能都要导包，导致一个项目导包一堆，还需控制好版本。

    - 现在：为每一种场景准备了一个依赖（maven坐标）；比如： **web-starter（包含所有web的依赖）。mybatis-starter**
    - 官方提供的场景：命名为：`spring-boot-starter-*`
    - 第三方提供场景：命名为：`*-spring-boot-starter`

- **重点：**按需自动配置 Spring 以及 第三方库
  - 如果这些场景我要使用（生效）。这个场景的所有配置都会自动配置好。
    - **约定大于配置**：每个场景都有很多默认配置。
    - 自定义：配置文件中修改几项就可以

- 提供生产级特性：如 监控指标、健康检查、外部化配置等

  - 监控指标、健康检查（k8s）、外部化配置（以前是将配置信息封装到源码中，最后会随着应用打包成jar包内部，因此如果向更改配置需要改源码，重新jar包部署，而现在是将配置信息放到外部配置文件（独立于项目jar包，如application.properties））

  - 无代码生成（全是自己写的，不会自动生成垃圾代码）、无xml

总结：简化开发，简化配置，简化整合，简化部署，简化监控，简化运维。

### 开发流程

1. 创建项目，并导入依赖（parent）

   ```xml
   <!--    所有springboot项目都必须继承自 spring-boot-starter-parent -->
       <parent>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-parent</artifactId>
           <version>3.0.5</version>
       </parent>
   ```

   

2. 导入场景（web场景）

   ```xml
       <dependencies>
   <!--        web开发的场景启动器 -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
       </dependencies>
   
   ```

3. 编写主程序 注意标注注解@SpringBootApplication

   ```java
   @SpringBootApplication //这是一个SpringBoot应用
   public class MainApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(MainApplication.class,args);
       }
   }
   ```

4. 开始编写业务代码（controller等）

   ```java
   @RestController
   public class HelloController {
   
       @GetMapping("/hello")
       public String hello(){
   
           return "Hello,Spring Boot 3!";
       }
   
   }
   ```

5. 测试省略

6. 打包

   打包成可以直接执行的jar包（java -jar demo.jar）

   导入依赖：

   ```xml
   <!--    SpringBoot应用打包插件-->
       <build>
           <plugins>
               <plugin>
                   <groupId>org.springframework.boot</groupId>
                   <artifactId>spring-boot-maven-plugin</artifactId>
               </plugin>
           </plugins>
       </build>
   ```

   使用maven clean、package即可打包

------

`application.properties`：

- 集中式管理配置。只需要修改这个文件就行 。
- 配置基本都有默认值
- 能写的所有配置都在： https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties

------

### Spring Initializer 创建向导

![image.png](https://cdn.nlark.com/yuque/0/2023/png/1613913/1679922435118-bde3347e-b9fe-4138-8e16-0c231884ea5f.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_39%2Ctext_5bCa56GF6LC3IGF0Z3VpZ3UuY29t%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10%2Fformat%2Cwebp)

创建好后，就可以编写业务代码了

### 依赖管理机制

1. 为什么导入`starter-web`所有相关依赖都导入进来？

   ![image-20241007125312187](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007125312187.png)

   导入的这个场景点进去可以看到，它还是由很多该场景用到的依赖和场景构成的。即依赖传递原则，A-B-C，那么A既有B和C

2. 为什么版本号都不用写？

   每个boot项目都必须由父项目：spring-boot-starter-parent，而该父项目也有一个父项目spring-boot-dependencies。

   ![image-20241007131555229](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007131555229.png)

   其中的properties标签存储着所有常见的jar包版本都声明好了（版本仲裁中心），因此我们就不需要自己写版本了。

   ![image-20241007132430277](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007132430277.png)

   如果想自定义版本，那么就在自身pom.xml中声明一个properties标签声明（需要与父项目那里key同名），然后指定版本。

   ![image-20241007132501496](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007132501496.png)

   或者直接声明dependencies的dependency自己写：

   ![image-20241007132619189](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007132619189.png)

   总结：

   ![image.png](https://cdn.nlark.com/yuque/0/2023/png/1613913/1679294529375-4ee1cd26-8ebc-4abf-bff9-f8775e10c927.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_25%2Ctext_5bCa56GF6LC3IGF0Z3VpZ3UuY29t%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10%2Fformat%2Cwebp)

### 自动配置机制

不再需要去手动配置spring、springmvc等

![image-20241007133353763](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007133353763.png)

各个组件：SpringApplication.run之后会返回ioc容器，该容器中有了所有组件并自动配置好了（DispatcherServlet、viewResolver、characterEncodingFilter）。



包扫描：默认包扫描规则Springboot会扫描主程序（@SpringBootApplication注解标注）所在的包及其以下的包



配置文件默认配置：某些配置已经被封装成了类中，即**配置文件**的所有配置项是和某个**类的对象**值进行一一绑定的。这些属性值都会有默认值。这种绑定了配置文件中每一项的类叫做配置**属性类**。

比如：

- `ServerProperties`绑定了所有Tomcat服务器有关的配置，图中server.port=9999实际上调用了ServerProperties的setPort()
- `MultipartProperties`绑定了所有文件上传相关的配置
- ....参照[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.server)：或者参照 绑定的  **属性类**

![image-20241007141209684](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007141209684.png)

#### spring-boot-starter

我们导入的一些场景，比如`spring-boot-starter-web`等，在其内部除了导入相关功能的依赖以外，还导入了一个`spring-boot-starter`，它是所有starter的starter（核心starter）。

`spring-boot-starter`中导入了`spring-boot-autoconfigure`包，全都是用来做各种场景的自动配置类`AutoConfiguration`：

![image-20241007142029608](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007142029608.png)

虽然由非常多的自动配置类，但是是按需配置的，根据具体导入了哪个场景来自动配置有关功能。

总结： 导入场景启动器、触发 `spring-boot-autoconfigure`这个包的自动配置生效、容器中就会具有相关场景的功能



自动配置再梳理：

**1、**导入`starter-web`：导入了web开发场景

- 1、场景启动器导入了相关场景的所有场景和依赖：`starter-json`、`starter-tomcat`、`springmvc`
- 2、每个场景启动器都引入了一个`spring-boot-starter`，核心场景启动器。
- 3、核心场景启动器引入了`spring-boot-autoconfigure`包。
- 4、`spring-boot-autoconfigure`里面囊括了所有场景的所有配置（已经写好了）。
- 5、只要这个包下的所有类都能生效，那么相当于SpringBoot官方写好的整合功能就生效了。但是但是，SpringBoot默认只扫描主程序所在的包，默认却**扫描不到** `spring-boot-autoconfigure`下写好的所有配置类（这些**配置类**给我们做了整合操作，不用我们在手动整合）。也就是说虽然引入了，但是还没有生效。怎么办呢，看下面⬇

**2、主程序**：`@SpringBootApplication`

- 1、`@SpringBootApplication注解`由三个注解组成`@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan`

- 2、`@EnableAutoConfiguration`：SpringBoot **开启自动配置的核心注解**。

  - 该注解内部使用`@Import(AutoConfigurationImportSelector.class)`提供功能：**批量**给容器中导入组件。

  - SpringBoot启动会默认加载 142个（不管何种场景）配置类。

    - 这**142个配置类**来自于`spring-boot-autoconfigure`下 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件指定的：

      ![image-20241007163146750](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007163146750.png)

  - 总结：@EnableAutoConfiguration注解作用是：项目启动的时候利用 @Import 批量导入组件机制，把 `spring-boot-autoconfigure` 包下的142个 `xxxxAutoConfiguration`类导入进来（**自动配置类**）

- 3、按需生效：

  - 并不是这`142`个自动配置类都能生效

  - 每一个自动配置类，都有**条件注解**`@ConditionalOnxxx`，只有条件成立（一般是某个类存在即OnClass，因此需要导入包，因此是场景中导入了的包才会条件成立），才能生效 

**3、**`xxxxAutoConfiguration`**自动配置类（共142个，但是不都生效⬆）**

- 内部其实是给容器中使用@Bean 放一堆组件，**使组件生效**。

- 每个**自动配置类**都可能有这个注解`@EnableConfigurationProperties(ServerProperties.class)`，用来把配置文件中配的指定前缀的属性值封装到 `xxxProperties`**属性类**中（使用属性绑定注解）

  - 以Tomcat为例：把服务器的所有配置都是以`server`开头的。配置都封装到了属性类中：

    ![image-20241007164259299](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007164259299.png)

    ![image-20241007164334900](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007164334900.png)

- 所以**容器**中放的某些组件**的一些**核心参数**，都来自于`xxxProperties`**。**`xxxProperties`**都是和配置文件绑定（都使用了属性绑定注解）。所以只需要改配置文件的值，核心组件的底层参数都能修改

**4、**写业务，全程无需关心各种整合（底层这些整合写好了，而且也生效了）



**核心流程总结：**

1、导入`starter`，就会导入`autoconfigure`包和场景需要的包。

2、`autoconfigure` 包里面 有一个文件 `META-INF/spring/**org.springframework.boot.autoconfigure.AutoConfiguration**.imports`,里面指定的所有启动要加载的自动配置类

3、@EnableAutoConfiguration 会自动的把上面文件里面写的所有**自动配置类都导入进来。xxxAutoConfiguration 是有条件注解进行按需加载**（第一步中导入的需要的包）

4、`xxxAutoConfiguration`又会给容器中导入一堆组件（比如DispatcherServlet等），组件都是从 `xxxProperties`中提取属性值

5、`xxxProperties`又是和**配置文件**进行了绑定

![image.png](https://cdn.nlark.com/yuque/0/2023/png/1613913/1679970508234-3c6b8ecc-6372-4eb5-8c67-563054d1a72d.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_37%2Ctext_5bCa56GF6LC3IGF0Z3VpZ3UuY29t%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10%2Fformat%2Cwebp)

## day1007

### 常用注解

#### 组件注册注解

SpringBoot摒弃XML，而是全注解驱动

**@Controller、 @Service、@Repository、@Component**



**@Configuration（同@SpringBootConfiguration）、@Bean、@Scope**

为了与以前xml文件对应，可以使用配置类和@Bean（作用其实同我们在类上使用@Component）：

我们新建的User、Cat组件还可以用一个Config类（用@Configuration标记）来装配：

![image-20241007150110987](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007150110987.png)

注意，组件的名字（即容器中的对象名）是以@Bean注解的属性>方法名的顺序指定的

@Scope用来标注是不是单实例



**@Import**

但是第三方的类，我们该怎么进行装配呢？因为代码是第三方的，我们没法使用类上加@Component方法，因此我们可以使用上面的@Configuration加@Bean的方法，new一个再返回。

也可以：

使用在配置类中@Import(类.class)进行导入 



#### 条件注解

如果注解指定的**条件成立**，则触发指定行为（注解标注在方法上就是方法生效，标注在类上就是类生效类中方法生效）

***@ConditionalOnXxx***：

**@ConditionalOnClass：如果*类路径*中存在这个类（比如导了jar包），则触发指定行为**

**@ConditionalOnMissingClass：如果类路径中不存在这个类，则触发指定行为**

**@ConditionalOnBean：如果*容器*中存在这个Bean（组件），则触发指定行为**

**@ConditionalOnMissingBean：如果容器中不存在这个Bean（组件），则触发指定行为**



示例：

如果类路径中存在Cat.class，则将User user组件加入容器：

```java
package org.example.springboot_learn.config;

import org.example.springboot_learn.bean.User;

@SpringBootConfiguration
public class AppConfig {

    @ConditionalOnClass(name = "org.example.springboot_learn.bean.Cat")
    @Bean
    public User user(){
        return new User();
    }
}
```

测试：

```java
@SpringBootApplication
public class SpringBootLearnApplication {

    public static void main(String[] args) {
        var ioc = SpringApplication.run(SpringBootLearnApplication.class, args);
        String[] beanNamesForType = ioc.getBeanNamesForType(User.class);
        for (String s : beanNamesForType) {
            System.out.println(s);
        }
    }

}
```

![image-20241007153953096](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007153953096.png)

#### 属性绑定注解

**@ConfigurationProperties： 声明组件的属性和配置文件哪些前缀开始项进行绑定**

将容器中任意**组件（Bean）的属性值**和**配置文件**的配置项的值**进行绑定：**

- **1、给容器中注册组件（@Component、@Bean）**
- **2、使用****@ConfigurationProperties 声明组件和配置文件的哪些配置项进行绑定**

示例：

声明一个组件Pig，然后将属性值放入application.properties中配置：

![image-20241007155013711](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007155013711.png)

![image-20241007155104037](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007155104037.png)

在Pig类上加上@ConfigurationProperties注解，注解属性是application.properties中的前缀

![image-20241007155221721](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007155221721.png)



**@EnableConfigurationProperties：快速注册注解：**

- **场景1：**上面的Pig类不使用@Component放入容器中（也不用其它方法），而是**只用@ConfigurationProperties注解标注要绑定属性（仍然需要这个注解，只是不用放在容器中）**。可以在config类上加入@EnableConfigurationProperties(类)：

  ![image-20241007160035003](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241007160035003.png)

  就可以实现Sheep的属性绑定并将组件放入容器中。但是这种情况还不如直接使用@Component放入容器中

- **场景2（主要用途）：**SpringBoot默认只扫描自己主程序所在的包。**如果导入第三方包，即使组件上标注了 @Component、@ConfigurationProperties 注解，也没用**。因为组件都扫描不进来，此时使用**这个注解就可以快速进行属性绑定并把组件注册进容器**

### yml文件

SpringBoot 集中化管理配置，所有的配置放在`application.properties`，虽然所有配置都有默认，但是层级关系不明显

因此可以使用yml类型文件配置：`application.yml`

对比：

```properties
person.name=张三
person.age=18
person.birthDay=2010/10/12 12:12:12
person.like=true
person.child.name=李四
person.child.age=12
person.child.birthDay=2018/10/12
person.child.text[0]=abc
person.child.text[1]=def
person.dogs[0].name=小黑
person.dogs[0].age=3
person.dogs[1].name=小白
person.dogs[1].age=2
person.cats.c1.name=小蓝
person.cats.c1.age=3
person.cats.c2.name=小灰
person.cats.c2.age=2
```

```yaml
person:
  name: 张三
  age: 18
  birthDay: 2010/10/10 12:12:12
  like: true
  child:
    name: 李四
    age: 20
    birthDay: 2018/10/10
    text: ["abc","def"]
  dogs:
    - name: 小黑
      age: 3
    - name: 小白
      age: 2
  cats:
    c1:
      name: 小蓝
      age: 3
    c2: {name: 小绿,age: 2} #对象也可用{}表示
```



注意：

- **大小写敏感**
- 使用**缩进表示层级关系，k: v，使用空格分割k,v**
- 缩进时不允许使用Tab键，只允许**使用空格**。换行
- 缩进的空格数目不重要，只要**相同层级**的元素**左侧对齐**即可
- **# 表示注释**，从这个字符一直到行尾，都会被解析器忽略。

### 日志

面向日志接口编程，使用时使用实现（和其它一样）

 ![image.png](https://cdn.nlark.com/yuque/0/2023/png/1613913/1680232037132-d2fa8085-3847-46f2-ac62-14a6188492aa.png?x-oss-process=image%2Fwatermark%2Ctype_d3F5LW1pY3JvaGVp%2Csize_29%2Ctext_5bCa56GF6LC3IGF0Z3VpZ3UuY29t%2Ccolor_FFFFFF%2Cshadow_50%2Ct_80%2Cg_se%2Cx_10%2Cy_10%2Fformat%2Cwebp)

Spring使用commons-logging作为内部日志，但底层日志实现是开放的。可对接其他日志框架，spring5及以后：commons-logging被spring直接自己写了

默认使用Logback，其配置已经默认配置好了

spring-boot-starter（即核心场景，每个场景都会导入的）里就有spring-boot-starter-logging

默认使用了`logback + slf4j` 组合作为默认底层日志

日志是利用**监听器机制**配置好的。`ApplicationListener`。

日志所有的配置都可以通过修改配置文件实现：在配置文件中以`logging`开始的所有配置。

#### 自己写日志

在日志工厂中直接拿到logger对象

```java
Logger logger = LoggerFactory.getLogger(getClass());
logger.info("日志内容");

或者在类上使用Lombok的@Slf4j注解，之后直接用log对象即可
```



日志输出到文件：

在application.properties中写：logging.filename.name = "*.log"



日志文档归档与切割：

- 归档：每天的日志单独存到一个文档中。

- 切割：每个文件10MB，超过大小切割成另外一个文件。

  | 配置项                                               | 描述                                                         |
  | ---------------------------------------------------- | ------------------------------------------------------------ |
  | logging.logback.rollingpolicy.file-name-pattern      | 日志存档的文件名格式（默认值：${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz） |
  | logging.logback.rollingpolicy.clean-history-on-start | 应用启动时是否清除以前存档（默认值：false）                  |
  | logging.logback.rollingpolicy.max-file-size          | 存档前，每个日志文件的最大大小（默认值：10MB）               |
  | logging.logback.rollingpolicy.total-size-cap         | 日志文件被删除之前，可以容纳的最大大小（默认值：0B）。设置1GB则磁盘存储超过 1GB 日志后就会删除旧日志文件 |
  | logging.logback.rollingpolicy.max-history            | 日志文件保存的最大天数(默认值：7).                           |

## day1008

### SpringBoot-WEB

#### 自动配置

引入web场景：spring-boot-starter-web，会在内部再引入spring-boot-starter。其中引入autoconfigure功能。之后再springboot启动类上标注的@SpringBootApplication内部会@EnableAutoConfiguration后，会使用@Import注解批量导入组件（META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports中的所有142个组件自动配置类），其中与web有关的：

```java
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration
====以下是响应式web场景和现在的没关系======
org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.ReactiveMultipartAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration
org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
================以上没关系=================
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
```

这些自动配置类又使用@EnableConfigurationProperties与一些属性类进行绑定，这些属性类又使用@ConfigurationProperties与配置文件中的某些前缀绑定（前缀如下）：

- SpringMVC的所有配置 `spring.mvc`
- Web场景通用配置 `spring.web`
- 文件上传配置 `spring.servlet.multipart`
- 服务器的配置 `server`: 比如：编码方式



#### 自动配置后的默认效果

1. 包含了 ContentNegotiatingViewResolver 和 BeanNameViewResolver 组件，**方便视图解析**

   但是一般SpringBoot给前端都是JSON，而不是再专门视图跳转，因此小学一下

2. **默认的静态资源处理机制**： 静态资源放在 static 文件夹下即可直接访问

3. **自动注册**了 **Converter**,GenericConverter,**Formatter**组件，适配常见**数据类型转换**和**格式化需求**

4. **支持** **HttpMessageConverters**，可以**方便返回**json等**数据类型** 

   @RestController @ResponseBody

   如果控制器方法返回对象，则转为JSON；如果控制器方法返回字符串，则直接写出去

5. **注册** MessageCodesResolver，方便**国际化**及错误消息处理

6. **支持 静态** index.html

7. **自动使用**ConfigurableWebBindingInitializer，实现消息处理、数据绑定、类型转化、数据校验等功能



SpringBoot 已经默认配置好了**Web开发**场景常用功能。我们直接使用即可



### 三种方式

| 方式         | 用法                                                         |                              | 效果                                                      |
| ------------ | ------------------------------------------------------------ | ---------------------------- | --------------------------------------------------------- |
| **全自动**   | 直接编写控制器逻辑                                           |                              | 全部使用**自动配置默认效果**                              |
| **手自一体** | `@Configuration` +   配置`WebMvcConfigurer+ *配置 WebMvcRegistrations* | **不要标注** `@EnableWebMvc` | **保留自动配置效果** **手动设置部分功能** 定义MVC底层组件 |
| **全手动**   | `@Configuration` +   配置`WebMvcConfigurer`                  | **标注** `@EnableWebMvc`     | **禁用自动配置效果** **全手动设置**                       |

总结：

**给容器中写一个配置类**，用`@Configuration`标注，并继承并**实现** `WebMvcConfigurer`。**但是不要标注** `@EnableWebMvc`**注解，实现手自一体的效果。**

### WebMvcAutoConfiguration原理

#### 生效条件

```java
@AutoConfiguration(after = { DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class }) //在这些自动配置之后
@ConditionalOnWebApplication(type = Type.SERVLET) //如果是web应用就生效，类型SERVLET 另一种类型是REACTIVE 响应式web
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class) //容器中没有这个Bean，才生效。默认就是没有
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)//优先级
@ImportRuntimeHints(WebResourcesRuntimeHints.class)
public class WebMvcAutoConfiguration { 
}
```



