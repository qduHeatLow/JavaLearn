# Spring

## day0917

### Maven

![image.png](https://cdn.nlark.com/yuque/0/2024/png/42995594/1710225813568-68f6bedb-6772-43b7-a474-7cff8fcd2c15.png?x-oss-process=image%2Fformat%2Cwebp)

1. Maven 其中一个核心特性就是依赖管理。我们开发的项目基本会使用外部依赖，或者我们需要处理多模块项目的模块之间的依赖，这些依赖关系非常复杂，管理起来比较困难。使用 Maven 管理依赖能大大降低难度。

2. Maven 对于依赖管理一个特点是可传递性依赖发现，比如我们项目依赖于 A，A 又依赖于 B，如果我们手动添加的话，需要将 A 和 B 两个依赖都下载引入项目。而使用 Maven 来构建项目的话，我们只需要显示引入依赖 A，Maven 会通过读取项目文件（pom.xml），找出它们项目之间的依赖关系，将 A 和 B 都引入进来

3. Maven 使用坐标管理依赖，坐标包含三个标识信息，通过坐标可以唯一标识一个依赖。
   group：定义当前项目所属组织或公司的唯一标识，一般为组织或者公司名称域名倒写（也可以是域名倒写+项目名），例如 com.alibaba
   artifactId：项目的唯一 ID，一般是实际项目名称或者实际项目的一个模块名称（如果项目比较大，会划分多模块，例如 spring-core，spring-bean），例如 fastjson
   version：项目的版本号，例如 1.2.76

   ```xml
   <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.76</version>
   </dependency>
   ```

4. 可以下载到本地 将jar包放入文件夹中 比如/lib，将scope标记为system

   ```xml
   <dependency>
   <groupId>com.alibaba</groupId>  <!-- 可以自定义 -->
   <artifactId>fastjson</artifactId>   <!--可以自定义-->
   <version>1.2.75</version>
   <scope>system</scope>
   <systemPath>${basedir}\libs\fastjson-1.2.75.jar</systemPath>
   </dependency>
   ```

5. 基于 Maven 构建的项目，项目需要的依赖首先会在本地仓库中查找，如果没有找到则会从远程仓库下载到本地仓库。如果这两者都没有找到，Maven 就会报错。
   每个用户在自己电脑上会有个本地仓库，存放所有下载过的依赖包，Windows 环境下，位置一般在用户目录的 .m2/repository 目录下。 

   *`0918困难： 如果手动删除了每个本地仓库的包 那么就执行clean 和 install重新下载`*

6. maven项目目录：
   ${basedir}：根目录，pom.xml 文件以及项目所有的子目录
   ${basedir}/src/main/java：项目的 java 源代码
   ${basedir}/src/main/resources：项目的资源文件，例如 application.yml，xml 文件等
   ${basedir}/src/test/java：存放项目的 java 测试源代码
   ${basedir}/src/test/resources：存放项目测试要用的资源文件
   ${basedir}/target：编译打包输出目录
   ${basedir}/target/classes：编译输出目录
   ${basedir}/target/test-classes：测试编译输出目录
   ![img_1.png](D:/JavaCode/Idea_Projects/Learn_Java/img_1.png)

7. Maven 可以添加插件，插件其实就是一些 jar 文件，执行 Maven 命令时，真正完成功能的就是插件
   Springboot插件：

   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-maven-plugin</artifactId>
       </plugin>
     </plugins>
   </build>
   ```

8. Maven 编译插件，可以设置项目使用的 JDK 版本是通过编译插件指定

   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-compiler-plugin</artifactId>
         <version>3.8.1</version>
       </plugin>
       <configuration>
         <source>1.8</source>
         <target>1.8</target>
       </configuration>
     </plugins>
   </build>
   ```

9. 当项目较大时，可以使用父子Maven工程：
   基于Maven 构建的项目，一般会定义一个 parent POM 作为一组子 module 的聚合 POM。
   在 parent POM 中使用 <modules> 标签来定义它的一组子模块。虽然在 parent POM 中不会有什么实际构建产出，但是我们可以在 parent POM 中定义一些共同构建配置，依赖等，并且可以被子模块继承
   比如：
   在父模块的基础上创建子模块，直接在父模块项目右键创建子模块 maven-dao，maven-service，maven-controller

   又比如：
   学习spring时候，先创建spring6父工程，之后每一个新项目，都在父工程下面创建子模块

10. 创建Spring项目的一般步骤: ![img_2.png](D:/JavaCode/Idea_Projects/Learn_Java/img_2.png)
      注意：其中配置文件放在src/main/resources文件夹中

***

## day0918 Spring学习 Maven Log4j2

### Spring入门 自动装配对象的流程

1. 首先在resources文件中创建xml文件（右键resources-新建-xml配置文件-spring） 随便命名如"beans.xml"

   `0918困难：不出现xml配置文件等内容 需将idea升级为专业版`

2. 在"beans.xml"文件中添加需要自动加载的类：

   ```xml
   <bean id="user" class="org.example.User"></bean>
   ```

3. 在需要引用对象的时候，先获取ApplicationContext ac，使用ac上下文来获取已经自动装配的类对象：

   ```java
   package org.example;
   import org.junit.jupiter.api.Test;
   import org.springframework.context.ApplicationContext;
   import org.springframework.context.support.ClassPathXmlApplicationContext;
   
   public class TestUser {
   @Test
   public void test() {
      ApplicationContext ApplicationContext = new ClassPathXmlApplicationContext("beans.xml");
      User user = (User) ApplicationContext.getBean("user");
      user.add();
    }
   }
   ```

   `0918困难： jdk8无法使用spring-context6.0以上，只能使用5.x版本 所以下载jdk17 并在项目结构中选择新版本`

4. 自动装配原理分析：

   - 创建对象时，会执行无参构造

   - 反射

     - 通过解析xml文件中的bean标签的id和class
     - 使用反射根据类全路径来创建对象
       - 反射类对象 Class clazz = Class.forName("bean标签中的全路径")
       - 创建对象 clazz.getDeclaredConstructor().newInstance()

   - 创建的类放在了容器中 

     ```java
     DefaultListableBeanFactory.java:
     Map<String,BeanDefinition> beanDefinitionMap ...
     ```

      - key:String，对应bean标签的id
      - value：类的描述信息

### Log4j2

1. 日志可以记录并监控系统状态，能够帮助定位、诊断

2. 代替System.out等打印语句

3. 引入Log4j2：

   ```xml
   <!--log4j2的依赖-->
   <dependency>
       <groupId>org.apache.logging.log4j</groupId>
       <artifactId>log4j-core</artifactId>
       <version>2.19.0</version>
   </dependency>
   <dependency>
       <groupId>org.apache.logging.log4j</groupId>
       <artifactId>log4j-slf4j2-impl</artifactId>
       <version>2.19.0</version>
   </dependency>
   ```

   加入日志配置文件：  
   在类的根路径下（resources文件夹）提供log4j2.xml配置文件（文件名固定为：log4j2.xml，文件必须放到类根路径下。）

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <configuration>
       <loggers>
           <!--
               level指定日志级别，从低到高的优先级：
                   TRACE < DEBUG < INFO < WARN < ERROR < FATAL
                   trace：追踪，是最低的日志级别，相当于追踪程序的执行
                   debug：调试，一般在开发中，都将其设置为最低的日志级别
                   info：信息，输出重要的信息，使用较多
                   warn：警告，输出警告的信息
                   error：错误，输出错误信息
                   fatal：严重错误
           -->
           <root level="DEBUG">
               <appender-ref ref="spring6log"/>
               <appender-ref ref="RollingFile"/>
               <appender-ref ref="log"/>
           </root>
       </loggers>
   
       <appenders>
           <!--输出日志信息到控制台-->
           <console name="spring6log" target="SYSTEM_OUT">
               <!--控制日志输出的格式-->
               <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss SSS} [%t] %-3level %logger{1024} - %msg%n"/>
           </console>
   
           <!--文件会打印出所有信息，这个log每次运行程序会自动清空，由append属性决定，适合临时测试用-->
           <File name="log" fileName="d:/JavaCode/Idea_Projects/spring6_log/test.log" append="false">
               <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
           </File>
   
           <!-- 这个会打印出所有的信息，
               每次大小超过size，
               则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，
               作为存档-->
           <RollingFile name="RollingFile" fileName="d:/spring6_log/app.log"
                        filePattern="log/$${date:yyyy-MM}/app-%d{MM-dd-yyyy}-%i.log.gz">
               <PatternLayout pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>
               <SizeBasedTriggeringPolicy size="50MB"/>
               <!-- DefaultRolloverStrategy属性如不设置，
               则默认为最多同一文件夹下7个文件，这里设置了20 -->
               <DefaultRolloverStrategy max="20"/>
           </RollingFile>
       </appenders>
   </configuration>
   ```

4. 使用日志，手动添加日志
   首先创建针对当前类（如TestUser）的Logger对象 之后使用logger.info("");打印

   ```java
   private Logger logger = LoggerFactory.getLogger(HelloWorldTest.class);
   //某一行
   logger.info("执行成功");
   ```

### IoC容器 Inversion of Control 控制反转

1. Spring 通过 IoC 容器来管理所有 Java 对象的实例化和初始化，控制对象与对象之间的依赖关系。我们将由 IoC 容器管理的 Java 对象称为 Spring Bean，它与使用关键字 new 创建的 Java 对象没有任何区别。
2. 将对象的创建权利交出去，交给第三方容器负责。  
   将对象和对象之间关系的维护权交出去，交给第三方容器负责。  
   `0918问题：markdown换行：两个空格加Enter`
3. 如何实现控制反转的？DI（Dependency Injection）：依赖注入
4. 实现过程：  
   - xml配置文件中有Bean的定义信息（bean标签）
   - 存在一个接口BeanDefinitionReader，用于读取Bean定义信息，其不同的实现类对应xml配置文件、注解方式等不同方式
   - 读取Bean定义信息后放入IoC容器中，之后开始实例化
   - 实例化过程使用：工厂模式（BeanFactory）+反射
   - 初始化
   - 最终对象，通过SpringContext.getBean("bean id")向外提供
5. 依赖注入的实现：Spring创建对象的过程中，将对象依赖属性通过配置进行注入：  
   set注入和set注入两种
6. Bean管理：Bean对象的创建，以及Bean对象中属性的赋值（或者Bean对象之间关系维护，类中有另一类的对象）
7. IoC容器管理bean，创建bean之前创建IoC容器，Spring中提供了两种IoC容器实现：  
   - BeanFactory：Spring内部
   - ApplicationContext接口：BeanFactory提供的子接口，面向开发者！！！
   - ApplicationContext有多个实现类：
     - ConfigurableApplicationContext ApplicationContext的直接子接口，具有启动关闭和刷新上下文的能力
     - ClassPathXmlApplicationContext 读取类路径下的XML配置文件创建IOC容器对象 经常用!
     - FileSystemXmlApplicationContext 读取文件系统的XML配置文件创建IOC容器对象
     - WebApplicationContext 专门为Web创建IOC容器对象
   - ![img_3.png](D:/JavaCode/Idea_Projects/Learn_Java/img_3.png)

### IoC容器：基于XML获取bean

1. 获取bean，首先获取容器

   - 通过xml配置文件中给的id  注意返回Object对象

   ```java
   User user = (User) context.getBean("user");
   ```

   - 根据类的类型.class 来获取bean，注意这种要求容器中只能有一个该类型的bean 否则不知道哪一个

   ```java
   User user = (User) context.getBean(User.class);
   ```

   - 根据id和类型 两者一起获取bean

   ```java
   User user = (User) context.getBean("user",User.class);
   ```

2. 接口可以有实现类，并且可以有多个实现类，根据向上转型：可以由接口的引用接收实现类的对象实例  
   那么根据xml配置实现类，可以实现接口对象的装配吗？可以，注意xml文件中只标注实现类，不能标注接口（不能new）

   ```xml
       <bean id="UserDaoImpl" class="bean.UserDaoImpl"></bean>
   ```

   测试直接使用接口类名去实现自动装配！！！：

   ```java
       ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
       UserDao userDao = (UserDao) context.getBean(UserDao.class);
       System.out.println(userDao);
       
       //输出：bean.UserDaoImpl@9d157ff
   ```

   可以看到，在xml中只标注实现类的id和类型，也可以实现接口类名的自动装配，返回的依然是实现类的对象！！  
   一句话：如果组件类（在xml标注的）实现了接口，根据接口类型可以获取到bean  
   如果接口有多个实现类，还可以吗？不可以！：

   ```xml
       <bean id="UserDaoImpl" class="bean.UserDaoImpl"></bean>
       <bean id="UserDaoImpl2" class="bean.UserDaoImpl2"></bean>
   
   ```

   ```java
        UserDao userDao = (UserDao) context.getBean(UserDao.class);
        //输出：NoUniqueBeanDefinitionException: No qualifying bean of type 'bean.UserDao' available: expected single matching bean but found 2: UserDaoImpl,UserDaoImpl2
   ```

   原因：接口类型可以接收实现类对象，而现在实现类有两个，不唯一（仍然对应根据类的类型.class 来获取bean，注意这种要求容器中只能有一个该类型的bean 否则不知道哪一个）  
   一句话：一个接口有多个实现类，这些实现类都配置了bean，根据接口类型不能获取到bean

### IoC容器：基于XML 依赖注入（创建对象时设置属性值）

1. “依赖”这个词在这里指的是一个对象所依赖的其他对象或服务。在软件设计中，一个类或组件往往需要与其他类或组件交互来完成其功能，这些被交互的类或组件就构成了当前类的“依赖”。例如，一个用户服务类可能依赖于一个数据访问对象（DAO）来持久化用户数据。  
   依赖注入的“注入”是指容器在运行时将依赖的实例“注入”到需要它们的对象中。这个过程可以是显式的，也可以是隐式的。显式注入通常是通过构造函数、工厂方法或setter方法来实现的，而隐式注入则可能通过注解或约定来实现。  
   类型安全：Spring 会尝试将字符串值转换为属性所期望的类型。如果转换不可能（例如，字符串不是一个有效的整数表示），则 Spring 会抛出异常

2. setter注入  

   - 在类中加入各个属性的setter方法 （alt+insert键）  

   - 通过配置xml文件中 bean标签中加入property标签，将属性值填入，之后自动调用setter方法

     ```xml
        <bean id="book" class="DI.Book">
          <property name="bname" value="Book Name"></property>
          <property name="author" value="Whq"></property>
         </bean>
     ```

     这样IOC容器中装配对象时会自动调用setter对属性值进行注入

3. 构造器注入

   - 在类中加入有参构造（无参构造保留 否则会报错）
   - xml文件的bean标签中加入constructor-arg 标签

   ```xml
   <bean id="bookCon" class="DI.Book">
        <constructor-arg name="bname" value="Java"></constructor-arg>
        <constructor-arg name="author" value="Whqq"></constructor-arg>
    </bean>
   ```

   这样IOC容器中装配对象时会自动调用构造方法对属性值进行注入

4. 特殊值注入处理

   - 字面量 bean标签中的value属性值即为字面量 
   - null bean标签不能写value值（否则表示字符串"null"），而是在property下写<null></null> 标签表示空值注入
   - xml实体 也就是xml中有些符号无法使用 需要用转义字符 比如小于号 &lt;  写法value="a &lt; b"
   - CDATA节 不使用转义字符 而是使用CDATA节，同样可以实现同样功能

   ```xml
   <propery name="">
    <value><![CDATA[a<b]]></value> 
   </propery>> 
   ```

5. 对象属性注入
   例子：部门与员工 1：n  

   - 外部bean引入
     - 在员工的bean当中使用ref 引用部门的id值，注意普通属性用value，对象属性用ref ：

   ```xml
       <bean id="dept_one" class="bean.Dept">
           <property name="dept_name" value="SEU"></property>
       </bean>
   
       <bean id="emp" class="bean.Emp">
           <property name="empname" value="xiaowang"></property>
           <property name="dept" ref="dept_one"></property>
       </bean>
   ```

   - 内部bean
     - 在一个bean中再声明一个bean就是内部bean
     - 内部bean只能用于给属性赋值，不能在外部通过IOC容器获取，因此可以省略id属性

   ```xml
       <!-- 这个用于IOC创建 -->
       <bean id="dept_one" class="bean.Dept">
           <property name="dept_name" value="SEU"></property>
       </bean>
       
       <bean id="emp" class="bean.Emp">
           <property name="empname" value="xiaowang"></property>
           <property name="dept">
               <!--内部bean仅用于属性赋值 -->
               <bean id="dept_one" class="bean.Dept">
                   <property name="dept_name" value="SEU"></property>
               </bean>
           </property>
       </bean>
   ```

   - 级联赋值
     - 直接为对象属性的属性赋值

   ```xml
       <bean id="dept_one" class="bean.Dept">
           <property name="dept_name" value="SEU"></property>
       </bean>
   
       <bean id="emp" class="bean.Emp">
           <property name="empname" value="xiaowang"></property>
           <property name="dept.name" value="dept_one_name"></property>
       </bean>
   ```

6. 数组注入：
   <property>标签下写<array>标签 之后可以用value或者ref（对象）赋值

```xml
    <property name="hobbies">
        <array>
            <value>抽烟</value>
            <value>喝酒</value>
            <value>烫头</value>
        </array>
    </property>
```

List的话，则只需要将<array>改为<list>
map的话，则是<map><entry><key value> <value>或者<ref>  
list和map可以使用<util:list id="list">  <util:map id="map">  
需要加入xmlns:util约束  
之后在注入属性时使用ref="list"或ref="map"即可

7. p命名空间
   先引入p命名空间  

   ```xml
   <bean id="studentSix" class="com.atguigu.spring6.bean.Student"
       p:id="1006" p:name="小明" p:clazz-ref="clazzOne" p:teacherMap-ref="teacherMap"></bean>
   ```

8. 引入外部属性文件  
   jdbc 先引入依赖 后配置jdbc.properties 之后xml文件配置context和bean：  

   ```xml
   <!-- 引入外部属性文件 -->
   <context:property-placeholder location="classpath:jdbc.properties"/>
   ```

## day0919 IOC

### IOC继续

1. bean标签的scope属性（是否单实例）：

   - singleton（默认） 在IOC容器中，这个bean对象始终为单实例  在IOC容器初始化时创建对象
   - prototype        在IOC容器中有多个实例  在获取bean时（即getBean）才创建对象
   - request 在一个请求的范围内有效
   - session 在一个会话范围内有效

2. bean的生命周期（bean对象从创建到销毁）

   - bean对象创建(调用无参构造)
   - 给bean设置注入属性（注入setter或构造器）
     - property标签 name value/ref
   - bean后置处理器（初始化之前）
   - bean对象初始化（调用指定初始化方法 比如手动创建的void initMethod()）
     - 在bean标签中使用init-method="initMethod" 
   - bean后置处理器（初始化之后）
   - 创建完成
   - bean对象销毁（配置指定销毁方法 比如手动创建的void destroyMethod()）
     - 在bean标签中使用destroy-method="destroyMethod" 
     - ApplicationContext的实现类 ClassPathXmlApplicationContext类中context.close()方法销毁
   - 直至IOC容器关闭

3. 后置处理器 beanPost  

   - 新建一个类（如MyBeanPost）实现BeanPostProcessor接口
   - 接口中有两个方法:
     - Object postProcessBeforeInitialization(Object bean,String beanName)
     - Object postProcessAfterInitialization(Object bean,String beanName)
   - 实现接口的方法后，可以在xml进行配置后置处理器：

   ```xml
   <!-- bean的后置处理器要放入IOC容器才能生效 -->
   <bean id="myBeanProcessor" class="org.life.MyBeanProcessor"/>
   ```

4. FactoryBean 注意不是BeanFactory(用来返回Bean的工厂模式)

   - 使用时 先实现FactoryBean<T> 接口 一个返回对象，一个返回类：

   ```java
   package com.atguigu.spring6.bean;
   public class UserFactoryBean implements FactoryBean<User> {
      @Override
      public User getObject() throws Exception {
         return new User();
      }
   
       @Override
       public Class<?> getObjectType() {
           return User.class;
       }
   }
   ```

   - 在xml中配置bean标签，注意id仍是想获得的类或者随便命名（实际由getObject方法返回），而class则是UserFactoryBean

   ```xml
   <bean id="user" class="com.atguigu.spring6.bean.UserFactoryBean"></bean>
   ```

   - 重要：这样虽然xml配置的时factoryBean，但是实际获得的是根据重写的getObject()方法返回的对象
   - 通过这种机制，Spring可以帮助把复杂组件创建的详细过程和繁琐细节屏蔽，把简介使用界面展现给程序员

5. xml自动装配："autowire=byType/byName"  
   不用再写<property>标签手动setter注入了

   - 自动装配方式：byType
     - spring容器会遍历当前bean标签的class字段对应的类中所有的setter方法，会在容器中查找和set参数类型相同的bean对象，将其通过setter方法进行注入，未找到对应类型的bean对象则setter方法不进行注入。
     - 需要注入的set属性的类型和被注入的bean的类型需要满足a.isAssignableFrom(b)关系 即a与b同类型或者b能够转换为a
     - byType：根据类型匹配IOC容器中的某个兼容类型的bean，为属性自动赋值 
     - 若在IOC中，没有任何一个兼容类型的bean能够为属性赋值，则该属性不装配，即值为默认值null 
     - 若在IOC中，有多个兼容类型的bean能够为属性赋值，则抛出异常NoUniqueBeanDefinitionException
   - 自动装配方式：byName 
     - byName：将自动装配的属性的属性名，作为bean的id在IOC容器中匹配相对应的bean进行赋值
     - spring容器会按照set的属性的名称去容器中查找与当前bean对象属性同名的bean对象(id)，然后将查找到的对象通过setter方法注入到当前bean中，未找到对应名称的bean对象则setter方法不进行注入 要注入的set属性的名称和被注入的bean的名称必须一致

### 基于注解管理Bean 简化xml配置

1. 总的流程

   - 引入依赖 依然是spring-context
   - 开启组件扫描 
     - Spring 默认不使用注解装配 Bean
     - 因此我们需要在 Spring 的 XML 配置中，通过 context:component-scan 元素开启 Spring Beans的自动扫描功能。
     - 开启此功能后，Spring 会自动扫描指定的包（base-package 属性设置）及其子包下的所有类，如果类上使用了 @Component 注解，就将该类装配到容器中。

   ```xml
    <context:component-scan base-package="com.atguigu.spring6"></context:component-scan>
   ```

      - 注意要加入xmln:context约束和xsi:schemaLocation中加入context
      - 排除某些组件

   ```xml
   <context:component-scan base-package="com.atguigu.spring6">
   <!-- context:exclude-filter标签：指定排除规则 -->
   <!-- 
        type：设置排除或包含的依据
       type="annotation"，根据注解排除，expression中设置要排除的注解的全类名
       type="assignable"，根据类型排除，expression中设置要排除的类型的全类名
   -->
   <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
   <!--<context:exclude-filter type="assignable" expression="com.atguigu.spring6.controller.UserController"/>-->
   </context:component-scan>
   ```

      - 只扫描指定组件

   ```xml
   <context:component-scan base-package="com.atguigu" use-default-filters="false">
   <!-- context:include-filter标签：指定在原有扫描规则的基础上追加的规则 -->
   <!-- use-default-filters属性：取值false表示关闭默认扫描规则 -->
   <!-- 此时必须设置use-default-filters="false"，因为默认规则即扫描指定包下所有类 -->
   <!-- 
        type：设置排除或包含的依据
       type="annotation"，根据注解排除，expression中设置要排除的注解的全类名
       type="assignable"，根据类型排除，expression中设置要排除的类型的全类名
   -->
   <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
   <!--<context:include-filter type="assignable" expression="com.atguigu.spring6.controller.UserController"/>-->
   </context:component-scan>
   ```

2. 注解定义Bean 直接在类上标注注解即可 四种注解功能相同，只不过后面三个对应不同层 名字不同
   ![img_4.png](D:/JavaCode/Idea_Projects/Learn_Java/img_4.png)

3. 例子 若不标注value则自动将类名首字母小写即user

   ```java
      @Component(value = "user") //作用同xml中<bean id="user" class="...">
      public class User {
      }
   ```

4. @Autowired注解  

   - 可以注解在构造方法上 方法上 形参上 属性上 注解上

   - 属性注入，根据类型装配(会自动判断是否有某个兼容类型) 【默认是byType】

     - 例子，在UserServiceImpl中自动注入UserDao，在UserController中自动注入UserService

       ```java
       @Autowired
       private UserDao userDao;
       ```


        @Autowired
        private UserService userService;
        ```
     - 以上构造方法和setter方法都没有提供，经过测试，仍然可以注入成功。

   - setter方法注入
     - 定义属性后，加入setter方法，在setter方法上加上@Autowired注解

   ```java
private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
   ```

   - 构造方法注入
     - 定义属性后加入构造方法，在之上加上@Autowired注解
     - 也可以放在构造方法形参上注入
     - 若只有一个有参数构造函数（此时也不能有无参构造），则可以不写注解 也可以实现自动注入

   ```java
//@Autowired
public UserServiceImpl(@Autowired UserDao userDao) {
   this.userDao = userDao;
}
   ```

5. @Autowired和@Qualifier注解 byName注入
   如果一个接口（比如UserDao）有两个实现类（UserDaoImpl和UserDaoRedisImpl），那么此时不能自动进行装配（byType异常）  
   此时需要用byName进行装配

   ```java
   @Autowired
   @Qualifier("userDaoImpl") // 指定bean的名字
   private UserDao userDao;
   ```

   @Qualifier注解主要用于指定bean名字

6. @Resource注解  

   - 也可以完成属性注入，同@Autowired功能
   - 是JDK（而非Spring）扩展包注解，即标准注解，更具通用性
   - 默认byName(Bean的id或者@Componet的value值)，未指定name时使用属性名（即定义的对象名）作为name。若找不到，则通过byType通过类型注入
   - @Autowired默认byType，若byName需要配合@Qulifier
   - @Resource只能用在属性上和setter方法上
   - 由于是JDK拓展包，因此需要额外引入依赖 jakarta.annotation

### Spring全注解开发 使用配置类代替xml配置文件

1. 建立配置类：类上加入注解@Configuration
2. 开启组件扫描：在类上加上注解@ComponentScan("扫描包名")
3. 使用时不再是找xml配置文件，而是使用AnnotationConfigApplicationContext(配置类.class)加载配置类：

```java
   @Test
   public void testAllAnnotation(){
       ApplicationContext context = new AnnotationConfigApplicationContext(Spring6Config.class);
       UserController userController = context.getBean("userController", UserController.class);
       userController.out();
       logger.info("执行成功");
   }
```

## day0920 IOC手动实现

### IOC手动实现

1. 创建注解@Bean(同@Component)和@Di(同@Autowired)

   ```java
   @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Bean {
   }  
   
   @Target(ElementType.FIELD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Di {
   }
   ```

2. @Bean 建立工厂模式的IOC容器 

   - 建立接口ApplicationContext
   - IOC容器map
   - 返回单实例
   - 包扫描路径，使用构造函数实现，扫描哪个类有注解，并通过反射进行实例化
   - 实例化后放入Map中，供以后使用

3. 列出当前文件夹下的资源：

   ```java
   Enumeration<URL> resources = Thread.currentThread()
                                               .getContextClassLoader()
                                               .getResources(basePackage);
   ```

4. 判断文件类型:路径判断后缀名

   ```java
   pak_class_path.contains(".class")
   ```

    **`出现了两个错误：一个是要记住Java变量修改后要保存 另外".class"替换为"" 记住有"."`**

5. 自动注入 @Di  

   - 也是在ApplicationContext的构造函数中实现
   - 遍历当前所有加载了的IOC容器里的对象（即Map中的），得到所有已经加载的类
     `Map的遍历：Map.EntrySet方法，然后用set遍历`
   - 找到已加载的类的所有field属性字段，看是否有@Di注解 
   - 若有@Di则将该field进行set，也就是把map里面的(field.getType)对象注入到这个field中
   - 注意先将Accessible设置为true 从而注入private对象

6. 这个IOC没有考虑一个接口有多个实现类，所以可以进行改进：  
   将Map<Class,Object> 改进为Map<Class,Map<String,Object>>  
   内部Map的String对应注解@Bean的一个新增属性name（用来指定 bean 的唯一名称）

### AOP

1. 现在如果我们要实现附加功能如日志，都需要在实现类或方法中添加日志代码，对实际业务代码有干扰，并且附加功能分散于业务中，不利于统一维护：

   ```java
   public class CalculatorLogImpl implements Calculator {
       
       @Override
       public int add(int i, int j) {
       
           System.out.println("[日志] add 方法开始了，参数是：" + i + "," + j);
       
           int result = i + j;
       
           System.out.println("方法内部 result = " + result);
       
           System.out.println("[日志] add 方法结束了，结果是：" + result);
       
           return result;
       }
   ```

   解决问题的困难：要抽取的代码在方法内部，靠以前把子类中的重复代码抽取到父类的方式没法解决。所以需要引入新的技术。

2. 二十三种设计模式中的一种，属于结构型模式  
   它的作用就是通过提供一个代理类，让我们在调用目标方法的时候，不再是直接对目标方法进行调用，而是通过代理类间接调用   
   让不属于目标方法核心逻辑的代码从目标方法中剥离出来——解耦。调用目标方法时先调用代理对象的方法，减少对目标方法的调用和打扰，同时让附加功能能够集中在一起也有利于统一维护

3. 静态代理：就是封装一个代理类，然后这个代理类去调用原来实现类  
   将日志功能集中到一个代理类中，将来有任何日志需求，都通过这一个代理类来实现。这就需要使用动态代理技术了

4. 动态代理

   - 只需要一个代理类
   - ![img_5.png](D:/JavaCode/Idea_Projects/Learn_Java/img_5.png)

   ```java
   public class ProxyFactory {
   
      private Object target;
   
      public ProxyFactory(Object target) {
         this.target = target;
      }
   
      public Object getProxy(){
   
         /**
          * newProxyInstance()：创建一个代理实例
          * 其中有三个参数：
          * 1、classLoader：加载动态生成的代理类的类加载器
          * 2、interfaces：目标对象实现的所有接口的class对象所组成的数组
          * 3、invocationHandler：设置代理对象实现目标对象方法的过程，即代理类中如何重写接口中的抽象方法
          */
         ClassLoader classLoader = target.getClass().getClassLoader();
         Class<?>[] interfaces = target.getClass().getInterfaces();
         InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               /**
                * proxy：代理对象
                * method：代理对象需要实现的方法，即其中需要重写的方法
                * args：method所对应方法的参数
                */
               Object result = null;
               try {
                  System.out.println("[动态代理][日志] "+method.getName()+"，参数："+ Arrays.toString(args));
                  result = method.invoke(target, args);
                  System.out.println("[动态代理][日志] "+method.getName()+"，结果："+ result);
               } catch (Exception e) {
                  e.printStackTrace();
                  System.out.println("[动态代理][日志] "+method.getName()+"，异常："+e.getMessage());
               } finally {
                  System.out.println("[动态代理][日志] "+method.getName()+"，方法执行完毕");
               }
               return result;
            }
         };
   
         return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
      }
   }
   ```

   - 解释代码：

     - 代理类工厂，工厂类中的object对象作为以后需要加入附加功能的类对象（通过工厂的构造函数），之后调用工厂的getProxy获得带有附加代理功能的原始类对象，该对象可以调用原本的类的方法

     ```java
      @Test
      public void testDynamicProxy(){
      ProxyFactory factory = new ProxyFactory(new CalculatorLogImpl());
      Calculator proxy = (Calculator) factory.getProxy();
      proxy.div(1,0);
      //proxy.div(1,1);
      }
     ```

## day0922 AOP

### AOP总体介绍

1. 是设计思想，通过*预编译方式和运行期动态代理方式*实现，不修改源码的情况下，给程序动态统一添加额外功能的一种技术
2. 可以对业务逻辑的各个部分进行隔离，降低耦合度，提高可重用性
3. 术语
   - 横切关注点
     - 逻辑概念，分散在每个各个模块中解决问题，如用户验证、日志管理、事务处理、数据缓存都属于横切关注点
     - 从每个方法中抽取出来的同一类非核心业务。在同一个项目中，我们可以使用多个横切关注点对相关方法进行多个不同方面的增强。
     - 有十个附加功能，就有十个横切关注点
     - ![img_7.png](D:/JavaCode/Idea_Projects/Learn_Java/img_7.png)
   - 通知（或这叫增强）
     - 想要增强的功能，比如安全、事务、日志等
     - 每个横切关注点要做的事情需要写一个方法来实现，这个方法就叫通知方法
     - 前置通知：在被代理的目标方法前执行
     - 返回通知：在被代理的目标方法成功结束后执行（寿终正寝）
     - 异常通知：在被代理的目标方法异常结束后执行（死于非命）
     - 后置通知：在被代理的目标方法最终结束后执行（盖棺定论）
     - 环绕通知：使用try…catch…finally结构围绕整个被代理的目标方法，包括上面四种通知对应的所有位置
   - 切面
     - 封装通知方法的类
     - ![img_6.png](D:/JavaCode/Idea_Projects/Learn_Java/img_6.png)
   - 目标
     - 被代理的目标对象  对象！对象！对象！(上述代码中构造方法中传递的参数new CalculatorImpl())
   - 代理
     - 向目标对象应用统治之后创建的代理对象 (上述代码中工厂get方法返回的proxy对象)
   - 连接点
     - 逻辑概念
     - 也就是spring允许使用通知的地方
     - ![img_8.png](D:/JavaCode/Idea_Projects/Learn_Java/img_8.png)
   - 切入点
     - 定位连接点的方式
     - 每个类的方法中包含多个连接点，怎么去查找连接点，就是切入点的事情
     - 将连接点看作数据库的记录，切入点就是sql查询语句
     - Spring的AOP可以通过切入点定位连接点，即定位需要增强的方法位置
     - org.springframework.aop.Pointcut接口描述，使用类和方法作为连接点的查询条件

### Spring基于注解的AOP

![img_11.png](D:/JavaCode/Idea_Projects/Learn_Java/img_11.png)

1. 动态代理的分类

   - JDK动态代理
     - 代理的对象有接口的情况，通过JDK动态代理生成接口实现类代理对象
     - 需要被代理目标类必须实现接口，要求代理对象和目标对象实现同样的接口
     - ![img_9.png](D:/JavaCode/Idea_Projects/Learn_Java/img_9.png)
   - cglib动态代理
     - 代理的对象没有接口的情况
     - 通过继承被代理的目标类来实现代理，不需要目标类再实现接口
     - ![img_10.png](D:/JavaCode/Idea_Projects/Learn_Java/img_10.png)

2. AspectJ：是AOP思想的一种实现。

   - 本质上是静态代理，将代理逻辑”织入“被代理的目标类编译得到的字节码文件中，所以最终效果意识动态的
   - weaver是织入器，Spring只是借用了AspectJ的注解

3. 步骤：

   - 引入aop和aspects依赖
   - 创建目标接口和实现类 如Calculator和CalculatorImpl
   - 在resources中添加配置文件 bean.xml
     - 约束新增aop
     - 开启组件扫描 context:component-scan
     - 开启Aspectj自动代理，为目标对象生成代理
       - <aop:aspectj-autoproxy></aop:aspectj-autoproxy>

   ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!--
        基于注解的AOP的实现：
        1、将目标对象和切面交给IOC容器管理（注解+扫描）
        2、开启AspectJ的自动代理，为目标对象自动生成代理
        3、将切面类通过注解@Aspect标识
    -->
    <context:component-scan base-package="com.atguigu.aop.annotation"></context:component-scan>
    
        <aop:aspectj-autoproxy />
    </beans>
   ```

    - 创建切面类
      - 加上注解@Aspect和@Component
      - 切入点表达式语法
        - ![img_12.png](D:/JavaCode/Idea_Projects/Learn_Java/img_12.png)
      - 通知类型（方法前@Before()、方法后@After、异常@AfterThrowing、返回@AfterReturning、环绕@Around）
        - 每个注解都有一个属性 切入点表达式配置切入点（即表达式）
        - @AfterReturning注解有returning属性，可以给返回对象起名，这样在方法参数中可以用Object result接收目标方法结果
        - @Around注解可以在方法参数中加入更高级的ProceedingJoinPoint 并且环绕需要将结果进行返回！见代码

    ```java
    package annoaop;
   
    import org.aspectj.lang.ProceedingJoinPoint;
    import org.aspectj.lang.annotation.Around;
    import org.aspectj.lang.annotation.Aspect;
    import org.springframework.stereotype.Component;
    
    import java.util.Arrays;
    
    @Aspect
    @Component
    public class LogAspect {
    //    @Before(value = "execution(* annoaop.*.*(..))")
    //    public void beforeMethod(){
    //        System.out.println("[Logger] 前置通知");
    //    }
    
        @Around(value = "execution(* annoaop.*.*(..))")
        public Object aroundMethod(ProceedingJoinPoint joinPoint){
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            String argsString = Arrays.toString(args);
            Object result = null;
            try {
                System.out.println("[Logger] 环绕通知:方法前，"+"目标方法："+methodName+"参数："+argsString);
                result = joinPoint.proceed();
                System.out.println("[Logger] 环绕通知:方法后，"+"目标方法："+methodName+"参数："+argsString);
            }catch (Throwable e){
                System.out.println("[Logger] 环绕通知:方法异常时，"+"目标方法："+methodName+"参数："+argsString);
            }finally {
                System.out.println("[Logger] 环绕通知:方法执行完毕，"+"目标方法："+methodName+"参数："+argsString);
            }
            return result;
        }
    }
   
    ```

    **`0922问题：输出数组 Arrays.toString(数组)`**

4. 重用切入点表达式

   ```java
   //重用切入点表达式声明
   @Pointcut("execution(* com.atguigu.aop.annotation.*.*(..))")
   public void pointCut(){}
     
   //在同一个切面中使用（切面类）
   @Before("pointCut()")
   public void beforeMethod(JoinPoint joinPoint){
       String methodName = joinPoint.getSignature().getName();
       String args = Arrays.toString(joinPoint.getArgs());
       System.out.println("Logger-->前置通知，方法名："+methodName+"，参数："+args);
   }
   
   //在不同切面中使用（切面类）
   @Before("com.atguigu.aop.CommonPointCut.pointCut()")
   public void beforeMethod(JoinPoint joinPoint){
       String methodName = joinPoint.getSignature().getName();
       String args = Arrays.toString(joinPoint.getArgs());
       System.out.println("Logger-->前置通知，方法名："+methodName+"，参数："+args);
   }
   ```


    ```

### Spring基于xml的AOP

1. xml中开启组件扫描
2. 加入<aop:config>
   - 配置切面类 <aop:aspect>
   - 配置切入点 <aop:pointcut>
   - 配置通知类型 
     - <aop:before after ...>

## day0923

### Spring-Junit整合

1. 原本每个新建的测试类，都需要加上寻找spring配置的代码：

   ```java
       ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
       Calculator calculator = applicationContext.getBean(Calculator.class);
   ```

2. pom.xml中通过加入junit依赖和spring-test依赖

3. bean.xml中添加包扫描打开

4. 新建测试类：

   - 类上添加注解：@SpringJunitConfig(locations="classpath:bean.xml") 代替上面的读取bean.xml的context对象
   - 类中需要使用何种对象，直接@autowired进行注入
   - 测试的方法：@Test注解（Junit5的包）
   - 直接进行测试

   ```java
   @SpringJUnitConfig(locations = "classpath:beans.xml")
   public class SpringJUnit5Test {
    
       @Autowired
       private User user;
    
       @Test
       public void testUser(){
           System.out.println(user);
       }
   }
   ```

5. junit4版本有不同:注解不同、导入的@Test包不同：

   ```java
   @RunWith(SpringJUnit4ClassRunner.class)
   @ContextConfiguration("classpath:beans.xml")
   public class SpringJUnit4Test {
    
       @Autowired
       private User user;
    
       @Test //包名不带api
       public void testUser(){
           System.out.println(user);
       }
   }
   ```

### 事务-JDBC

1. 普通步骤

   - 加入依赖：
     - spring-jdbc（Spring持久化层支持包）、
     - mysql-connector-java（MySQL驱动）、
     - druid（数据源）
   - 创建jdbc.properties 

   ```xml
    jdbc.user=root
    jdbc.password=root
    jdbc.url=jdbc:mysql://localhost:3306/spring?characterEncoding=utf8&useSSL=false
    jdbc.driver=com.mysql.cj.jdbc.Driver
   ```

   - spring配置文件beans.xml
     - 导入外部属性文件（就上面的properties）
     - 配置数据源
     - 配置jdbcTemplate对象

   ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
        
            <!-- 导入外部属性文件 -->
            <context:property-placeholder location="classpath:jdbc.properties" />
        
            <!-- 配置数据源 -->
            <bean id="druidDataSource" class="com.alibaba.druid.pool.DruidDataSource">
                <property name="url" value="${jdbc.url}"/>
                <property name="driverClassName" value="${jdbc.driver}"/>
                <property name="username" value="${jdbc.user}"/>
                <property name="password" value="${jdbc.password}"/>
            </bean>
        
            <!-- 配置 JdbcTemplate -->
            <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
                <!-- 装配数据源 -->
                <property name="dataSource" ref="druidDataSource"/>
            </bean>
        
        </beans>
   ```


    ```

   - 之后就可以创建测试类（@SpringJUnitConfig注解），对jdbcTemplate对象（@Autowired注入）进行测试

   ```java
 @SpringJUnitConfig(locations = "classpath:beans.xml")
 public class JDBCTemplateTest {
 
     @Autowired
     private JdbcTemplate jdbcTemplate;
 
 }
   ```

   - 增删改功能都由jdbcTemplate.update(String sql,参数args)方法

   ```java
 @Test
 //测试增删改功能
 public void testUpdate(){
 //添加功能
 String sql = "insert into t_emp values(null,?,?,?)";
 int result = jdbcTemplate.update(sql, "张三", 23, "男");
 
     //修改功能
     //String sql = "update t_emp set name=? where id=?";
     //int result = jdbcTemplate.update(sql, "张三atguigu", 1);
 
     //删除功能
     //String sql = "delete from t_emp where id=?";
     //int result = jdbcTemplate.update(sql, 1);
 }
 
 
   ```

   - 查询
     - 返回对象
       - 用一个类，包含各个属性，以及各个setter和getter方法
       - 依然是写String sql语句，用jdbcTemplate.queryForObject方法
       - 此方法参数第三个参数是args，第二个参数是一个封装接口，将数据封装为对象或集合
       - 第二个参数需要实现，有两个参数rs和rowNum
         - 可以用lambda表达式：new对象，rs.getString("SQL里的属性名")对对象进行设置参数，返回对象。
         - 也可以用该接口的实现类BeanPropertyRowMapper<>(对象.class)
     - 返回List集合
       - 同上，只是jdbcTemplate.query方法
       - 参数同上，不用改，可以用该接口的实现类BeanPropertyRowMapper<>(对象.class)

2. 由于事务特性，所以需要开启事务、提交事务、回滚事务、关闭数据库连接等，手工实现比较麻烦

3. 声明式事务，通过配置让框架实现事务功能 保证数据库操作能够符合事务特征

4. 基于注解的声明式事务

   - 添加组件扫描 bean.xml

   - 创建组件 @Controller,@Service,@Repository

     - Controller调用service的一条语句
     - Service调用Dao的多条语句实现
     - Dao层调用jdbcTemplate的方法

   - 需要添加事务保证正确性 一条语句执行失败则回滚 只有都成功才成功

     - 向spring配置文件加入tx命名空间 xmlns:tx="http://www.springframework.org/schema/tx  http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd"

       - 事务管理器transactionManager 用来指定向哪个数据源进行管理

         - ```xml
           <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
               <property name="dataSource" ref="druidDataSource"></property>
           </bean>
           ```

       - 开启事务的注解驱动  通过注解@Transactional所标识的方法或标识的类中所有的方法，都会被事务管理器管理事务

         - ```xml
           <tx:annotation-driven transaction-manager="transactionManager" />
           ```

     - 因为service层表示业务逻辑层，一个方法表示一个完成的功能，因此处理事务一般在service层处理

       - 向Service层的方法或者类上添加注解@Transactional

   - 事务注解的属性

     - @Transactional(readOnly = true) 只读属性
       - 对一个查询操作来说，如果我们把它设置成只读，就能够明确告诉数据库，这个操作不涉及写操作。这样数据库就能够针对查询操作来进行优化。
     - @Transactional(timeout = 3) 设置超时时间
       - 出问题的程序应该被回滚，撤销它已做的操作，事务结束，把资源让出来，让其他正常程序可以执行
     - 回滚策略 声明式事务默认只针对运行时异常回滚，编译时异常不回滚 可以通过设置策略在某种异常时不回滚 会针对某种异常时回滚
     - @Transactional(isolation = ) 隔离级别
       - 级别越高，隔离性（事务间互不影响）越好，但并发性低
       - @Transactional(isolation = Isolation.DEFAULT)//使用数据库默认的隔离级别
       - @Transactional(isolation = Isolation.READ_UNCOMMITTED)//读未提交
       - @Transactional(isolation = Isolation.READ_COMMITTED)//读已提交
       - @Transactional(isolation = Isolation.REPEATABLE_READ)//可重复读
       - @Transactional(isolation = Isolation.SERIALIZABLE)//串行化
       - ![img_13.png](D:/JavaCode/Idea_Projects/Learn_Java/img_13.png)
     - @Transactional(isolation = propagation)
       - 两个事务如a调用b时，它们之间的嵌套关系，是合并一个事务还是开启新事务？
       - 有7种传播行为，最常见的时REQUIRED(没有就新建，有就加入)和REQUIRES_NEW(不管如何，都开启新事务，原事务被挂起)
       - 如果a调用两个b，当b第二次失败时，前面一种全部回滚；后面一种会只回滚第二个b

   - 纯注解，无xml （Junit简单注解方式无法使用）

     - 新建类 添加注解：
       - @Configuration
       - @ComponentScan("包名")
       - @EnableTransactionManagement 开启事务管理
     - 建立数据源DataSource对象、jdbcTemplate对象、DataSourceTransactionManager对象 @Bean注解用于返回对象的方法上

   ```java
   package com.atguigu.spring6.config;
   
   import com.alibaba.druid.pool.DruidDataSource;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.ComponentScan;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.jdbc.core.JdbcTemplate;
   import org.springframework.jdbc.datasource.DataSourceTransactionManager;
   import org.springframework.transaction.annotation.EnableTransactionManagement;
   import javax.sql.DataSource;
   
   @Configuration
   @ComponentScan("com.atguigu.spring6")
   @EnableTransactionManagement
   public class SpringConfig {
   
       @Bean
       public DataSource getDataSource(){
           DruidDataSource dataSource = new DruidDataSource();
           dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
           dataSource.setUrl("jdbc:mysql://localhost:3306/spring?characterEncoding=utf8&useSSL=false");
           dataSource.setUsername("root");
           dataSource.setPassword("root");
           return dataSource;
       }
   
       @Bean(name = "jdbcTemplate")
       public JdbcTemplate getJdbcTemplate(DataSource dataSource){
           JdbcTemplate jdbcTemplate = new JdbcTemplate();
           jdbcTemplate.setDataSource(dataSource);
           return jdbcTemplate;
       }
   
       @Bean
       public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource){
           DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
           dataSourceTransactionManager.setDataSource(dataSource);
           return dataSourceTransactionManager;
       }
   }
   
   ```

5. 基于xml的声明式事务 新增事务增强、切入点设置

   - 其它同以前步骤
   - 新增事务增强、切入点设置  效果同@Transactional

   ```xml
   <aop:config>
       <!-- 配置事务通知和切入点表达式 -->
       <aop:advisor advice-ref="txAdvice" pointcut="execution(* com.atguigu.spring.tx.xml.service.impl.*.*(..))"></aop:advisor>
   </aop:config>
   <!-- tx:advice标签：配置事务通知 -->
   <!-- id属性：给事务通知标签设置唯一标识，便于引用 -->
   <!-- transaction-manager属性：关联事务管理器 -->
   <tx:advice id="txAdvice" transaction-manager="transactionManager">
       <tx:attributes>
           <!-- tx:method标签：配置具体的事务方法 -->
           <!-- name属性：指定方法名，可以使用星号代表多个字符 -->
           <tx:method name="get*" read-only="true"/>
           <tx:method name="query*" read-only="true"/>
           <tx:method name="find*" read-only="true"/>
       
           <!-- read-only属性：设置只读属性 -->
           <!-- rollback-for属性：设置回滚的异常 -->
           <!-- no-rollback-for属性：设置不回滚的异常 -->
           <!-- isolation属性：设置事务的隔离级别 -->
           <!-- timeout属性：设置事务的超时属性 -->
           <!-- propagation属性：设置事务的传播行为 -->
           <tx:method name="save*" read-only="false" rollback-for="java.lang.Exception" propagation="REQUIRES_NEW"/>
           <tx:method name="update*" read-only="false" rollback-for="java.lang.Exception" propagation="REQUIRES_NEW"/>
           <tx:method name="delete*" read-only="false" rollback-for="java.lang.Exception" propagation="REQUIRES_NEW"/>
       </tx:attributes>
   </tx:advice>
   
    
   ```

## day0924 Resources

### Spring Resources

1. Java的标准java.net.URL类和各种URL前缀的标准处理程序无法满足所有对low-level资源的访问，比如：   
   没有标准化的 URL 实现可用于访问需要从类路径或相对于 ServletContext 获取的资源  
   并且缺少某些Spring所需要的功能，例如检测某资源是否存在等  
   而Spring的Resource声明了访问low-level资源的能力

2. Spring 的 Resource 接口位于 org.springframework.core.io 中。 旨在成为一个更强大的接口，用于抽象对低级资源的访问

3. Resource接口继承了InputStreamSource接口，提供了很多InputStreamSource所没有的方法

4. Resource 接口是 Spring 资源访问策略的抽象，它本身并不提供任何资源访问实现，具体的资源访问由该接口的实现类完成——每个实现类代表一种资源访问策略

5. Resource一般包括这些实现类：UrlResource、ClassPathResource、FileSystemResource、ServletContextResource、InputStreamResource、ByteArrayResource
   `0924问题：怎样读取文件内容 用inputStream：byte []b;while(in.read(b)!=-1)`

6. Spring 提供如下两个标志性接口：
   （1）ResourceLoader ： 该接口实现类的实例可以获得一个Resource实例。
   （2）ResourceLoaderAware ： 该接口实现类的实例将获得一个ResourceLoader的引用。
   在ResourceLoader接口里有如下方法:  
   Resource getResource（String location） ： 该接口仅有这个方法，用于返回一个Resource实例。  
   ApplicationContext实现类都实现ResourceLoader接口，因此ApplicationContext可直接获取Resource实例。

7. 如果ApplicationContext是FileSystemXmlApplicationContext，res就是FileSystemResource实例；如果ApplicationContext是ClassPathXmlApplicationContext，res就是ClassPathResource实例

8. 当Spring应用需要进行资源访问时，实际上并不需要直接使用Resource实现类，而是调用ResourceLoader实例的getResource()方法来获得资源，ReosurceLoader将会负责选择Reosurce实现类，也就是确定具体的资源访问策略，从而将应用程序和具体的资源访问策略分离开来

9. 使用ApplicationContext访问资源时，可通过不同前缀指定强制使用指定的ClassPathResource、FileSystemResource等实现类

   ```java
   Resource res = ctx.getResource("calsspath:bean.xml");
   Resrouce res = ctx.getResource("file:bean.xml");
   Resource res = ctx.getResource("http://localhost:8080/beans.xml");
   ```

10. ResourceLoaderAware接口实现类的实例将获得一个ResourceLoader的引用，ResourceLoaderAware接口也提供了一个setResourceLoader()方法，该方法将由Spring容器负责调用，Spring容器会将一个ResourceLoader对象作为该方法的参数传入  
    如果把实现ResourceLoaderAware接口的Bean类部署在Spring容器中，Spring容器会将自身当成ResourceLoader作为setResourceLoader()方法的参数传入。由于ApplicationContext的实现类都实现了ResourceLoader接口，Spring容器自身完全可作为ResorceLoader使用。

11. Bean示例访问资源：

    - 代码中获取Resource示例 使用ApplicationContext.getResource()方法
    - 使用依赖注入 把Resource作为属性
      - private Resource resource;
      - 在配置文件中，<bean><property name="resource" value="文件的classpath">

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="resourceBean" class="com.atguigu.spring6.resouceloader.ResourceBean" >
          <!-- 可以使用file:、http:、ftp:等前缀强制Spring采用对应的资源访问策略 -->
          <!-- 如果不采用任何前缀，则Spring将采用与该ApplicationContext相同的资源访问策略来访问资源 -->
            <property name="res" value="classpath:atguigu.txt"/>
        </bean>
    </beans>
    ```

### 国际化 比较简单  有用时再深入学

### 数据校验

1. 原本数据校验和业务代码耦合在一起，spring validation允许通过注解的方式定义对象校验规则

2. Spring Validation是对Hibernate Validator的进一步封装

3. 校验方法一：通过Validator接口

   - 实现org.springframework.validation.Validator接口，然后在代码中调用这个类

   - 步骤

     - 引入依赖 hibernate-validator 和 jakarta.el

     - 创建实体类 定义属性 创建setter和getter

     - 创建实现Validator接口的实现类，重写接口方法编写校验逻辑

       - boolean supports(Clazz<?> clazz) 检查校验生效的类型

         - 比如return Person.class.equals(clazz);

       - void validate(Object target,Errors errors) 校验的实际规则

         - 比如name 不能为空，使用ValidatorUtils工具类提供方法 第二个字段是属性的名称field，第三个字段是errorCode,第四个字段是提示信息

         - ```java 
           ValidatorUtils.rejectIfEmpty(errors,"name","name.empty","name is null");
           ```

         - 比如年龄不能小于0，大于200

         - ```java
           Person person = (Person) target;
           if (person.getAge() < 0){
             errors.rejectValue("age", "age.value.error","age lt 0");
           }else if(person.getAge() > 200){
             errors.rejectValue("age", "age.value.error","age gt 200");
           }
           ```

     - 测试使用

       - 创建person对应DataBinder
       - 设置校验器
       - 调用方法执行校验
       - 得到校验结果
       - 

       ```java 
         //创建person对象
       Person person = new Person();
       person.setName("lucy");
       person.setAge(-1);
       
       // 创建Person对应的DataBinder
       DataBinder binder = new DataBinder(person);
       
       // 设置校验
       binder.setValidator(new PersonValidator());
       
       // 由于Person对象中的属性为空，所以校验不通过
       binder.validate();
       
       //输出结果
       BindingResult results = binder.getBindingResult();
       System.out.println(results.getAllErrors());
       ```

4. 校验方法二：Bean Validation注解实现

   - 步骤

     - 创建配置类，配置LocalValidatorFactoryBean

       - @Configuration
       - @ComponentScan
       - @Bean  返回LocalValidatorFactoryBean()

     - 创建实体类，定义属性，生成setter和getter，属性上面使用注解设置校验规则

       - @常用注解说明
         - @NotNull 限制必须不为null
         - @NotEmpty 只作用于字符串类型，字符串不为空，并且长度不为0
         - @NotBlank 只作用于字符串类型，字符串不为空，并且trim()后不为空串
         - @DecimalMax(value) 限制必须为一个不大于指定值的数字
         - @DecimalMin(value) 限制必须为一个不小于指定值的数字
         - @Max(value) 限制必须为一个不大于指定值的数字
         - @Min(value) 限制必须为一个不小于指定值的数字
         - @Pattern(value) 限制必须符合指定的正则表达式
         - @Size(max,min) 限制字符长度必须在min到max之间
         - @Email 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式

     - 创建校验器类

       - 使用jakarta.validation.Validator校验

         - ```java 
           public  boolean validator(User user){
            Set<ConstraintViolation<User>> sets =  validator.validate(user);
            return sets.isEmpty();
           }
           ```

       - 使用org.springframework.validation.Validator校验

         - ```java
           public boolean validaPersonByValidator(User user) {
               BindException bindException = new BindException(user, user.getName());
               validator.validate(user, bindException);
               return bindException.hasErrors();
           }
           ```

       - 两者都是再类上@Service或者@Component 然后有属性Validator validator，使用@Autowired（前面配置类里面的@Bean注入）

     - 测试使用 

       - 通过配置类获取context，通过context获取校验器context.getBean()

5. 校验方式三：基于方法实现校验

   - 步骤
     - 创建配置类，配置MethodValidationPostProcessor
       - @Configuration
       - @ComponentScan
       - @Bean  配置MethodValidationPostProcessor()
     - 创建实体类，使用注解设置校验规则
     - 定义Service类，通过注解操作对象
       - 这个类使用@Service @Validated注解
       - 里面定义检验方法
         - 方法参数中加上注解@NotNull和@Valid 参数使用要校验的对象
         - 应该是@Valid会校验对象的属性上的注解
     - 测试使用
       - 加载配置类 contexxt
       - context.getBean得到上面Service
       - service.校验方法

6. 自定义校验注解 以后需要时再仔细学

### AOT 提前编译  Ahead of Time  为了云原生

1. JIT Just In Time 即时编译 边运行边编译（字节码转换为机器码）
2. AOT 提前编译 把源码直接转换为机器码，启动快，占用内存低。但是不能优化，程序安装时间长
3. 一般AOT运行流程：.java->class->（使用jaotc编译工具）->.so(程序函数库，即编译好的可以供其他程序使用的代码和数据)  
   JVM对.so进行load即可
4. JVM加载预编译成的二进制库，可以直接执行，不用再即时编译
5. 不能跨平台运行（因为已经是机器码了）
6. 即时编译 (JIT) 是默认模式，Java Hotspot 虚拟机使用它在运行时将字节码转换为机器码
7. 提前编译 (AOT)由新的 GraalVM 编译器支持，并允许在构建时将字节码直接静态编译为机器码

