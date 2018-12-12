package com.xuexiang.xtcp.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xuexiang.xtcp.annotation.ProtocolField;
import com.xuexiang.xtcp.model.IProtocol;
import com.xuexiang.xtcp.model.IProtocolFieldCenter;
import com.xuexiang.xtcp.model.ProtocolFieldInfo;
import com.xuexiang.xtcp.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
 * @author xuexiang
 * @since 2018/12/12 下午2:17
 */
@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
public class ProtocolFieldProcessor extends AbstractProcessor {
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

    private static final String PROTOCOL_CLASS_NAME = "ProtocolFieldCenter";

    private TypeMirror mIProtocol = null;

    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();   // Contain field need ProtocolField and his super class.

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

        mLogger.info(">>> ProtocolFieldProcessor init. <<<");
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> protocolFieldElements = roundEnvironment.getElementsAnnotatedWith(ProtocolField.class);
            try {
                mLogger.info(">>> Found ProtocolFields, start... <<<");
                parseProtocolFields(protocolFieldElements);

            } catch (Exception e) {
                mLogger.error(e);
            }
            return true;
        }

        return false;
    }

    /**
     * 解析协议字段标注
     *
     * @param protocolFieldElements
     */
    private void parseProtocolFields(Set<? extends Element> protocolFieldElements) throws IOException {
        mLogger.info(">>> Found ProtocolFields, size is " + protocolFieldElements.size() + " <<<");
        categories(protocolFieldElements); //对ProtocolField字段按所在包装类的类名进行分类
        generateProtocolFieldCenterCode(); //生成对应的协议字段管理中心代码
    }

    private void generateProtocolFieldCenterCode() throws IOException {
        if (MapUtils.isNotEmpty(parentAndChild)) {
            ClassName protocolFieldCenterClassName = ClassName.get(PACKAGE_NAME, upperFirstLetter(moduleName) + PROTOCOL_CLASS_NAME);
            TypeSpec.Builder protocolFieldCenterBuilder = TypeSpec.classBuilder(protocolFieldCenterClassName);

            /*
               private static ProtocolFieldCenter sInstance;
             */
            FieldSpec instanceField = FieldSpec.builder(protocolFieldCenterClassName, "sInstance")
                    .addModifiers(Modifier.PRIVATE)
                    .addModifiers(Modifier.STATIC)
                    .build();

            /*

              ``Map<String, ProtocolFieldInfo>```
             */
            ParameterizedTypeName class2FieldsType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(ProtocolFieldInfo.class)
            );

            /*
               private Map<String, ProtocolFieldInfo> mClass2Fields = new HashMap<>();
             */
            FieldSpec class2FieldsField = FieldSpec.builder(class2FieldsType, "mClass2Fields")
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc("类名 -> 协议字段名集合")
                    .initializer("new $T<>()", HashMap.class)
                    .build();

            /*
              构造函数(保证单例）
              private ProtocolFieldCenter() {}
             */
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE);

            ProtocolField protocolField;
            String className;
            String fieldName;
            Map<Integer, String> map;
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {

                TypeElement item = entry.getKey();  //封装字段的最里层类
                List<Element> fields = entry.getValue();
                className = item.asType().toString().replaceAll("<T>", ""); //去除范型

                map = new TreeMap<>();
                for (Element element : fields) {
                    fieldName = element.getSimpleName().toString();
                    protocolField = element.getAnnotation(ProtocolField.class);
                    if (protocolField != null && protocolField.isField()) {
                        mLogger.info(">>> Found ProtocolField: " + className + "[" + fieldName + "] <<<");
                        map.put(protocolField.index(), fieldName);
                    }
                }

                //遍历map中的值
                StringBuilder sb = new StringBuilder();
                for (String value : map.values()) {
                    sb.append(value).append(",");
                }
                constructorBuilder.addStatement("mClass2Fields.put($S, new $T($S))",
                        className,
                        ProtocolFieldInfo.class,
                        sb.toString());
            }

            MethodSpec constructorMethod = constructorBuilder.build();

            MethodSpec instanceMethod = MethodSpec.methodBuilder("getInstance")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(protocolFieldCenterClassName)
                    .addCode("if (sInstance == null) {\n" +
                            "    synchronized ($T.class) {\n" +
                            "        if (sInstance == null) {\n" +
                            "            sInstance = new $T();\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n", protocolFieldCenterClassName, protocolFieldCenterClassName)
                    .addStatement("return sInstance")
                    .build();

            MethodSpec getFieldsByClassNameMethod = MethodSpec.methodBuilder("getProtocolFields")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addJavadoc("根据类名获取协议字段名集合")
                    .addParameter(String.class, "className", Modifier.FINAL)
                    .returns(String[].class)
                    .addStatement("return mClass2Fields.get(className).getFields()")
                    .build();

            MethodSpec getClass2FieldsMethod = MethodSpec.methodBuilder("getClass2Fields")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(class2FieldsType)
                    .addStatement("return mClass2Fields")
                    .build();

            CodeBlock javaDoc = CodeBlock.builder()
                    .add("<p>这是ProtocolFieldProcessor自动生成的类，用以管理协议的映射。</p>\n")
                    .add("<p><a href=\"mailto:xuexiangjys@163.com\">Contact me.</a></p>\n")
                    .add("\n")
                    .add("@author xuexiang \n")
                    .add("@since ").add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).add("\n")
                    .build();

            protocolFieldCenterBuilder
                    .addSuperinterface(ClassName.get(IProtocolFieldCenter.class))
                    .addJavadoc(javaDoc)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(instanceField)
                    .addField(class2FieldsField)
                    .addMethod(constructorMethod)
                    .addMethod(instanceMethod)
                    .addMethod(getFieldsByClassNameMethod)
                    .addMethod(getClass2FieldsMethod);
            JavaFile.builder(PACKAGE_NAME, protocolFieldCenterBuilder.build()).build().writeTo(mFiler);

        }

    }

    /**
     * 对协议字段进行分类
     *
     * @param protocolFieldElements
     */
    private void categories(Set<? extends Element> protocolFieldElements) {
        if (CollectionUtils.isNotEmpty(protocolFieldElements)) {
            for (Element protocolField : protocolFieldElements) {
                //getEnclosingElement--返回封装此元素的最里层元素, 即该字段所在的类。
                TypeElement enclosingElement = (TypeElement) protocolField.getEnclosingElement();

                if (parentAndChild.containsKey(enclosingElement)) { // Has categories
                    parentAndChild.get(enclosingElement).add(protocolField);
                } else {
                    List<Element> childs = new ArrayList<>();
                    childs.add(protocolField);
                    parentAndChild.put(enclosingElement, childs);
                }
            }
            mLogger.info("categories finished.");
        }
    }

    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ProtocolField.class.getCanonicalName());
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
