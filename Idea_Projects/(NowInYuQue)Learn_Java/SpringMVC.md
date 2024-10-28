# SpringMVC

## day0925 SpringMVC

### Servlet

1. Servlet（Tomcat）动画演示：https://i-blog.csdnimg.cn/blog_migrate/5e324f5a5cc107dd908716b7dc7a8d6e.gif
2. 总结即浏览器Http由tomcat接收后，由Tomcat加载servlet，servlet将http请求解析为request对象，之后tomcat传递给servlet进行业务处理为response对象，Tomcat再将response对象转换为http响应
3. Servlet接口定义了Servlet与servlet容器（如Tomcat）之间的契约：Servlet容器将Servlet类载入内存，并产生Servlet实例和调用它具体的方法。但是要注意的是，在一个应用程序中，每种Servlet类型只能有一个实例
4. Servlet容器调用Servlet的Service（）方法，并传入一个ServletRequest对象和一个ServletResponse对象。ServletRequest对象和ServletResponse对象都是由Servlet容器（例如TomCat）封装好的，并不需要程序员去实现，程序员可以直接使用这两个对象
5. 对于每一个应用程序，Servlet容器还会创建一个ServletContext对象。这个对象中封装了上下文（应用程序）的环境详情。每个应用程序只有一个ServletContext。每个Servlet对象也都有一个封装Servlet配置的ServletConfig对象。
6. Servlet生命周期：
   - init 
     - 当Servlet第一次被请求时，Servlet容器就会开始调用这个方法来初始化一个Servlet对象出来，但是这个方法在后续请求中不会在被Servlet容器调用
     - 调用这个方法时，Servlet容器会传入一个ServletConfig对象进来从而对Servlet对象进行初始化
   - service
     - 每当请求Servlet时，Servlet容器就会调用这个方法
   - destroy
7. 每个应用程序会有一个ServletContext对象，就可以共享从应用程序中的所有资料处访问到的信息，并且可以动态注册Web对象。前者将对象保存在ServletContext中的一个内部Map中，通过setAttribute(String var1, Object var2)、Object getAttribute(String var1)等处理属性

***

1. GenericServlet抽象类的出现很好的解决了这个问题。本着尽可能使代码简洁的原则，GenericServlet实现了Servlet和ServletConfig接口
2. 为Servlet接口中的所有方法提供了默认的实现，则程序员需要什么就直接改什么，不再需要把所有的方法都自己实现了

***

1. 为Servlet接口中的所有方法提供了默认的实现，则程序员需要什么就直接改什么，不再需要把所有的方法都自己实现了

2. ![img_15.png](D:/JavaCode/Idea_Projects/Learn_Java/img_15.png)

3. xml中需要配置好Servlet的映射关系和servlet对象的创建

   ```xml
   </servlet-mapping>
       <servlet>
           <servlet-name>FormServlet</servlet-name>
           <servlet-class>MyServlet</servlet-class>
       </servlet>
       <servlet-mapping>
           <servlet-name>FormServlet</servlet-name>
           <url-pattern>/form</url-pattern>
       </servlet-mapping>
   ```

   业务逻辑：通过编写HttpServlet的继承类(上述中的MyServlet)，重写其中的doGet(rq,rs),doPost(.)...等

4. Servlet需要使用response.getWriter()来获取writer，使用writer.write来一行行的写HTML语句。所以JSP出现

***

1. ServletContextListener（Servlet全局监听器）：监听ServletContext  

2. 当应用启动时，ServletContext进行初始化，然后Servlet容器会自动调用正在监听ServletContext的ServletContextListener的void contextInitialized(ServletContextEvent var1)方法，并向其传入一个ServletContextEvent对象。当应用停止时，ServletContext被销毁，此时Servlet容器也会自动地调用正在监听ServletContext的ServletContextListener的void contextDestroyed(ServletContextEvent var1)方法

3. 手写类实现ServletContextListener接口，然后在xml中配置：

   ```java
   import javax.servlet.ServletContextEvent;
   import javax.servlet.ServletContextListener;
   public class MyListener implements ServletContextListener {
       @Override
       public void contextInitialized(ServletContextEvent servletContextEvent) {
           System.out.println("ServletContextListener.contextInitialized方法被调用");
       }
       @Override
       public void contextDestroyed(ServletContextEvent servletContextEvent) {
           System.out.println("ServletContextListener.contextDestroyed方法被调用");
       }
   }
   
   ```

   ```xml
   <listener>
       <listener-class>MyListener</listener-class>
   </listener>
   ```

***

Spring容器是如何借用ServletContextListener这个接口来实例化

```xml
<listener>

    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>

</listener>

<context-param>

    <param-name>contextConfigLocation</param-name>

    <param-value>

        classpath:applicationContext.xml
    </param-value>

</context-param>

```

关键org.springframework.web.context.ContextLoaderListener代码：

```java
public void contextInitialized(ServletContextEvent event) {
    this.initWebApplicationContext(event.getServletContext());
}
```

1. 这个类实现了ServletContextListener接口中的两个方法  
   - 当ServletContext初始化后 contextInitialized(ServletContextEvent event)方法被调用，接下来执行initWebApplicationContext(event.getServletContext())方法:  
   - WebApplicationContext initWebApplicationContext(ServletContext servletContext)
   - 返回一个WebApplicationContext
2. 当Servlet容器启动时，ServletContext对象被初始化，然后Servlet容器调用web.xml中注册的监听器的
   public void contextInitialized(ServletContextEvent event)
   方法，而在监听器中，调用了this.initWebApplicationContext(event.getServletContext())方法，在这个方法中实例化了Spring IOC容器。即ApplicationContext对象。
   因此，当ServletContext创建时我们可以创建applicationContext对象，当ServletContext销毁时，我们可以销毁applicationContext对象。这样applicationContext就和ServletContext“共生死了”。

***

使用Servlet开发web程序流程：
![img_16.png](D:/JavaCode/Idea_Projects/Learn_Java/img_16.png)
使用SpringMVC开发web流程：
![img_17.png](D:/JavaCode/Idea_Projects/Learn_Java/img_17.png)

### SpringMVC

1. MVC架构模式：controller（调度）、model（数据和业务处理）、view（展示）
2. 三层模型：表现层（对应经典mvc模式中的view+controller）、业务层（对应经典mvc模式中的model层一部分）、持久层（对应经典mvc模式中的model层一部分）
3. SpringMVC是基于Spring的，是Spring中的一个模块，专门用来做web开发使用的
4. SpringMVC 也叫 Spring web mvc。是 Spring 框架的一部分，是在Spring3.0 后发布的。基于 MVC 架构，功能分工明确、解耦合
5. SpringMVC也是一个容器，使用IoC核心技术，管理界面层中的控制器对象。SpringMVC的底层就是servlet，以servlet为核心，接收请求、处理请求，显示处理结果给用户。在此之前这个功能是由Servlet来实现的，现在使用SpringMVC来代替Servlet行驶控制器的角色和功能。 其核心Servlet是：DispatcherServlet
6. 请求响应模式的演进阶段1：原本三层模式一个Servlet处理一个请求，web-service-dao
7. 请求响应模式的演进阶段2：mvc模式，Servlet拆分为controller、service、dao，组织出model，页面view(jsp)，一个Servlet可以处理多个请求
8. 请求响应模式的演进阶段3：异步调用形式：view不再是jsp，而是html、css、vue、elementUI；页面异步调用后端服务器的controller-service-dao，存储数据依然是model（java的对象）  
   java对象在页面与后端服务器之间的传递通过json完成（完成java程序与前端页面交互）
9. SpringMVC主要做上面过程中的controller和转换json
10. SpringMVC为我们做了什么？
    - 入口控制：SpringMVC框架通过DispatcherServlet作为入口控制器，负责接收请求和分发请求。而在Servlet开发中，需要自己编写Servlet程序，并在web.xml中进行配置，才能接受和处理请求
    - 在SpringMVC中，表单提交时可以自动将表单数据绑定到相应的JavaBean对象中，只需要在控制器方法的参数列表中声明该JavaBean对象即可，无需手动获取和赋值表单数据。而在纯粹的Servlet开发中，这些都是需要自己手动完成的
    - IoC容器：SpringMVC框架通过IoC容器管理对象，只需要在配置文件中进行相应的配置即可获取实例对象，而在Servlet开发中需要手动创建对象实例
    - 统一处理请求：SpringMVC框架提供了拦截器、异常处理器等统一处理请求的机制，并且可以灵活地配置这些处理器。而在Servlet开发中，需要自行编写过滤器、异常处理器等，增加了代码的复杂度和开发难度
    - 视图解析：SpringMVC框架提供了多种视图模板，如JSP、Freemarker、Velocity等，并且支持国际化、主题等特性。而在Servlet开发中需要手动处理视图层，增加了代码的复杂度

### SpringMVC 在IDEA中新建

1. 在项目结构中，添加web支持，6.0
   ![img_18.png](D:/JavaCode/Idea_Projects/Learn_Java/img_18.png)
   https://www.yuque.com/dujubin/java/myxi54xu063hgsl4
   这个文档中由新建springmvc项目的步骤，注意版本 jdk21

2. 原本Servlet需要在web.xml中添加servlet对象和mapping，现在只需要web.xml配置一个spring的前端控制器DispatcherServlet（springmvc框架中最核心的类）

   - 接收客户端的HTTP请求：DispatcherServlet监听来自Web浏览器的HTTP请求，然后根据请求的URL将请求数据解析为Request对象。
   - 处理请求的URL：DispatcherServlet将请求的URL（Uniform Resource Locator）与处理程序进行匹配，确定要调用哪个控制器（Controller）来处理此请求。
   - 调用相应的控制器：DispatcherServlet将请求发送给找到的控制器处理，控制器将执行业务逻辑，然后返回一个模型对象（Model）。 
   - 渲染视图：DispatcherServlet将调用视图引擎，将模型对象呈现为用户可以查看的HTML页面。 
   - 返回响应给客户端：DispatcherServlet将为用户生成的响应发送回浏览器，响应可以包括表单、JSON、XML、HTML以及其它类型的数据

3. 创建Controller类，使用@Controller注解标注

4. WEB-INF目录下，应该由springmvc-servlet.xml 文件,即SpringMVC框架有它自己的配置文件

   - 配置组件扫描
   - 配置视图解析器 
     - View是一个单独组件，负责展示，可以灵活配置不同的视图解析器 如jsp、freemarker等等

5. 编写模板解析器里要解析的文件，比如我们在springmvc-servlet.xml中定义模板文件前缀：WEB-INF/templates，模板后缀：.thymeleaf。那我们就写一个first.thymeleaf文件放在web-inf/templates中，用于解析器寻找解析并渲染

6. 继续编写Controller类中的方法（请求映射由@RequestMapping提供），该方法名称随意，其返回值代表的是一个逻辑视图名称

   ```java
   @Controller
   public class FirstController {
   
       @RequestMapping("/test")
       public String hello() {
           return "first"; //逻辑视图名称
   
       }
   }
   ```

   其中，return返回的逻辑视图名称，会被视图解析器添加前缀和后缀：WEB-INF/templates/first.thymeleaf。

7. 视图解析器根据组合后的物理视图名称，对其进行解析

8. Tomcat10 配置：默认即可，部署中加入要部署的exploded文件，应用程序上下文即localhost:8080/“应用程序上下文”/“mapping路径”
   `0925知识点，如何配置tomcat`

9. 测试结果：http://localhost:8080/springmvc/test 跳转后被DispatcherServlet接收，后mapping到控制器，返回first逻辑视图，被解析器加上前后缀并解析模板文件为html格式，返回给浏览器
   ![img_19.png](D:/JavaCode/Idea_Projects/Learn_Java/img_19.png)

10. ![img_21.png](D:/JavaCode/Idea_Projects/Learn_Java/img_21.png)

***

1. web.xml 前端控制器可以更改初始化参数，完成指定springmvc配置文件名字和位置（默认WEB-INF/springmvc-servlet.xml）

   ```xml
   <init-param>
       <param-name>contextConfigLocation</param-name>
       <param-value>classpath:springmvc.xml</param-value>
   </init-param
   ```

    `0925知识点：类路径包括main/java和main/resources，所以可以将springmvc.xml放到resources目录下`

2. 建议在前端控制器设置：在web服务器启动时就初始化前端控制器，提升用户第一次访问效率

   ```xml
   <!--为了提高用户的第一次访问效率，建议在web服务器启动时初始化前端控制器-->
   <load-on-startup>1</load-on-startup>
   ```

### RequestMapping 及Restful风格 @PathVariable

1. 普通的请求路径：http://localhost:8080/springmvc/login?username=admin&password=123&age=20  
   RESTful风格的请求路径：http://localhost:8080/springmvc/login/admin/123/20

2. 数据直接与URL放一起，数据使用占位符{}进行获取：
   ![img_20.png](D:/JavaCode/Idea_Projects/Learn_Java/img_20.png)

3. 使用@PathVariable注解：在形参上加入注解，注解的属性为占位符里的属性名

4. @RequestMapping的属性：

   - value mapping的url
   - method 前端的请求方式。果前端发送请求的方式和后端的处理方式不一致时，会出现405错误
     - 通过RequestMapping源码可以看到，method属性也是一个数组，每个元素是 RequestMethod，而RequestMethod是一个枚举类型的数据
     - 所以，使用method属性，需要@RequestMapping(value = "/login", method = RequestMethod.POST)，即枚举类型使用
   - 因此，可以看出，对于RequestMapping注解来说，多一个属性，就相当于多了一个映射的条件，如果value和method属性都有，则表示只有前端发送的请求路径 + 请求方式都满足时才能与控制器上的方法建立映射关系，只要有一个不满足，则无法建立映射关系。例如：@RequestMapping(value="/login", method = RequestMethod.POST) 表示当前端发送的请求路径是 /login，并且发送请求的方式是POST的时候才会建立映射关系。如果前端发送的是get请求，或者前端发送的请求路径不是 /login，则都是无法建立映射的。
   - params属性
     - params属性也是一个数组，不过要求请求参数必须和params数组中要求的所有参数完全一致后，才能映射成功
     - @RequestMapping(value="/login", params={"username", "password"})
     - @RequestMapping(value="/login", params={"username=admin", "password"})
   - headers属性
     - headers和params原理相同，用法也相同
     - @RequestMapping(value="/login", headers={"Referer", "Host"})
     - @RequestMapping(value="/login", headers={"Referer=http://localhost:8080/springmvc/", "Host"})

5. 衍生Mapping 不需要再额外指定method属性：

   - PostMapping
   - GetMapping
   - PutMapping
   - DeleteMapping
   - PatchMapping

    ```java
   @PostMapping("/login")
   public String testMethod(){
       return "testMethod";
   }
    ```

6. 使用超链接以及原生的form表单只能提交get和post请求，put、delete、head请求可以使用发送ajax请求的方式来实现，如果form指定为其他的则会改为get方式 

7. 做文件上传，一定是post请求

8. params属性测试时，thymeleaf中这样写：  
   <!--测试RequestMapping的params属性-->  
   <a th:href="@{/testParams(username='admin',password='123')}">测试params属性</a>

### 获取请求参数

创建子模块spring_004，创建一个注册页面

1. 使用原生Servlet API进行请求参数获取

   - 由web容器（tomcat）负责创建request对象、response对象、session对象等

   - Springmvc怎么获取这些对象

     - 在SpringMVC当中，一个Controller类中的方法参数上如果有HttpServletRequest，SpringMVC会自动将当前请求对象传递给这个参数

     - ```java
       @PostMapping(value="/register")
       public String register(HttpServletRequest request){
       // 通过当前请求对象获取提交的数据
           String username = request.getParameter("username");
           String password = request.getParameter("password");
           String sex = request.getParameter("sex");
           String[] hobbies = request.getParameterValues("hobby");
           String intro = request.getParameter("intro");
           System.out.println(username + "," + password + "," + sex + "," + Arrays.toString(hobbies) + "," + intro);
           return "success";
       }
       ```

        `0926问题：一定要注意每次写跳转链接时候都要加上/`

   - 但是这种方法junit单元测试不方便，因为只有tomcat可以创建request对象等，离不开tomcat

2. 使用RequestParam：将请求参数与方法上的形参映射

   - @RequestParm(value或者name="请求参数名") String 形参名
   - required属性：默认情况下，这个参数为 true，表示方法参数是必需的。如果请求中缺少对应的参数，则会抛出异常
   - defaultValue属性：默认值

3. 使用controller方法上的形参名来接收

   - @RequestParam 这个注解是可以省略的，如果方法形参的名字和提交数据时的name相同，则 @RequestParam 可以省略

   - 如果你采用的是Spring6+版本，你需要在pom.xml文件中指定编译参数'-parameter'

   - ```xml
     <build>
       <plugins>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-compiler-plugin</artifactId>
               <version>3.12.1</version>
               <configuration>
                   <source>21</source>
                   <target>21</target>
                   <compilerArgs>
                       <arg>-parameters</arg>
                   </compilerArgs>
               </configuration>
           </plugin>
       </plugins>
     </build>
     ```

   - 形参的名字必须和提交的数据的name一致！！！！！

   - 原理：当你使用 -parameter 选项编译 Java 代码时，编译器会记录源代码中的方法和构造函数参数名称。
     这些名称被存储在生成的 .class 文件的常量池中，因为它们是字符串常量。
     在运行时，Java 反射 API 可以访问这些常量池中的参数名信息，从而提供更丰富的反射功能  
     可以看到，当参数很多时，上面的方法需要一次次的写形参，因此使用POJO类/JavaBean接收请求参数

4. POJO类/JavaBean方式

   - 类的属性名(其实主要是setter方法)必须和请求参数的参数名保持一致

   - 参数解析底层机制：

     - https://blog.csdn.net/qq_26664043/article/details/140088884?fromshare=blogdetail&sharetype=blogdetail&sharerId=140088884&sharerefer=PC&sharesource=weixin_46391939&sharefrom=from_link

     - ![img_21.png](D:/JavaCode/Idea_Projects/Learn_Java/img_21.png)

       当 Spring MVC 接收到一个 HTTP 请求*并确定要调用*的控制器方法后，它会按照以下步骤使用 HandlerMethodArgumentResolver 来解析方法的参数

       - 确定解析器：Spring MVC 会遍历所有已注册的 HandlerMethodArgumentResolver 实现，并调用每个解析器的 supportsParameter 方法来检查是否有解析器支持当前方法的参数。一旦找到支持的解析器，就会使用该解析器来解析参数
       - 解析参数：一旦确定了合适的解析器，Spring MVC 就会调用该解析器的 resolveArgument 方法来实际解析请求中的数据。这个过程可能涉及从请求头、请求体、路径变量、查询参数等不同来源提取数据，并将其转换为方法参数所需的类型

     - Spring MVC 提供了多种内置的 HandlerMethodArgumentResolver 实现，用于处理不同类型的请求参数

       - ServletRequestParamMethodArgumentResolver：解析请求参数中的查询参数
       - PathVariableMethodArgumentResolver：解析请求参数中的路径变量
       - RequestHeaderMethodArgumentResolver：解析请求头中的参数
       - ...

     - 除了内置解析器外，Spring MVC 还允许开发者自定义 HandlerMethodArgumentResolver 实现类，以处理特殊的参数类型或实现自定义的解析逻辑。自定义解析器需要实现 HandlerMethodArgumentResolver 接口，并覆盖 supportsParameter 和 resolveArgument 方法。然后，通过注册自定义解析器到 Spring MVC 的配置中，使其能够参与到参数解析的过程中

5. @RequestHeader注解 将请求头信息映射到方法的形参上

   - 和@RequestParam类似  有value、required、defaultValue属性

6. @CookieValue注解 将请求提交的Cookie数据映射到方法的形参上

   - 在html中写Cookie

   ```html
   <script type="text/javascript">
       function sendCookie(){
           document.cookie = "id=123456789; expires=Thu, 18 Dec 2025 12:00:00 UTC; path=/";
           document.location = "/springmvc/register";
       }
   </script>
   <button onclick="sendCookie()">向服务器端发送Cookie</button>
   ```

   - 在Mapping形参中使用Cookie数据

    ```java
   @GetMapping("/register")
   public String register(User user,
                          @RequestHeader(value="Referer", required = false, defaultValue = "")
                          String referer,
                          @CookieValue(value="id", required = false, defaultValue = "2222222222")
                          String id){
       System.out.println(user);
       System.out.println(referer);
       System.out.println(id);
       return "success";
   }
    ```

### 域对象

1. 分为request、session、application三个

2. request对象代表了一次请求。一次请求一个request

3. session对象代表了一次会话。从打开浏览器开始访问，到最终浏览器关闭，这是一次完整的会话。每个会话session对象都对应一个JSESSIONID，而JSESSIONID生成后以cookie的方式存储在浏览器客户端。浏览器关闭，JSESSIONID失效，会话结束。

   - 在A资源中通过重定向的方式跳转到B资源，因为是重定向，因此从A到B是两次请求，如果想让A资源和B资源共享同一个数据，可以将数据存储到session域中。
   - 登录成功后保存用户的登录状态

4. application对象代表了整个web应用，服务器启动时创建，服务器关闭时销毁。对于一个web应用来说，application对象只有一个。

   - 使用应用域的业务场景：记录网站的在线人数

5. 注意：在thymeleaf的html中，使用属性用<div th:text="${testRequestScope}">，而超链接<a th:href="@{/testModel}">  

6. request域对象的使用

   - 原生ServletAPI

     - 直接使用HttpServletRequest对象做形参 

     - ```java
       @RequestMapping("/testServletAPI")
       public String testServletAPI(HttpServletRequest request){
           // 向request域中存储数据
           request.setAttribute("testRequestScope", "在SpringMVC中使用原生Servlet API实现request域数据共享");
           return "view";
       }
       ```

     - html页面这样写：<div th:text="${testRequestScope}"></div>

   - Model接口

     - 使用Model model做Mapping控制器方法的形参
     - model.addAttribute()   注意是add
     - MVC模式中的Model模型数据，

   - Map接口

     - Map接口用法同Model，使用一个Map<String,Object> map做Mapping控制器方法的形参
     - map.put()

   - ModelMap类

     - 同上面两个
     - model.addAttribute()

   - *无论是Model、Map还是ModelMap，底层实例化的对象都是：BindingAwareModelMap。

     - BindingAwareModelMap继承了ModelMap，而ModelMap又实现了Map接口
     - ![img_22.png](D:/JavaCode/Idea_Projects/Learn_Java/img_22.png)

   - ModelAndView类

     - 是SpringMVC为了更好体现MVC框架而提供的，既封装了Model，也封装了View（即既封装了业务逻辑处理后的数据模型，以体现了跳转的视图）

     - 这种方式需要注意对原本Mapping控制器方法的不同点

       - 返回值不再是String（逻辑视图的名），而是ModelAndView对象
       - 无形参，并且ModelAndView实在方法中new的！！
       - 调用addObject向域中存储数据
       - 调用setViewName设置视图名字

      - ```java
        @RequestMapping("/testModelAndView")
            public ModelAndView testModelAndView(){
            // 创建“模型与视图对象”
            ModelAndView modelAndView = new ModelAndView();
            // 绑定数据
            modelAndView.addObject("testRequestScope", "在SpringMVC中使用ModelAndView实现request域数据共享");
            // 绑定视图
            modelAndView.setViewName("view");
            // 返回
            return modelAndView;
            }
        ```

   - 无论是Model、Map、ModelMap还是ModelAndView，每个控制器方法一定返回的是ModelAndView(即使是String逻辑视图名，也会被封装)，将View和Model结合为ModelAndView后，返回给DispatcherServlet

     - Dispatcher源码中的doDispatch方法按照mapping路径，来决定调用哪个控制器，返回值就是ModelAndView对象：
     - ![img_23.png](D:/JavaCode/Idea_Projects/Learn_Java/img_23.png)
     - ![img_24.png](D:/JavaCode/Idea_Projects/Learn_Java/img_24.png)  

7. Session使用

   - 在html中得到session数据：```html <div th:text="${session.testSessionScope1}"></div>    ```

   - 使用SessionAttributes注解

     - SessionAttributes的属性是 value = {"seeion数据的键1", "seeion数据的键2"}

     - 向ModelMap中添加属性（使用键值对）

     - ```java
       @Controller
       @SessionAttributes(value = {"x", "y"})
       public class SessionScopeTestController {
       
           @RequestMapping("/testSessionScope2")
           public String testSessionAttributes(ModelMap modelMap){
               // 向session域中存储数据
               modelMap.addAttribute("x", "我是埃克斯");
               modelMap.addAttribute("y", "我是歪");
       
               return "view";
           }
       }
       
       ```

8. application域对象

   - 都是使用Servlet API

   - ```java
     @Controller
     public class ApplicationScopeTestController {
     
         @RequestMapping("/testApplicationScope")
         public String testApplicationScope(HttpServletRequest request){
             
             // 获取ServletContext对象
             ServletContext application = request.getServletContext();
     
             // 向应用域中存储数据
             application.setAttribute("applicationScope", "我是应用域当中的一条数据");
     
             return "view";
         }
     }
     ```

   - 先获取ServletContext对象 这个对象就是application域对象



## day1002 

Spring MVC中，视图View是支持定制。视图View和框架是解耦合的，耦合度低扩展能力强。视图View可以通过配置文件进行灵活切换。

实现视图的核心类与接口：

1. ViewResolver接口（视图解析器）：负责将`逻辑视图名`转换为`物理视图名`，最终创建View接口的实现类，即视图实现类对象 ，核心方法为resolveViewName：![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1710824983130-13d175e9-be25-4e76-bccf-d50f63cee853.png?x-oss-process=image%2Fformat%2Cwebp)

2. View接口（视图）：负责将模型数据Model渲染为视图格式（HTML代码），并最终将生成的视图（HTML代码）输出到客户端。（它负责将模板语言转换成HTML代码），核心方法render：![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1710825045618-8ca7d10a-9f8f-4210-a871-8b7d34885311.png?x-oss-process=image%2Fformat%2Cwebp)

3. ViewResolverRegistry（视图解析器注册器）：负责在web容器（Tomcat）启动的时候，完成视图解析器的注册。如果有多个视图解析器，会将视图解析器对象按照order的配置放入List集合

4. 如何实现自己的视图组件：

   - 编写实现类ViewResolver接口，实现resolveViewName方法。在该方法中完成`逻辑视图名`到`物理视图名`的转换，并返回View对象
   - 编写类实现View接口，实现render方法，将模板语言转换成HTML代码，并把HTML代码响应到浏览器
   - Thymeleaf相关类：ThymeleafView、ThymeleafViewResolver

5. Controller的方法处理业务并返回一个`逻辑视图名`给DispatcherServlet

   DispatcherServlet调用ThymeleafViewResolver的resolveViewName方法，将`逻辑视图名`转换为`物理视图名`，并创建ThymeleafView对象返回给DispatcherServlet

   DispatcherServlet再调用ThymeleafView的render方法，render方法将模板语言转换为HTML代码，响应给浏览器，完成最终的渲染。

### SpringMVC 转发和重定向

#### 转发

在Spring MVC中默认是转发的方式，我们之前所写的程序，都是转发的方式。只不过都是转发到Thymeleaf的模板文件xxx.html上  

那么，在Spring MVC中如何转发到另一个Controller上呢？可以使用Spring MVC的`forward`

```java
package com.powernode.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/a")
    public String toA(){
        return "forward:/b";
    }

    @RequestMapping("/b")
    public String toB(){
        return "b";
    }
}
```

测试结果：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1710839187256-3c823090-ff26-4d46-8dca-d7727e800da9.png?x-oss-process=image%2Fformat%2Cwebp)



当return "a"时，默认是forward转发，底层创建的视图对象是ThymeleafView

当return “forward:/a”时，底层创建的视图对象是InternalResourceView对象

当return "redirect:/b"时，底层创建的视图对象是RedirectView对象

#### 重定向 --使用较多

控制器最后返回return “redirect:/b”，测试结果：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1710857817456-baf96179-4ce2-4897-8873-aa1232ed8462.png?x-oss-process=image%2Fformat%2Cwebp)

重定向可以跨域，需要加上全路径

使用场景：保存新的用户信息后，采用重定向来显示用户列表。

```java
@PostMapping("/user")
public String save(User user){
    // 保存用户
    userDao.save(user);
    // 重定向到列表
    return "redirect:/user";
}
```



### mvc:view-controller

`<mvc:view-controller>` 配置用于将某个请求映射到特定的视图上，即指定某一个 URL 请求到一个视图资源的映射，使得这个视图资源可以被访问

它相当于是一个独立的处理程序，不需要编写任何 Controller，只需要指定 URL 和对应的视图名称就可以了

一般情况下，`<mvc:view-controller>` 配置可以替代一些没有业务逻辑的 Controller，例如首页、错误页面等。当用户访问配置的 URL 时，框架将直接匹配到对应的视图，而无需再经过其他控制器的处理

```xml
<mvc:view-controller path="/如何访问该页面" view-name="对应的逻辑视图名称" />
```

比如：

```xml
<mvc:view-controller path="/" view-name="index" />
```

注意，如果在springmvc.xml文件中配置了 `<mvc:view-controller>`，就需要同时在springmvc.xml文件中添加如下配置：

```xml
<mvc:annotation-driven/>
```

启用Spring MVC的注解。如果没有以上的配置，Controller就无法访问到。访问之前的Controller会发生 404 问题

### 访问静态资源

原本使用全路径可以访问到静态资源，但是由于DispatcherServlet默认mapping所有"/"，所以无法正常访问静态资源

1. Tomcat服务器自带的DefaultServlet可以处理静态资源：开启默认Servlet处理：

```xml
<!-- 开启注解驱动 -->
<mvc:annotation-driven />

<!--开启默认Servlet处理-->
<mvc:default-servlet-handler>
```

​	这样**同一个请求路径，先走DispatcherServlet，如果找不到则走默认的Servlet**

2. 使用mvc:resources标签配置静态资源

   ```xml
   <!-- 开启注解驱动 -->
   <mvc:annotation-driven />
   
   <!-- 配置静态资源处理 -->
   <mvc:resources mapping="/static/**" location="/static/" />
   ```

   这样以后，所有的"/static/**"路径的资源都会到"/static/"目录下找静态资源

### RESTFul编程风格

RESTFul是`WEB服务接口`的一种设计风格
web服务接口举个例子：/springmvc/user/detail?id=1

RESTFul定义了一组约束条件和规范，可以让`WEB服务接口`更加简洁、易于理解、易于扩展、安全可靠：

- 对请求的URL格式有约束和规范
- 对HTTP的请求方式有约束和规范
- 对请求和响应的数据格式有约束和规范
- 对HTTP状态码有约束和规范

RESTFul对请求方式的约束：

- 查询必须发送GET请求
- 新增必须发送POST请求
- 修改必须发送PUT请求
- 删除必须发送DELETE请求

对比：

- 传统的URL：get请求，/springmvc/getUserById?id=1
- REST风格的URL：**get请求**，/springmvc/user/1

- 传统的URL：get请求，/springmvc/deleteUserById?id=1
- REST风格的URL：**delete请求**, /springmvc/user/1

两个请求路径一样，只是请求方式不同，完成了不同的操作

请求方式在Controller中注解的method属性标注：如method=RequestMethod.GET或者.DELETE

| **传统的 URL**           | **RESTful URL** |
| ------------------------ | --------------- |
| GET /getUserById?id=1    | GET /user/1     |
| GET /getAllUser          | GET /user       |
| POST /addUser            | POST /user      |
| POST /modifyUser         | PUT /user       |
| GET /deleteUserById?id=1 | DELETE /user/1  |

Controller方法：

```java
@Controller
public class UserController {

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public String getById(@PathVariable("id") Integer id){
        System.out.println("根据用户id查询用户信息，用户id是" + id);
        return "ok";
    }

}
```

#### 修改请求 put/delete请求

前面的查询直接给链接即可，新增post则使用表单method="post"

由于表单中method=“put”和“delete”都会默认改为get，所以怎么发出put和delete请求呢？隐藏域

1. 在原本post表单 method还是post

2. 在post表单中添加隐藏于：

   ```html
   <input type="hidden" name="_method" value="put">
   ```

   注意：name必须是_method，value必须是put/PUT/delete/DELETE

3. 在web.xml中添加一个过滤器，springmvc已经提前封装好，直接用。可以将POST请求转换为PUT/DELETE请求:

   ```XML
   <!--隐藏的HTTP请求方式过滤器-->
   <filter>
       <filter-name>hiddenHttpMethodFilter</filter-name>
       <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
   </filter>
   <filter-mapping>
       <filter-name>hiddenHttpMethodFilter</filter-name>
       <url-pattern>/*</url-pattern>
   </filter-mapping>
   ```

原理：先走过滤器，doFilterInternal方法：如果是POST请求，先获取"_method"参数，判空之后进行封装，最后filterChain.doFilter。若不是POST请求或者" _method"参数为空，则直接filterChain.doFilter。也就是前端只要写，过滤器就能转换为底层能够识别的PUT等请求。

注意，在同一个xml中的过滤器，越靠上优先级越高，因此如果配置HiddenHiddenMethodFilter和字符编码过滤器，必须按照先Hidden后字符顺序，否则字符编码不生效。字符编码过滤器执行之前不能调用 request.getParameter方法，如果提前调用了，乱码问题就无法解决了。因为request.setCharacterEncoding()方法的执行必须在所有request.getParameter()方法之前执行。因此这两个过滤器就有先后顺序的要求，在web.xml文件中，应该先配置CharacterEncodingFilter，然后再配置HiddenHttpMethodFilter。

## day1003

### HttpMessageConverter

HttpMessageConverter是Spring MVC中非常重要的一个接口。翻译为：HTTP消息转换器。该接口下提供了很多实现类，不同的实现类有不同的转换方式。

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711000445139-8bc9f74d-6ec3-4942-8063-5a130eac64eb.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_640%2Climit_0)

HTTP协议包括请求协议和响应协议

HTTP消息转换器转换的是：`HTTP协议（字符串）`与`Java程序中的对象`之间的互相转换：

![无标题.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711002146899-deaef9c8-a3b7-425e-97b1-6ada5477c674.png?x-oss-process=image%2Fformat%2Cwebp)

请求体中的数据是如何转换成user对象的，底层实际上使用了`HttpMessageConverter`接口的其中一个实现类`FormHttpMessageConverter`



Controller返回值看做逻辑视图名称，视图解析器将其转换成物理视图名称，生成视图对象，`StringHttpMessageConverter`负责将视图对象中的HTML字符串写入到HTTP协议的响应体中。最终完成响应。

![无标题.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711003362257-f736f7c8-4d55-4e3f-b8f8-cfbab97c21f4.png?x-oss-process=image%2Fformat%2Cwebp)

![无标题.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711003929875-072161b4-af27-4855-9980-5d8ba186730b.png?x-oss-process=image%2Fformat%2Cwebp)

**通过SpringMVC为我们提供的注解，我们通过使用不同的注解来启用不同的消息转换器**

在HTTP消息转换器这一小节，我们重点要掌握的是两个注解两个类：

- @ResponseBody
- @RequestBody
- ResponseEntity
- RequestEntity

学之前需要先学习AJAX请求

### SpringMVC的AJAX异步请求

在thymeleaf中：

引入vue3和axios的js文件

vue代码中写data：message，方法getMessage()，将异步请求结果写入结果。

[[@{/}]]表示thymeleaf动态获取根路径/

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>首页</title>
    <script th:src="@{/static/js/vue3.4.21.js}"></script>
    <script th:src="@{/static/js/axios.min.js}"></script>
</head>
<body>
<h1>首页</h1>
<hr>

<div id="app">
    <h1>{{message}}</h1>
    <button @click="getMessage">获取消息</button>
</div>

<script th:inline="javascript">
    Vue.createApp({
        data(){
            return {
                message : "这里的信息将被刷新"
            }
        },
        methods:{
            async getMessage(){
                try {
                    const response = await axios.get([[@{/}]] + 'hello')
                    this.message = response.data
                }catch (e) {
                    console.error(e)
                }
            }
        }
    }).mount("#app")
</script>

</body>
</html>
```

**之前我们都是传统的请求，Controller返回一个**`**逻辑视图名**`**，然后交给**`**视图解析器**`**解析。最后跳转页面。**

**而AJAX请求是不需要跳转页面的，因为AJAX是页面局部刷新，以前我们在Servlet中使用**`**response.getWriter().print("message")**`**的方式响应。**

**在Spring MVC中怎么办呢？当然，我们在Spring MVC中也可以使用Servlet原生API来完成这个功能，代码如下**:

```java
@RequestMapping(value = "/hello")
    public String hello(HttpServletResponse response) throws IOException {
        response.getWriter().print("hello");
        return null;
    }
//或者void类型 无返回值
```



在这个例子中，我们要向前端响应一个字符串"hello"，这个"hello"就是响应协议中的响应体，我们可以使用 @ResponseBody 注解来启用对应的消息转换器。而这种消息转换器只负责将Controller返回的信息以响应体的形式写入响应协议。

### @ResponseBody

我们不使用Servlet原生API，而是使用消息转换器的注解

```java
	@RequestMapping(value = "/hello")
    @ResponseBody
    public String hello(){
        // 由于你使用了 @ResponseBody 注解
        // 以下的return语句返回的字符串则不再是“逻辑视图名”了
        // 而是作为响应协议的响应体进行响应。
        return "hello";
    }
```



通常AJAX请求需要服务器给返回一段JSON格式的字符串，可以使用这样的返回格式：

```java
	@RequestMapping(value = "/hello")
    @ResponseBody
    public String hello(){
        return "{\"username\":\"zhangsan\",\"password\":\"1234\"}";
    }
```

上面底层使用的是StringHttpMessageConverter

如果在程序中是一个POJO对象，怎么将POJO对象以JSON格式的字符串响应给浏览器呢？两种方式：

- 第一种方式：自己写代码将POJO对象转换成JSON格式的字符串，用上面的方式直接return即可。
- 第二种方式：启用`MappingJackson2HttpMessageConverter`消息转换器

#### MappingJackson2HttpMessageConverter

使用此转换器步骤：

1. 在pom中引入依赖

   ```xml
   <dependency>
     <groupId>com.fasterxml.jackson.core</groupId>
     <artifactId>jackson-databind</artifactId>
     <version>2.17.0</version>
   </dependency>
   ```

2. 开启注解驱动

   ```xml
   <mvc:annotation-driven/>
   ```

   开启后会在HandlerAdapter中自动装配一个消息转换器：MappingJackson2HttpMessageConverter

3. 准备POJO类，比如User，为其实现构造方法和setter和getter

4. 在控制器上标注@ResponseBody注解

   ```java
       @RequestMapping(value = "/hello")
       @ResponseBody
       public User hello(){
           User user = new User("zhangsan", "22222");
           return user;
       }
   ```

   测试：

   ![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711014082618-8a46beab-d498-4d67-abad-662e07d5871f.png?x-oss-process=image%2Fformat%2Cwebp)

   代码底层启用的就是 MappingJackson2HttpMessageConverter 消息转换器。

   他的功能很强大，可以将POJO对象转换成JSON格式的字符串，响应给前端。

   其实这个消息转换器`MappingJackson2HttpMessageConverter`本质上只是比`StringHttpMessageConverter`多了一个json字符串的转换，其他的还是一样

### @RestController

为了方便，Spring MVC中提供了一个注解 @RestController。这一个注解代表了：@Controller + @ResponseBody。

@RestController 标注在**类上**即可。被它标注的Controller中**所有的方法**上都会自动标注 @ResponseBody

```java
@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public User hello(){
        User user = new User("zhangsan", "22222");
        return user;
    }
}
```

### @RequestBody

**只能用在方法参数上**

这个注解的作用是直接将请求体传递给Java程序，在Java程序中可以直接使用一个String类型的变量接收这个请求体的内容。

当没有使用此注解时，对于控制器方法参数是一个对象时，当请求体提交数据时，Spring MVC会自动使用 `FormHttpMessageConverter`消息转换器，将请求体转换成user对象。

当使用此注解时：

```java
@RequestMapping("/save")
public String save(@RequestBody String requestBodyStr){
    System.out.println("请求体：" + requestBodyStr);
    return "success";
}
```

这样，Spring MVC仍然会使用 `FormHttpMessageConverter`消息转换器，请求体的数据则会直接以**字符串**形式传递给requestBodyStr

#### MappingJackson2HttpMessageConverter

如果在请求体中提交的是一个JSON格式的字符串，这个JSON字符串传递给Spring MVC之后，可以直接将JSON字符串转换成POJO对象，使用@RequestBody参数，底层使用的消息转换器是：`MappingJackson2HttpMessageConverter`，来完成JSON与POJO类的转换

步骤如下（同@ResponseBody一节的步骤）：

1. 引入jackson依赖

2. 开启注解驱动

3. 创建POJO类，并将POJO类作为控制器方法的参数，使用@RequestBody标注该参数

   ```java
   @RequestMapping("/send")
   @ResponseBody
   public String send(@RequestBody User user){
       System.out.println(user);
       System.out.println(user.getUsername());
       System.out.println(user.getPassword());
       return "success";
   }
   ```

4. 请求体重提交json格式数据

   ```xml
   <!DOCTYPE html>
   <html lang="en" xmlns:th="http://www.thymeleaf.org">
   <head>
       <meta charset="UTF-8">
       <title>首页</title>
       <script th:src="@{/static/js/vue3.4.21.js}"></script>
       <script th:src="@{/static/js/axios.min.js}"></script>
   </head>
   <body>
   
   <div id="app">
       <button @click="sendJSON">通过POST请求发送JSON给服务器</button>
       <h1>{{message}}</h1>
   </div>
   
   <script>
       let jsonObj = {"username":"zhangsan", "password":"1234"}
   
       Vue.createApp({
           data(){
               return {
                   message:""
               }
           },
           methods: {
               async sendJSON(){
                   console.log("sendjson")
                   try{
                       const res = await axios.post('/springmvc/send', JSON.stringify(jsonObj), {
                           headers : {
                               "Content-Type" : "application/json"
                           }
                       })
                       this.message = res.data
                   }catch(e){
                       console.error(e)
                   }
               }
           }
       }).mount("#app")
   </script>
   
   </body>
   </html>
   ```

### RequestEntity类

这个类的实例封装了整个请求协议：包括请求行、请求头、请求体所有信息，将其放在控制器方法的参数上：

```java
@RequestMapping("/send")
@ResponseBody
public String send(RequestEntity<User> requestEntity){
    System.out.println("请求方式：" + requestEntity.getMethod());
    System.out.println("请求URL：" + requestEntity.getUrl());
    HttpHeaders headers = requestEntity.getHeaders();
    System.out.println("请求的内容类型：" + headers.getContentType());
    System.out.println("请求头：" + headers);

    User user = requestEntity.getBody();
    System.out.println(user);
    System.out.println(user.getUsername());
    System.out.println(user.getPassword());
    return "success";
}
```

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711032010156-cb98e4a9-5238-4dd6-ac1a-81dd6198a47d.png?x-oss-process=image%2Fformat%2Cwebp)

### ResponseEntity类

该类的实例可以封装响应协议，包括：状态行、响应头、响应体。也就是说：如果你想定制属于自己的响应协议，可以使用该类

假如我要完成这样一个需求：前端提交一个id，后端根据id进行查询，如果返回null，请在前端显示404错误。如果返回不是null，则输出返回的user

```java
@Controller
public class UserController {
     
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(user);
        }
    }
}
```

当用户不存在时：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711032765280-343794d6-b262-460b-8c03-e14bd8946850.png?x-oss-process=image%2Fformat%2Cwebp)

当用户存在时：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711032830325-866fe36b-cc47-4493-b9bb-8ebd34c7a86c.png?x-oss-process=image%2Fformat%2Cwebp)

### 文件上传与下载

#### 文件上传

html代码：

```html
<!--文件上传表单-->
<form th:action="@{/file/up}" method="post" enctype="multipart/form-data">
    文件：<input type="file" name="fileName"/><br>
    <input type="submit" value="上传">
</form>

</body>
</html>
```

form表单采用post请求，enctype是multipart/form-data，并且上传组件是：type="file"

**在DispatcherServlet配置时，添加 multipart-config 配置信息:**

```xml
<!--前端控制器-->
<servlet>
    <servlet-name>dispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:springmvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
    <multipart-config>
        <!--设置单个支持最大文件的大小-->
        <max-file-size>102400</max-file-size>
        <!--设置整个表单所有文件上传的最大值-->
        <max-request-size>102400</max-request-size>
        <!--设置最小上传文件大小-->
        <file-size-threshold>0</file-size-threshold>
    </multipart-config>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcherServlet</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

controller代码，将浏览器接收到的文件存储到服务器：

```java
@Controller
public class FileController {

    @RequestMapping(value = "/file/up", method = RequestMethod.POST)
    public String fileUp(@RequestParam("fileName") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        String name = multipartFile.getName();
        System.out.println(name);
        // 获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        System.out.println(originalFilename);
        // 将文件存储到服务器中
        // 获取输入流
        InputStream in = multipartFile.getInputStream();
        // 获取上传之后的存放目录
        File file = new File(request.getServletContext().getRealPath("/upload"));
        // 如果服务器目录不存在则新建
        if(!file.exists()){
            file.mkdirs();
        }
        // 开始写
        //BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath() + "/" + originalFilename));
        // 可以采用UUID来生成文件名，防止服务器上传文件时产生覆盖
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath() + "/" + UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."))));
        byte[] bytes = new byte[1024 * 100];
        int readCount = 0;
        while((readCount = in.read(bytes)) != -1){
            out.write(bytes,0,readCount);
        }
        // 刷新缓冲流
        out.flush();
        // 关闭流
        in.close();
        out.close();

        return "ok";
    }

}
```



#### 文件下载

代码比较固定

html中使用超链接即可：

```html
<!--文件下载-->
<a th:href="@{/download}">文件下载</a>
```

controller类要返回ResonponseEntity类的实例，从而返回自定义的响应（响应体为文件数据，响应头和响应状态）：

```java
@GetMapping("/download")
public ResponseEntity<byte[]> downloadFile(HttpServletResponse response, HttpServletRequest request) throws IOException {
    File file = new File(request.getServletContext().getRealPath("/upload") + "/1.jpeg");
    // 创建响应头对象
    HttpHeaders headers = new HttpHeaders();
    // 设置响应内容类型
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // 设置下载文件的名称
    headers.setContentDispositionFormData("attachment", file.getName());

    // 下载文件
    return new ResponseEntity<byte[]>(Files.readAllBytes(file.toPath()), headers, HttpStatus.OK);
}
```

### 异常处理器

异常处理器就是用来在处理器controller方法中出现异常时，使用异常处理器进行跳转到对应视图，从而用户友好界面。

SpringMVC为异常处理提供了一个接口：HandlerExceptionResolver

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711683439894-1af197f8-20d1-401b-8704-11d51b131670.png?x-oss-process=image%2Fformat%2Cwebp)

核心方法是：resolveException。

该方法用来编写具体的异常处理方案。返回值ModelAndView，表示异常处理完之后跳转到哪个视图。

两个常用默认实现：

- 默认异常处理器：DefaultHandlerExceptionResolver：
  - ![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711683899955-8f7b2a54-716a-4b36-8550-e4630f695bca.png?x-oss-process=image%2Fformat%2Cwebp)
- 自定义异常处理器：SimpleMappingExceptionResolver

#### 自定义异常处理器

即SimpleMappingExceptionResolver

通常两种方式：XML配置文件或注解方式

XML配置文件方式：

```xml
springmvc.xml:
<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
    <property name="exceptionMappings">
        <props>
            <!--用来指定出现异常后，跳转的视图-->
            <prop key="java.lang.Exception">tip</prop>
        </props>
    </property>
    <!--将异常信息存储到request域，value属性用来指定存储时的key。-->
    <property name="exceptionAttribute" value="e"/>
</bean>
```

展示界面：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>出错了</title>
</head>
<body>
<h1>出错了，请联系管理员！</h1>
<div th:text="${e}"></div>
</body>
</html>
```



注解方式：

```java
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public String tip(Exception e, Model model){
        model.addAttribute("e", e);
        return "tip";
    }
}
```

### 拦截器 Interceptor

拦截器类似于过滤器 Filter，但Filter主要用于对请求和响应内容的处理，如设置请求头、状态码、编码等，其工作处于DispatcherServlet之前和响应结束的最后。

并且拦截器属于SpringMVC，过滤器属于JavaWeb规范

Springmvc中拦截器的作用是在请求到达Controller之前或之后进行拦截，对请求和响应做一些处理

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711639953694-56fde7e8-af9f-4abc-b680-48ccf30b9df9.png?x-oss-process=image%2Fformat%2Cwebp)

常见场景：登陆验证、权限校验、请求日志（记录请求信息）、更改响应（对响应进行更改）

拦截器可以实现多个，回被自动放到ArrayList集合中。每一个拦截器都有三个方法：preHandle、postHandle、afterCompletion（视图渲染结束后），正好对应图中三种调用时机（自动调用）

#### 拦截器创建与基本配置

1. 定义拦截器 ：

实现`org.springframework.web.servlet.HandlerInterceptor` 接口，共有三个方法可以进行选择性的实现：

- preHandle：处理器方法调用之前执行
  - **只有该方法有返回值，返回值是布尔类型，true放行，false拦截。**

- postHandle：处理器方法调用之后执行
- afterCompletion：渲染完成后执行

2. 基本配置：

   - 方法1：

     ```xml
     <mvc:interceptors>
         <bean class="com.powernode.springmvc.interceptors.Interceptor1"/>
     </mvc:interceptors>
     ```

   - 方法2：

     ```xml
     <mvc:interceptors>
         <ref bean="interceptor1"/>
     </mvc:interceptors>
     ```

     这种方法需要启动包扫名，并将Interceptor类进行@Component标记

     

   **注意：对于这种基本配置来说，拦截器是拦截所有请求的。**

   3. 高级配置（匹配拦截）

      使用mvc:interceptor，里面最后仍然是配基本配置

      拦截所有路径：/**

      ```xml
      <mvc:interceptors>
          <mvc:interceptor>
              <!--拦截所有路径-->
              <mvc:mapping path="/**"/>
              <!--除 /test 路径之外-->
              <mvc:exclude-mapping path="/test"/>
              <!--拦截器-->
              <ref bean="interceptor1"/>
          </mvc:interceptor>
      </mvc:interceptors>
      ```

      

#### 拦截器执行顺序

1. 若所有拦截器preHandle都是true

   执行preHandle时是按照xml配置顺序执行，执行完controller之后，再按照反顺序执行 1234 controller 4321

2. 若有一个拦截器preHandle返回false

   执行preHandle时是按照xml配置顺序执行，不会执行contorller，且不会执行拦截（返回false）的那个拦截器的afterCompletion，但会按照反顺序执行这个拦截器之前的拦截器的afterCompleton（因为Index存储的是上一次的，所以执行after时是从index反向执行）！1234 321![image-20241004103817700](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241004103817700.png)

HandlerExcutionChain有controller和InterceptorList和当前InterceptorIndex

## day1004

### SpringMVC回顾总结

DispatcherServlet是一个Servlet，Tomcat服务器负责创建DispatcherServlet对象，并且Tomecat服务器自动调用这个Servlet的init方法，且只在服务器启动时调用一次，来初始化Servlet

DispatcherServlet类的核心方法doDispatch，一次请求调用一次

```java
public class DispatcherServlet extends FrameworkServlet {
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 根据请求对象request（根据请求路径）获取处理器
        //返回的实际上是一个处理器执行链对象（包括对应这次请求的所有拦截器和controller）
        // 这个处理器执行链对象是在每次发送请求时都创建一个，是请求级别的，一次请求一个
        // 该对象中描述了本次请求应该执行的拦截器是哪些，顺序是怎样的，要执行的处理器是哪个
        HandlerExecutionChain mappedHandler = getHandler(processedRequest);

        // 根据处理器执行链对象里的处理器controller，来获取处理器适配器。（底层使用了适配器模式）
        // HandlerAdapter在web服务器启动的时候就创建好了。（启动时创建多个HandlerAdapter放在List集合中）
        // HandlerAdapter有多种类型：
        // RequestMappingHandlerAdapter：用于适配使用注解 @RequestMapping 标记的控制器方法
        // SimpleControllerHandlerAdapter：用于适配实现了 Controller 接口的控制器
        // 注意：此时还没有进行数据绑定（也就是说，表单提交的数据，此时还没有转换为pojo对象。）
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

        // 执行本次请求对应的所有拦截器中的 preHandle 方法
        if (!mappedHandler.applyPreHandle(processedRequest, response)) {
            return;
        }

        // 通过处理器适配器调用处理器方法
        // 在调用处理器方法之前会进行数据绑定，将表单提交的数据绑定到处理器方法上（如POJO对象等）。（底层是通过WebDataBinder完成的）
        // 在数据绑定的过程中会使用到消息转换器：HttpMessageConverter
        // 结束后返回ModelAndView对象（ModelAndView对象有一个Model属性和viewName属性，将逻辑视图名封装其中）
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

        //  执行请求对应的所有拦截器中的 postHandle 方法
        mappedHandler.applyPostHandle(processedRequest, response, mv);

        // 处理分发结果（在这个方法中完成响应，将响应结果到浏览器，并在这一过程中还会调用afterCompletion拦截器方法）
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }


    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
			@Nullable Exception exception) throws Exception {
        // 渲染视图
        render(mv, request, response);
        // 渲染完毕后，调用该请求对应的所有拦截器的 afterCompletion方法。
        mappedHandler.triggerAfterCompletion(request, response, null);
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 通过视图解析器解析，返回视图对象
        view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
        // 调用视图对象的渲染，真正的渲染视图
        view.render(mv.getModelInternal(), request, response);
    }

    //通过视图名字返回视图对象
    protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
			Locale locale, HttpServletRequest request) throws Exception {
        // 通过视图解析器（具体是视图解析器接口的实现类 比如ThymeleafViewResovler），通过逻辑视图名字转换为物理视图名字，返回视图对象
        ViewRosovler viewResovler；
        // 视图对象返回（具体是视图接口的实现类对象 比如ThymeleafView）
        View view = viewResolver.resolveViewName(viewName, locale);
        return view;
	}
}
```

#### 获得HandlerExecutionChain

![image-20241004163850140](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241004163850140.png)

根据请求对象（Request），由处理器映射器（Mapping）返回一个【由处理器方法（HandlerMethod）封装的】处理器执行链对象（HandlerExecutionChain）：

```java
// 根据每一次的请求对象来获取处理器执行链对象
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (this.handlerMappings != null) {
            // 遍历所有的HandlerMapping 找到当前请求对应的Handler
			for (HandlerMapping mapping : this.handlerMappings) {
                // 通过处理器映射器来获取的处理器执行链对象
                // 底层实际上会通过 HandlerMapping 对象获取 HandlerMethod对象，将HandlerMethod 对象传递给 HandlerExecutionChain对象。
                // 注意：HandlerMapping对象和HandlerMethod对象都是在服务器启动阶段创建的。
                // RequestMappingHandlerMapping对象中有多个HandlerMethod对象。
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
                    // 找到一个mapping里有对应的handler 就返回
					return handler;
				}
			}
		}
		return null;
	}


```



##### HandlerMapping

处理请求（doDispatch）的第一步代码：

```java
HandlerExecutionChain mappedHandler = getHandler(processedRequest);
```

本质上这一过程中内部调用了

```java
 HandlerExecutionChain handler = mapping.getHandler(request);
```

HandlerMapping（上面的mapping就是其实现类对象）是根据请求的url映射对应的handler（可以认为写的controller类），是一个接口，其实现类很多，最常用的：RequestMappingHandlerMapping类（专门处理controller上带有@RequestMapping注解的）

HandlerMapping有多种类型（有处理@RequestMapping注解的、XML配置文件的等等）。每一个HandlerMapping对象在服务器启动的时候就创建好了，放到了List集合中。



##### HandlerMethod

```java
HandlerExecutionChain类中的属性：
    public class HandlerExcutionChain{
        Object handler = new HandlerMethod(....);
        List<HandlerInterceptor> interceptorList;
}
```

封装有关由方法和bean组成的处理程序方法的信息。提供对方法参数、方法返回值、方法注释等的方便访问。

在web服务器启动时，初始化spring容器的时候，就创建好了HandlerMethod，这个类中比较重要的属性：beanName（或者bean本身）和Method。

每一个Controller方法都对应一个HandlerMethod对象，beanName（或bean）对应该控制器方法所在的Controller类实例（bean），Method对应具体的控制器方法。

`SpringMVC`应用启动时会搜集并分析每个`Web`控制器方法，从中提取对应的 "<请求匹配条件,控制器方法>“ 映射关系，形成一个映射关系表保存在一个`RequestMappingHandlerMapping bean`中。然后在客户请求到达时，再使用 `RequestMappingHandlerMapping`中的该映射关系表找到相应的控制器方法去处理该请求。 "请求匹配条件" 通过 `RequestMappingInfo`包装和表示，而 "控制器方法"则通过 `HandlerMethod`来包装和表示。

**所有的HandlerMapping对象（包括注解的、xml配置的，每一种都放在list中）和HandlerMethod对象都在SpringMVC应用启动时就产生。 一次请求对应的具体的Mapping对象去（自己内部的关系映射表）找到对应请求的HandlerMethod返回给HandlerExecutionChain对象的handler属性**



#### 获得HandlerAdapter

```java
//mappedHandler.getHandler()就是获得HandlerExecutionChain中的HandlerMethod对象 即这次请求对应的处理器，并且可以看到此方法的一些信息（如注解等）
//
HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

```

##### HandlerAdapter

HandlerAdapter可以根据找到的handler执行对应的方法，然后返回ModelAndView

注意HandlerMapping只是找（映射）对应的handler，而不执行

HandlerAdapter也是一个接口

每一个处理器（即controller）都有自己适合的处理器适配器，SpringMVC中有很多处理器适配器，其中常用的就是RequestMappingHandlerAdapter（HandlerAdapte接口的一个实现类），专门处理带有@RequestMapping注解的controller的

在服务器启动阶段，所有的HandlerAdapter接口的实现类对象都会创建，并且放在list中

底层使用适配器模式，对HandlerMethod（即参数handler）进行了适配，

```java
protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (this.handlerAdapters != null) {
            //对所有的HandlerAdapter进行遍历
			for (HandlerAdapter adapter : this.handlerAdapters) {
                //看当前适配器是否支持此handler 即控制器
                //比如RequestMappingHandlerAdapter就支持带有@RequestMapping注解的控制器
				if (adapter.supports(handler)) {
					return adapter;
				}
			}
		}
		throw new ServletException("No adapter for handler [" + handler +
				"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}
```



#### 拦截器调用

见拦截器一章 比较好懂  通过list和index，对拦截器进行调用

#### 处理器方法调用

```java
// 通过处理器适配器调用处理器方法
// 在调用处理器方法之前会进行数据绑定，将表单提交的数据绑定到处理器方法上（如POJO对象等）。（底层是通过WebDataBinder完成的），在数据绑定的过程中会使用到消息转换器：HttpMessageConverter
// 结束后返回ModelAndView对象（ModelAndView对象有一个Model属性和viewName属性，将逻辑视图名封装其中）
mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
```

调用HandlerMethod方法前，需要准备数据，也就是参数绑定。

前端数据通过消息转换器HttpMessageConverter转换为POJO对象，之后绑定到HandlerMethod对象

具体步骤：数据绑定、获取可调用的处理器方法、为可调用的方法绑定数据和设置参数、执行可调用的方法、返回ModelAndView对象

#### 处理分发结果

见上面第一小节分析，主要是视图解析器对象和视图对象，的解析和渲染

![未命名文件.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711943505835-476f954e-ba6c-4a78-b16b-683524e25520.png?x-oss-process=image%2Fformat%2Cwebp)

#### WEB服务器启动时工作

`DispatcherServlet extends FrameworkServlet extends HttpServletBean extends HttpServlet extends GenericServlet implements Servlet`

服务器启动阶段完成了：

1. 初始化Spring上下文，也就是创建所有的bean，让IoC容器将其管理起来。

2. 初始化SpringMVC相关的对象：处理器映射器，处理器适配器等。。。:

   ![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711945408231-6e96abeb-ceff-480e-9f2c-72bfa2a5d419.png?x-oss-process=image%2Fformat%2Cwebp)

[123-SpringMVC源码解读-服务器启动时做了什么_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1sC411L76f?p=123&vd_source=45e7edc0c3a2d1e39ba23997d0099d72)

包括配置在web.xml中的前端控制器的init-param等配置信息会在Tomcat服务器启动时，自动封装到ServletConfig中，Tomcat服务器在调用DispatcherServlet的init方法时候，会自动将创建好的ServletConfig传递给DispatcherServlet中的init方法参数

##### 简化的DispatcherServlet的init代码：

![image-20241005112241805](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241005112241805.png)

这样就能获取到web.xml中的init-pram，从而获取到springmvc.xml

![image-20241005112341316](C:\Users\14429\AppData\Roaming\Typora\typora-user-images\image-20241005112341316.png)

之后初始化Spring容器：

1. 初始化Spring容器

   组件扫描包下的类纳入IoC容器的管理。

   创建视图解析器对象

   创建所有的拦截器对象

   扫描这个包下所有的类：org.myspringmvc.web.servlet.mvc.method.annotation，全部实例化，纳入IoC容器管理

2. 初始化HandlerMapping

3. 初始化HandlerAdapter
4. 初始化ViewResolver

## day1005

### SpringMVC全注解开发

不用再写web.xml，springmvc.xml

而且项目不用添加web支持（直接main下新建webapp目录，webapp下新建WEB-INF目录，并在其下新建thymeleaf目录（放thymeleaf视图等）

#### Servlet3.0新特性

规范中提供了一个接口：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711700341492-8c9a85d9-bca5-484f-8d5d-c3939f48db95.png?x-oss-process=image%2Fformat%2Cwebp)

服务器在启动的时候会自动从容器中找 `ServletContainerInitializer`接口的实现类，自动调用它的`onStartup`方法来完成Servlet上下文的初始化

在Spring3.1版本时，提供了上述接口的实现类`SpringServletContainerInitializer`：

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711700544729-77092224-626d-4b76-8408-f3744fe2ad72.png?x-oss-process=image%2Fformat%2Cwebp)

![image.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711700669446-3bcc469c-71d3-423a-86f7-52e95b73f344.png?x-oss-process=image%2Fformat%2Cwebp)

在服务器启动的时候，它会去加载所有实现`WebApplicationInitializer`接口的类，这个接口下有一个抽象子类是我们需要的：`AbstractAnnotationConfigDispatcherServletInitializer`。因此，我们编写继承该抽象子类的类，并重写其中的方法后，web服务器在启动的时候会根据它来初始化Servlet上下文。

![未命名文件.png](https://cdn.nlark.com/yuque/0/2024/png/21376908/1711701535524-d2635ca6-3bae-4613-9dbb-ed6cb0b7dca6.png?x-oss-process=image%2Fformat%2Cwebp)

#### 编写WebAppInitializer

这是一个配置类，所以要加上@Configuration注解

要继承AbstractAnnotationConfigDispatcherServletInitialize类，重写其中的方法

```java
package com.powernode.springmvc.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * ClassName: WebAppInitializer
 * Description:
 * Datetime: 2024/3/29 16:50
 * Author: 老杜@动力节点
 * Version: 1.0
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * Spring的配置
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    /**
     * SpringMVC的配置
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{SpringMVCConfig.class};
    }

    /**
     * 用于配置 DispatcherServlet 的映射路径
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * 配置过滤器
     * @return
     */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceRequestEncoding(true);
        characterEncodingFilter.setForceResponseEncoding(true);
        HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();
        return new Filter[]{characterEncodingFilter, hiddenHttpMethodFilter};
    }
}

```

为什么是数组，因为每种配置都可以加好几个。

SpringConfig（相当于web.xml）类和SpingMVCConfig(相当于springmvc.xml)类都是需要自己创建，并且都要加上@Configuration注解



##### SpringMVCConfig

实现WebMvcConfigurer ：

加入组件扫描（注解）

开启注解驱动（注解）

配置视图解析器（3个方法）

开启静态资源处理（默认Servlet处理，重写方法）

视图控制器（view-controller，重写方法）

异常处理器（重写方法）

拦截器（重写方法）

```java
// 指定该类是一个配置类，可以当配置文件使用
@Configuration
// 开启组件扫描
@ComponentScan("com.powernode.springmvc.controller")
// 开启注解驱动
@EnableWebMvc
public class SpringMVCConfig implements WebMvcConfigurer {

    @Bean
    public ThymeleafViewResolver getViewResolver(SpringTemplateEngine springTemplateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver iTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(iTemplateResolver);
        return templateEngine;
    }

    @Bean
    public ITemplateResolver templateResolver(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/thymeleaf/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);//开发时关闭缓存，改动即可生效
        return resolver;
    }
    
    //开启默认Servlet处理（静态资源处理）
    @Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    	configurer.enable();
	}
    
    //加入一个view-controller
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/test").setViewName("test");
    }
    
    //异常处理器放到参数集合中（这里演示加入一个）
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties prop = new Properties();
        //下面的语句是用来表示出现java.lang.Exception异常后跳转到tip页面
        prop.setProperty("java.lang.Exception", "tip");
        resolver.setExceptionMappings(prop);
        //异常将异常信息存储到request域，value属性用来指定存储时的key
        resolver.setExceptionAttribute("yiChang");
        resolvers.add(resolver);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	MyInterceptor myInterceptor = new MyInterceptor();                 registry.addInterceptor(myInterceptor).addPathPatterns("/**").excludePathPatterns("/test");
    }
}
```

## day1006

### Spring整合MyBatis

#### 数据源连接池

因为每次用数据的时候再去连接数据库效率不高。

可以先准备connection对象放池子里，用的时候直接去池子里拿对象，用完归还给池子

JDBC的数据库连接池用javax.sql.DataSource来表示，是一个接口，其实现类通常由服务器（如Tomcat）或其它（Druid）等等来实现

编写DataSourceConfig：

```java
package com.powernode.ssm.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


public class DataSourceConfig {

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}

//如果不自动注入：
    public class DataSourceConfig {
    @Bean
    public DataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/druid");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        return druidDataSource;
    }
}
```

注意里面的属性由resources的jdbc.properties配置文件指定（放在resources目录下，以后就可以使用classpath:前缀来表示！！），jdbc.properties在Spring的配置文件中进行路径指定。

`1006问题：爆红时，maven下载源代码`

#### MyBatisConfig

MyBatisConfig：

```java
package com.powernode.ssm.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


public class MyBatisConfig {

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource){
        
        //SqlSessionFactoryBean 是 Spring 对 MyBatis 的整合，用于创建和配置 MyBatis 的 SqlSessionFactory 实例。SqlSessionFactory 是 MyBatis 框架的核心，用于管理数据库会话和执行SQL操作
    
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        
        //设置数据源，会根据形参，自动在容器中找到类型相容的对象进行注入（自动找到我们配置的Druid）
        sqlSessionFactoryBean.setDataSource(dataSource);
        
        //这个方法告诉 MyBatis 框架在指定的包路径下扫描实体类，然后为这些实体类设置别名。这样，您在编写 MyBatis 的 Mapper 文件时可以直接使用实体类的别名来引用它们，而不必使用完整的类名。
        //举例来说，如果您有一个位于 com.powernode.ssm.bean 包下的实体类 User，并且您在 com.powernode.ssm.dao 包下编写了相应的 Mapper 接口，那么在 Mapper 文件中，您可以使用 User 的别名来引用它，而不必写完整的类名 com.powernode.ssm.bean.User。这种别名设置可以提高代码的可读性和维护性，减少了在 Mapper 文件中书写冗长的类名的需要。
        sqlSessionFactoryBean.setTypeAliasesPackage("com.powernode.ssm.bean");
        return sqlSessionFactoryBean;
    }

    
    //配置dao包的扫描
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("com.powernode.ssm.dao");
        return msc;
    }

}
```

#### SpringConifg

注意，Spring的包扫描只需要扫描service即可，因为dao已经在Mybatis中配过了（//配置dao包的扫描部分），controller包的扫描（也就是handler的扫描）也会在SpringMVC中配置。

注意，jdbc.properties的注入也需要使用类注解@PropertySource

注意，需要将我们写的DataSourceConfig和MybatisConfig两个配置类导入进来，因此使用@Import注解

```java
package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.example.service")
@PropertySource("classpath:jdbc.properties")
@Import({DataSourceConfig.class, MyBatisConfig.class})
public class SpringConfig {
}

```



### SSM整合

SSM：Spring、SpringMVC、Mybatis

笔记见：[第14章 SSM整合 (yuque.com)](https://www.yuque.com/dujubin/java/iptnrxyr74aeehc8)

上节Spring已经整合了Mybatis



下面Spring整合SpringMVC：

1. 首先web.xml的全注解形式：

（实现AbstractAnnotationConfigDispathcerServletInitializer，三个必须重写的方法分别对应实现Spring的配置（也就是我们上一节写的SpringConfig.class）、SpringMVC配置（需要新建SpringMvc.class）和DispatcherServlet的映射路径（直接"/"））

```java
package org.example.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;
import java.util.Properties;

// 指定该类是一个配置类，可以当配置文件使用
@Configuration
// 开启组件扫描
@ComponentScan("org.example.handler")
// 开启注解驱动
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Bean
    public ThymeleafViewResolver getViewResolver(SpringTemplateEngine springTemplateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver iTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(iTemplateResolver);
        return templateEngine;
    }

    @Bean
    public ITemplateResolver templateResolver(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/WEB-INF/thymeleaf/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);//开发时关闭缓存，改动即可生效
        return resolver;
    }

    //开启默认Servlet处理（静态资源处理）
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //加入一个view-controller
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/test").setViewName("test");
    }

    //异常处理器放到参数集合中（这里演示加入一个）
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties prop = new Properties();
        //下面的语句是用来表示出现java.lang.Exception异常后跳转到tip页面
        prop.setProperty("java.lang.Exception", "tip");
        resolver.setExceptionMappings(prop);
        //异常将异常信息存储到request域，value属性用来指定存储时的key
        resolver.setExceptionAttribute("yiChang");
        resolvers.add(resolver);
    }


}
```

2. springmvc.xml的全注解形式：SpringMVCConfig类：

   ```java
   package org.example.config;
   
   import org.springframework.context.ApplicationContext;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.ComponentScan;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.HandlerExceptionResolver;
   import org.springframework.web.servlet.config.annotation.*;
   import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
   import org.thymeleaf.spring6.SpringTemplateEngine;
   import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
   import org.thymeleaf.spring6.view.ThymeleafViewResolver;
   import org.thymeleaf.templatemode.TemplateMode;
   import org.thymeleaf.templateresolver.ITemplateResolver;
   
   import java.util.List;
   import java.util.Properties;
   
   // 指定该类是一个配置类，可以当配置文件使用
   @Configuration
   // 开启组件扫描
   @ComponentScan("org.example.handler")
   // 开启注解驱动
   @EnableWebMvc
   public class SpringMvcConfig implements WebMvcConfigurer {
   
       @Bean
       public ThymeleafViewResolver getViewResolver(SpringTemplateEngine springTemplateEngine) {
           ThymeleafViewResolver resolver = new ThymeleafViewResolver();
           resolver.setTemplateEngine(springTemplateEngine);
           resolver.setCharacterEncoding("UTF-8");
           resolver.setOrder(1);
           return resolver;
       }
   
       @Bean
       public SpringTemplateEngine templateEngine(ITemplateResolver iTemplateResolver) {
           SpringTemplateEngine templateEngine = new SpringTemplateEngine();
           templateEngine.setTemplateResolver(iTemplateResolver);
           return templateEngine;
       }
   
       @Bean
       public ITemplateResolver templateResolver(ApplicationContext applicationContext) {
           SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
           resolver.setApplicationContext(applicationContext);
           resolver.setPrefix("/WEB-INF/thymeleaf/");
           resolver.setSuffix(".html");
           resolver.setTemplateMode(TemplateMode.HTML);
           resolver.setCharacterEncoding("UTF-8");
           resolver.setCacheable(false);//开发时关闭缓存，改动即可生效
           return resolver;
       }
   
       //开启默认Servlet处理（静态资源处理）
       @Override
       public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
           configurer.enable();
       }
   
       //加入一个view-controller
       @Override
       public void addViewControllers(ViewControllerRegistry registry) {
           registry.addViewController("/test").setViewName("test");
       }
   
       //异常处理器放到参数集合中（这里演示加入一个）
       @Override
       public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
           SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
           Properties prop = new Properties();
           //下面的语句是用来表示出现java.lang.Exception异常后跳转到tip页面
           prop.setProperty("java.lang.Exception", "tip");
           resolver.setExceptionMappings(prop);
           //异常将异常信息存储到request域，value属性用来指定存储时的key
           resolver.setExceptionAttribute("yiChang");
           resolvers.add(resolver);
       }
   
   
   }
   ```

   

#### 添加事务控制

1. 在SpringConfig上加上@EnableTransactionManagement开启事务管理器

2. 在DataSourceConfig中配置事务管理器（此时使用的是PlatformTransactionManager，还可以使用其他的）

   ```java
   	@Bean
       public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
           DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
           //为事务管理器设置datasource
           dataSourceTransactionManager.setDataSource(dataSource);
           return dataSourceTransactionManager;
       }
   ```

   

3. 在需要的service类上添加@Transactional注解（更多内容见day0923 事务-JDBC）

#### 测试

##### 后端

1. 创建User表

2. 创建bean.User类

3. 创建dao.UserDao接口

   ```java
   //不用添加注解扫描，因为在Mybatis中已经添加包扫描路径了
   package org.example.dao;
   
   import org.apache.ibatis.annotations.Select;
   import org.example.bean.User;
   
   public interface UserDao {
       @Select("select * from tbl_user where id = #{id}")
       User selectById(Long id);
   }
   
   ```

4. 创建Service接口和实现类：

   ```java
   //接口
   package org.example.service;
   
   import org.example.bean.User;
   
   public interface UserService {
       User getById(Long id);
   }
   
   //实现类
   package org.example.service.impl;
   
   import org.example.bean.User;
   import org.example.dao.UserDao;
   import org.example.service.UserService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;
   
   @Service
   public class UserServiceImpl implements UserService {
   
       @Autowired
       private UserDao userDao;
   
       @Override
       public User getById(Long id) {
           return userDao.selectById(id);
       }
   }
   
   ```

5. 编写controller

   ```java
   package org.example.handler;
   
   import org.example.bean.User;
   import org.example.service.UserService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.PathVariable;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController  //这个注解表明RESTFul风格 并且自动加上@ResponseBody 可以将user对象转换成JSON格式给前端
   @RequestMapping("/users")
   public class UserHandler {
   
       @Autowired
       private UserService userService;
   
       @GetMapping("/{id}")
       public User detail(@PathVariable("id") Long id){
           User user = userService.getById(id);
           return user;
       }
   }
   
   ```

##### 前端

在webapp下新建static/js目录，将需要的vue3.5.11.js和axios.min.js放进去

在webapp/WEB-INF/thymeleaf下新建index.html：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
  <script th:src="@{/static/js/vue3.5.11.js}"></script>
  <script th:src="@{/static/js/axios.min.js}"></script>
</head>
<body>
<div id="app">
  <button @click="getMessage">查看id=1的用户信息</button>
  <h1>{{message}}</h1>
</div>
<script th:inline="javascript">
  Vue.createApp({
    data(){
      return{
        message : ''
      }
    },
    methods : {
      async getMessage(){
        let response = await axios.get([[@{/}]] + 'users/1');
        this.message = response.data
      }
    }
  }).mount("#app")
</script>
</body>
</html>
```

