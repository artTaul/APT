package com.zwt.apt_processor;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.zwt.apt_annotation.BindView;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements mElements;
    private Map<String, ClassTemplate> mClassTemplateMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportType = new LinkedHashSet<>();
        supportType.add(BindView.class.getCanonicalName());
        return supportType;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Messager messager = processingEnv.getMessager();
        mClassTemplateMap.clear();
        //获取所有包含该注解的集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        if (elements.size() > 0) {
            messager.printMessage(Diagnostic.Kind.NOTE, "=============开始处理BindView==============");
            //组装数
            String fullClassName = "";
            for (Element element : elements) {
                boolean isPrintLog = true;
                //拿到当前注解元素
                VariableElement variableElement = (VariableElement) element;
                TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
                isPrintLog = !fullClassName.equals(classElement.getQualifiedName().toString());
                fullClassName = classElement.getQualifiedName().toString();
                if (isPrintLog) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "被注解的类 [" + fullClassName + "]");
                }
                messager.printMessage(Diagnostic.Kind.NOTE, "被注解类的元素 [" + variableElement.getSimpleName().toString() +"]");
                ClassTemplate template = mClassTemplateMap.get(fullClassName);
                if (template == null) {
                    template = new ClassTemplate(mElements, classElement);
                    mClassTemplateMap.put(fullClassName, template);
                }
                //获取注解中参数的值
                BindView bindViewAnnotation = variableElement.getAnnotation(BindView.class);
                int id = bindViewAnnotation.value();
                template.putElement(id, variableElement);
            }

            //生成class类
            for (String key : mClassTemplateMap.keySet()) {
                ClassTemplate template = mClassTemplateMap.get(key);
                try {
                    JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(template.getClassFullName(), template.getTypeElement());
                    Writer writer = fileObject.openWriter();
                    writer.write(template.generateJavaCode(messager));
                    writer.flush();
                    writer.close();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "=============BindView处理结束==============");
        }
        return true;
    }
}