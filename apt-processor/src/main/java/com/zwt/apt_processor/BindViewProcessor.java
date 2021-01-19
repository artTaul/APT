package com.zwt.apt_processor;

import com.google.auto.service.AutoService;
import com.zwt.apt_annotation.BindView;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
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
        mClassTemplateMap.clear();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassTemplate template = mClassTemplateMap.get(fullClassName);
            if (template == null) {
                template = new ClassTemplate(mElements, classElement);
                mClassTemplateMap.put(fullClassName, template);
            }
            BindView bindViewAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindViewAnnotation.value();
            template.putElement(id, variableElement);
        }
        for (String key : mClassTemplateMap.keySet()) {
            ClassTemplate template = mClassTemplateMap.get(key);
            try {
                JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(template.getClassFullName(), template.getTypeElement());
                Writer writer = fileObject.openWriter();
                writer.write(template.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (Throwable throwable) {

            }
        }
        return true;
    }
}