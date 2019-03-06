package com.xuexiang.xtcp.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.xuexiang.xtcp.annotation.Protocol;
import com.xuexiang.xtcp.enums.StorageMode;
import com.xuexiang.xtcp.model.IProtocol;
import com.xuexiang.xtcp.model.IProtocolCenter;
import com.xuexiang.xtcp.model.ProtocolInfo;
import com.xuexiang.xtcp.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.xuexiang.xtcp.util.Consts.KEY_MODULE_NAME;


/**
 * 协议中心自动生成器
 *
 * @author xuexiang
 * @since 2018/12/11 上午10:09
 */
@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
public class ProtocolProcessor extends AbstractProcessor {
    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    private Types mTypes;
    private Elements mElements;
    /**
     * 日志相关的辅助类
     */
    private Logger mLogger;

    /**
     * Module name, maybe its 'app' or others
     */
    private String moduleName = null;
    /**
     * 页面配置所在的包名
     */
    private static final String PACKAGE_NAME = "com.xuexiang.xtcp";

    private static final String PROTOCOL_CLASS_NAME = "ProtocolCenter";

    private TypeMirror mIProtocol = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();
        mLogger = new Logger(processingEnv.getMessager());

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            mLogger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            mLogger.info("These no module name, at 'build.gradle', like :\n" +
                    "javaCompileOptions {\n" +
                    "    annotationProcessorOptions {\n" +
                    "        arguments = [ moduleName : project.getName() ]\n" +
                    "    }\n" +
                    "}\n");
            //默认是app
            moduleName = "app";
        }
        mIProtocol = mElements.getTypeElement(IProtocol.class.getCanonicalName()).asType();

        mLogger.info(">>> ProtocolProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> protocolElements = roundEnvironment.getElementsAnnotatedWith(Protocol.class);
            try {
                mLogger.info(">>> Found Protocols, start... <<<");
                parseProtocols(protocolElements);

            } catch (Exception e) {
                mLogger.error(e);
            }
            return true;
        }

        return false;
    }

    /**
     * 解析协议标注
     *
     * @param protocolElements
     */
    private void parseProtocols(Set<? extends Element> protocolElements) throws IOException {
        if (CollectionUtils.isNotEmpty(protocolElements)) {
            mLogger.info(">>> Found Protocols, size is " + protocolElements.size() + " <<<");

            ClassName protocolClassName = ClassName.get(PACKAGE_NAME, upperFirstLetter(moduleName) + PROTOCOL_CLASS_NAME);
            TypeSpec.Builder protocolCenterBuilder = TypeSpec.classBuilder(protocolClassName);

            /*
               private static ProtocolCenter sInstance;
             */
            FieldSpec instanceField = FieldSpec.builder(protocolClassName, "sInstance")
                    .addModifiers(Modifier.PRIVATE)
                    .addModifiers(Modifier.STATIC)
                    .build();

            /*

              ``Map<String, ProtocolInfo>```
             */
            ParameterizedTypeName class2InfoType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(ProtocolInfo.class)
            );

            /*
               private Map<String, ProtocolInfo> mClass2Info = new HashMap<>();
             */
            FieldSpec class2InfoField = FieldSpec.builder(class2InfoType, "mClass2Info")
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc("协议类名 -> 协议信息")
                    .initializer("new $T<>()", HashMap.class)
                    .build();

             /*

              ``Map<byte, ProtocolInfo>```
             */
            ParameterizedTypeName opcode2InfoType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(Byte.class),
                    ClassName.get(ProtocolInfo.class)
            );

            /*
               private Map<byte, ProtocolInfo> mOpCode2Info = new HashMap<>();
             */
            FieldSpec opcode2InfoField = FieldSpec.builder(opcode2InfoType, "mOpCode2Info")
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc("opCode -> 协议信息")
                    .initializer("new $T<>()", HashMap.class)
                    .build();


            /*
              构造函数(保证单例）
              private ProtocolCenter() {}
             */
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE);

            TypeMirror tm;
            String name;
            Protocol protocol;
            for (Element element : protocolElements) {
                tm = element.asType();
                // IProtocol
                if (mTypes.isAssignable(tm, mIProtocol)) {
                    mLogger.info(">>> Found Protocol: " + tm.toString() + " <<<");


                    protocol = element.getAnnotation(Protocol.class);
                    name = StringUtils.isEmpty(protocol.name()) ? element.getSimpleName().toString() : protocol.name();

                    constructorBuilder.addStatement("mClass2Info.put($S, new $T($S, $S, (byte)$L, (byte)$L, $T.$L, $S))",
                            tm.toString(),
                            ProtocolInfo.class,
                            name,
                            tm.toString(),
                            protocol.opCode(),
                            protocol.resCode(),
                            StorageMode.class,
                            protocol.mode(),
                            protocol.desc());

                    if (protocol.resCode() == -1) {  //resCode == -1时代表该协议是响应消息
                        constructorBuilder.addStatement("mOpCode2Info.put((byte)$L, new $T($S, $S, (byte)$L, (byte)$L, $T.$L, $S))",
                                protocol.opCode(),
                                ProtocolInfo.class,
                                name,
                                tm.toString(),
                                protocol.opCode(),
                                protocol.resCode(),
                                StorageMode.class,
                                protocol.mode(),
                                protocol.desc());
                    }
                }
            }

            MethodSpec constructorMethod = constructorBuilder.build();

            MethodSpec instanceMethod = MethodSpec.methodBuilder("getInstance")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(protocolClassName)
                    .addCode("if (sInstance == null) {\n" +
                            "    synchronized ($T.class) {\n" +
                            "        if (sInstance == null) {\n" +
                            "            sInstance = new $T();\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n", protocolClassName, protocolClassName)
                    .addStatement("return sInstance")
                    .build();

            MethodSpec getProtocolByClassNameMethod = MethodSpec.methodBuilder("getProtocol")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addJavadoc("根据协议的类名获取协议的详细信息")
                    .addParameter(String.class, "className", Modifier.FINAL)
                    .returns(ProtocolInfo.class)
                    .addStatement("return mClass2Info.get(className)")
                    .build();

            MethodSpec getProtocolByOpcodeMethod = MethodSpec.methodBuilder("getProtocol")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addJavadoc("根据opcode获取协议的详细信息")
                    .addParameter(byte.class, "opCode", Modifier.FINAL)
                    .returns(ProtocolInfo.class)
                    .addStatement("return mOpCode2Info.get(opCode)")
                    .build();

            MethodSpec getOpCodeByClassNameMethod = MethodSpec.methodBuilder("getOpCodeByClassName")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addJavadoc("根据协议的类名获取对应的OpCode")
                    .addParameter(String.class, "className", Modifier.FINAL)
                    .returns(byte.class)
                    .addStatement("return mClass2Info.get(className).getOpCode()")
                    .build();

            MethodSpec getClass2InfoMethod = MethodSpec.methodBuilder("getClass2Info")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(class2InfoType)
                    .addStatement("return mClass2Info")
                    .build();

            MethodSpec getOpCode2InfoMethod = MethodSpec.methodBuilder("getOpCode2Info")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(opcode2InfoType)
                    .addStatement("return mOpCode2Info")
                    .build();

            CodeBlock javaDoc = CodeBlock.builder()
                    .add("<p>这是ProtocolProcessor自动生成的类，用以管理协议的映射。</p>\n")
                    .add("<p><a href=\"mailto:xuexiangjys@163.com\">Contact me.</a></p>\n")
                    .add("\n")
                    .add("@author xuexiang \n")
                    .add("@since ").add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).add("\n")
                    .build();

            protocolCenterBuilder
                    .addSuperinterface(ClassName.get(IProtocolCenter.class))
                    .addJavadoc(javaDoc)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(instanceField)
                    .addField(class2InfoField)
                    .addField(opcode2InfoField)
                    .addMethod(constructorMethod)
                    .addMethod(instanceMethod)
                    .addMethod(getProtocolByClassNameMethod)
                    .addMethod(getProtocolByOpcodeMethod)
                    .addMethod(getOpCodeByClassNameMethod)
                    .addMethod(getClass2InfoMethod)
                    .addMethod(getOpCode2InfoMethod);
            JavaFile.builder(PACKAGE_NAME, protocolCenterBuilder.build()).build().writeTo(mFiler);
        }
    }


    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Protocol.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    private static String upperFirstLetter(final String s) {
        if (StringUtils.isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }
}
