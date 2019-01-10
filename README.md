# AOP实现-Android平台transform和ASM结合插入代码
感谢大佬[Hunter](https://github.com/Leaking/Hunter.git)的开源，重新实现结构

## 代码结构：
* app：应用层，这里只作为实验对象，在不同方法中插入代码实现功能；
* agent: 代理model，负责实现要插入的方法，此处实现时间计算，其它功能可以直接加入这个model；
* base-transform: 基础的transform实现，读取字节码并插入的实现model，本身与应用层没有联系；
* timing-plugin: 基于base-trasnform实现函数计时，此处过滤Cost注解的函数  

> agent和sohuGroovy没有引用关系，agent是sohuGroovy插入方法的实现；  

## 导入方法
1. 在Project的build.gradle中添加maven地址，dependencies中添加classpath，导入sohuGroovy发布的包；  

2. 在app的build.gradle中添加 
```
implementation project(':agent') 	// 加入agent，保证注入的函数操作能一起打包
apply plugin: 'plugin-timing' // 对应的插件id
timingHunterExt {				// 标记release和debug
    runVariant = 'DEBUG'
}
```

## 编译方法
> 由于采用了maven方式，所以需要先生成repo仓库包  

1. 注释掉project的build.gradle中关于插件的引用;
2. 注释掉app的build.gradle中关于插件的引用（apply plugin部分和关于插件的引用）;
3. sync gradle，首先执行base-transform的uploadArchives任务，然后执行timing-plugin的uploadArchives任务（同样需要注释掉timing-plugin中关于base-transform的引用，base-transform生成后再取消注释）；
4. 把1和2中注释掉的部分恢复，然后同步gradle，生成apk；
