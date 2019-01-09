# AOP实现-Android平台transform和ASM结合插入代码

## 代码结构：
* app：应用层，这里只作为实验对象，在不同方法中插入代码实现功能；
* agent: 代理model，负责实现要插入的方法，此处实现时间计算；
* sohuGroovy: 读取字节码并插入的实现model，本身是一个java的jar，所以不能通过api和implementation方式导入到Android的model中；  

> agent和sohuGroovy没有引用关系，agent是sohuGroovy插入方法的实现；  

## 导入方法
1. 在Project的build.gradle中添加maven地址，dependencies中添加classpath，导入sohuGroovy发布的包；  

2. 在app的build.gradle中添加 apply plugin: 'com.sohu.groovy'，然后导入model agent（导入方法不限制，目的是为了app打包时包含agent的类）
3. 如果在应用中需要使用注解，那么在app的依赖中添加sohuGroovy的依赖：  

```
// 如果使用了注解，需要用这种方法添加sohuGroovy包；
compileOnly 'com.sohu.groovy:sohuGroovy:1.0-SNAPSHOT';
annotationProcessor 'com.sohu.groovy:sohuGroovy:1.0-SNAPSHOT'
```

## 编译方法
> 由于采用了maven方式，所以需要先生成repo仓库包  

1. 注释掉project的build.gradle中关于sohuGroovy的引用;
2. 注释掉app的build.gradle中关于sohuGroovy的应用（apply plugin部分和dependencies中关于sohuGroovy的引用）;
3. sync gradle，然后在Gradle中sohuGroovy下的upload会生成uploadArchives任务，执行任务在repo中生成sohuGroovy的包;
4. 把1和2中注释掉的部分恢复，然后同步gradle，生成apk；
