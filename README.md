# XTCP
[![xtcp][xtcpsvg]][xtcp]  [![api][apisvg]][api]

一个便捷的TCP消息包拼装和解析框架

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 特征

* 简单通过`@Protocol`和`@ProtocolField`的配置，即可让实体对象拥有自动转化为TCP传输的byte数据和自动byte数据解析。

* 支持byte、short、int、long、byte\[\]、short\[\]、int\[\]、long\[\]、String等常用基础类型，支持类型的拓展

* 支持大端和小端两种存储方式，支持设置全局默认存储方式和局部存储方式。

* 支持short、int、long读取长度的自定义。

* 支持对实体字段进行排序，避免解析错乱。

* 支持自定义协议项和协议解析器。

* 支持不定长数组解析。

* 支持自动协议映射，自动根据读取的opcode识别出对应的协议并进行解析，并根据对应注册的协议信息判断协议是否有响应。

-------

## 1、演示（请star支持）

### Demo下载

[![downloads](https://img.shields.io/badge/downloads-1.3M-blue.svg)](https://github.com/xuexiangjys/XTCP/blob/master/apk/xtcpdemo_1.0.apk?raw=true)

![](https://github.com/xuexiangjys/XTCP/blob/master/img/download.png)

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
  implementation 'com.github.xuexiangjys.XTCP:xtcp_runtime:1.0.1'
  annotationProcessor 'com.github.xuexiangjys.XTCP:xtcp_compiler:1.0.1'
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

### 2.2 初始化协议中心

在Application中注册协议中心和协议字段中心。

```
XTCP.getInstance()
        .addIProtocolCenter(AppProtocolCenter.getInstance()) //添加协议中心
        .addIProtocolFieldCenter(AppProtocolFieldCenter.getInstance(), XTCPProtocolFieldCenter.getInstance()) //添加协议字段中心
        .setDefaultStorageMode(StorageMode.BigEndian) //设置默认存储方式（默认是大端）
        .debug(true); //打开调试日志
```

### 2.3 构建通信协议实体

通过继承`XProtocolItem`或者实现`IProtocolItem`接口，通过`@Protocol`和`@ProtocolField`注解进行配置。

* @Protocol: 用于注解协议项，包括`name`、`opcode`、`resCode`、`mode`、`desc`等属性。

* @ProtocolField: 用于注解协议项字段，包括`index`、`isField`、`length`、`mode`、`charset`等属性。

```
@Protocol(name = "参数设置请求", opCode = 0x12, resCode = 0x33, desc = "注意重启下位机后生效！")
public class SettingRequest extends XProtocolItem {
    @ProtocolField(index = 0)
    private byte func1;
    @ProtocolField(index = 1)
    private short func2;
    @ProtocolField(index = 2)
    private int func3;
    @ProtocolField(index = 3)
    private long func4;
    @ProtocolField(index = 4)
    private ByteArray list1;
    @ProtocolField(index = 5)
    private ShortArray list2;
    @ProtocolField(index = 6)
    private IntArray list3;
    @ProtocolField(index = 7)
    private LongArray list4;
    @ProtocolField(index = 8)
    private StringField string1;
    @ProtocolField(index = 9)
    private LargeString string2;
    @ProtocolField(index = 10)
    private TestProtocolItem testItem;
    @ProtocolField(index = 11)
    private LoginInfo loginInfo;
    @ProtocolField(index = 12)
    private LoginInfoArray loginInfos;
}
```

### 2.4 自定义协议项和协议项数组

#### 自定义协议项

要想自定义协议项很简单，同样的，只需要继承`XProtocolItem`或者实现`IProtocolItem`接口，使用`@ProtocolField`注解进行配置即可。

【注意】不需要使用`@Protocol`进行注解。

```
public class LoginInfo extends XProtocolItem {
    @ProtocolField(index = 0)
    private StringField loginName;

    @ProtocolField(index = 1)
    private StringField password;
}

```

#### 自定义协议项数组

自定义协议项数组类需要继承`AbstractArrayItem`，详细的实现案例[点击查看](https://github.com/xuexiangjys/XTCP/blob/master/app/src/main/java/com/xuexiang/xtcpdemo/model/LoginInfoArray.java)

### 2.5 协议项的byte化和反byte化

凡是继承`XProtocolItem`或者实现`IProtocolItem`接口，都将有如下两个方法:

```
/**
 * 将协议实体转化为byte数组
 *
 * @param storageMode 存储形式
 * @return
 */
byte[] proto2byte(StorageMode storageMode);

/**
 * 将byte数组数据转化为协议实体
 *
 * @param bytes       byte数组数据
 * @param index       起始字节
 * @param tailLength  消息尾的长度[和index一起决定了数据解析的范围]
 * @param storageMode 存储形式
 * @return 是否解析成功
 */
boolean byte2proto(byte[] bytes, int index, int tailLength, StorageMode storageMode);
```

使用案例如下:

```
SettingRequest request = new SettingRequest()
        .setFunc1((byte) 0x45)
        .setFunc2((short) 12)
        .setFunc3(2345)
        .setFunc4((long) 1213131233)
        .setList1((byte) 0x23, (byte) 0x45, (byte) 0x56)
        .setList2((short) 11, (short) 22, (short) 33)
        .setList3(111, 222, 333)
        .setList4((long) 1221312, (long) 23123123)
        //长度超过255的话，就溢出了
        .setString1("我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！")
        .setString2("我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！" +
                "我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！我的名字叫薛翔！")
        .setLoginInfo(new LoginInfo("xuexiang", "123456"))
        .setLoginInfos(new LoginInfo("xuexiang1", "222"),
                new LoginInfo("xuexiang23", "3333"),
                new LoginInfo("xuexiang456", "44444"))
        .setTestItem(new TestProtocolItem()
                .setFunc1((byte) 0x56)
                .setFunc2((short) 314)
                .setFunc3(6111)
                .setFunc4((long) 35536234)
                .setList1(314, 334, 34235, 67584, 45234, 6757)
                .setLoginInfo(new LoginInfo("xuexiangjys", "111111")));
byte[] bytes = request.proto2byte(StorageMode.Default);
Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));

SettingRequest request1 = new SettingRequest();
request1.byte2proto(bytes, 0, 0, StorageMode.Default);

Log.e("xuexiang", request1.toString());
```

### 2.6 消息体包装与解析

实现`IMessage`接口，可自定义属于自己的协议消息载体。框架中默认提供了`XMessage`和`XOrderlyMessage`两种消息体模版。

* XMessage: 无序消息体模版，无消息ID，无法检测是否丢包或者包重复。[详细实现点击查看](https://github.com/xuexiangjys/XTCP/blob/master/xtcp_runtime/src/main/java/com/xuexiang/xtcp/core/message/template/XMessage.java)

* XOrderlyMessage: 有序消息体模版, 可检测是否丢包或者包重复。[详细实现点击查看](https://github.com/xuexiangjys/XTCP/blob/master/xtcp_runtime/src/main/java/com/xuexiang/xtcp/core/message/template/XOrderlyMessage.java)

#### 消息转byte的使用方法

* 1.新建你需要使用的消息体模版。

* 2.设置你传输的协议项。

* 3.使用`msg2Byte`方法将消息转化为byte数组数据。

#### byte转消息的使用方法

* 1.新建你需要转化的消息体模版。

* 2.使用`byte2Msg`方法，传入byte数组数据进行转化。

```
MessageTest messageTest = new MessageTest()
        .setFunc1((byte) 0x45)
        .setFunc2((short) 12)
        .setFunc3(2345)
        .setFunc4((long) 1213131233)
        .setList2((short) 11, (short) 22, (short) 33)
        .setLoginInfo(new LoginInfo("xuexiang", "123456"));

XMessage message = new XMessage()
        .setIProtocolItem(messageTest);
byte[] bytes = message.msg2Byte();
Log.e("xuexiang", ConvertUtils.bytesToHexString(bytes));

XMessage message1 = new XMessage();
boolean result = message1.byte2Msg(bytes);

Log.e("xuexiang", "result:" + result +", ProtocolItem:" + message1.getProtocolItem());
```

### 2.7 自定义协议解析器

如果你对协议的解析有特殊的需求，可实现`IProtocolParser`接口，并通过`XTCP.getInstance().setIProtocolParser`来替换[默认的协议解析器](https://github.com/xuexiangjys/XTCP/blob/master/xtcp_runtime/src/main/java/com/xuexiang/xtcp/core/parser/impl/DefaultProtocolParser.java)。

【注意】谨慎替换，如果替换方法有误的话，会导致整个框架无法正常使用，建议不要替换。

-----

## 3、基础内容介绍

### 协议项注解@Protocol

属性名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
name | String | "" | 协议的名称
opCode | byte | /(必填） | 协议命令码，协议命令的唯一号
resCode | byte | -1 | 协议响应码（结果对应的命令码）
mode | StorageMode | StorageMode.Default | 数据存储方式（大端 or 小端）
desc | String | "" | 描述信息

### 协议字段注解@ProtocolField

属性名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
index | int | /(必填） | 字段的顺序索引
isField | boolean | true | 是否为协议解析字段
length | int | -1 | 协议字段的长度, 不设置的话，默认自动识别
mode | StorageMode | StorageMode.Default | 数据存储方式（大端 or 小端）
charset | String | "UTF-8" | 字符集（只对String有效）

### 基础数组数据类型

类名 | 对应的数组类型 | 数组最大长度
:-|:-:|:-:
ByteArray | byte\[\] | 255
LargeByteArray | byte\[\] | 65535
IntArray | int\[\] | 255
ShortArray | short\[\] | 255
LongArray | long\[\] | 255
StringField | String | 255
LargeString | String | 65535

【注意】:框架支持不定长数组，如果你的数组长度不定（没有确定长度的字段），那么你有且只能有这么一个数组可以不使用以上的数组包装类。

## 4、混淆配置

```
# xtcp
-keep @com.xuexiang.xtcp.annotation.* class * {*;}
-keep class * {
    @com.xuexiang.xtcp.annotation.* <fields>;
}
-keepclassmembers class * {
    @com.xuexiang.xtcp.annotation.* <methods>;
}
```

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![](https://github.com/xuexiangjys/XPage/blob/master/img/qq_group.jpg)

[xtcpsvg]: https://img.shields.io/badge/XTCP-v1.0.1-brightgreen.svg
[xtcp]: https://github.com/xuexiangjys/XTCP
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14
