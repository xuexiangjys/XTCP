# XTCP

便捷的TCP消息包拼装和解析框架

## 特征

* 简单通过@Protocol和@ProtocolField的配置，即可让实体对象拥有自动转化为TCP传输的byte数据和自动byte数据解析。

* 支持byte、short、int、long、byte\[\]、short\[\]、int\[\]、long\[\]、String等常用基础类型，支持类型的拓展

* 支持大端和小端两种存储方式，支持设置全局默认存储方式和局部存储方式。

* 支持short、int、long读取长度的自定义。

* 支持对实体字段进行排序，避免解析错乱。

* 支持自定义协议项和协议解析器。

* 支持自动协议映射，自动根据读取的opcode识别出对应的协议并进行解析，并根据对应注册的协议信息判断协议是否有响应。

-------

## 2、如何使用

> 目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1 添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:
```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在dependencies添加:

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys.XTCP:xtcp_runtime:1.0.0'
  annotationProcessor 'com.github.xuexiangjys.XTCP:xtcp_compiler:1.0.0'
}
```

3.进行moduleName注册

```
defaultConfig {
    ...

    javaCompileOptions {
        annotationProcessorOptions {
            arguments = [ moduleName : project.getName() ]
        }
    }
}
```
【注意】：如果不注册的话，默认ModuleName为`app`。

4.在Application中注册协议中心和协议字段中心。

```
XTCP.getInstance()
        .addIProtocolCenter(AppProtocolCenter.getInstance()) //添加协议中心
        .addIProtocolFieldCenter(AppProtocolFieldCenter.getInstance(), XTCPProtocolFieldCenter.getInstance()) //添加协议字段中心
        .debug(true); //打开调试日志
```

### 2.2 基础内容介绍

#### 协议项注解@Protocol

属性名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
name | String | "" | 协议的名称
opcode | byte | /(必填） | 协议命令码，协议命令的唯一号
resCode | byte | -1 | 协议响应码（结果对应的命令码）
mode | StorageMode | StorageMode.Default | 数据存储方式（大端 or 小端）
desc | String | "" | 描述信息

#### 协议字段注解@ProtocolField

属性名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
index | int | /(必填） | 字段的顺序索引
isField | boolean | true | 是否为协议解析字段
length | int | -1 | 协议字段的长度, 不设置的话，默认自动识别
mode | StorageMode | StorageMode.Default | 数据存储方式（大端 or 小端）
charset | String | "UTF-8" | 字符集（只对String有效）

#### 基础数组数据类型

类名 | 对应的数组类型 | 数组最大长度
:-|:-:|:-:
ByteArray | byte\[\] | 255
LargeByteArray | byte\[\] | 65535
IntArray | int\[\] | 255
ShortArray | short\[\] | 255
LongArray | long\[\] | 255
StringField | String | 255
LargeString | String | 65535