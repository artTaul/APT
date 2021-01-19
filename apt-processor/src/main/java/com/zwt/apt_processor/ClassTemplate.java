package com.zwt.apt_processor;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class ClassTemplate {
    public static final String SUFFIX_STR = "_ViewBinding";
    private String mBindClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassTemplate(Elements element, TypeElement typeElement) {
        this.mTypeElement = typeElement;
        PackageElement packageElement = element.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindClassName = className + SUFFIX_STR;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    public String getClassFullName() {
        return mPackageName + "." + mBindClassName;
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    public String generateJavaCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package ").append(mPackageName).append(";\n");
        stringBuilder.append('\n');
        stringBuilder.append("public class ").append(mBindClassName).append(" {\n");
        generateBindViewMethods(stringBuilder);
        stringBuilder.append('\n');
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private void generateBindViewMethods(StringBuilder stringBuilder) {
        stringBuilder.append("\tpublic void bindView(");
        stringBuilder.append(mTypeElement.getQualifiedName());
        stringBuilder.append(" context) {\n");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement variableElement = mVariableElementMap.get(id);
            String viewName = variableElement.getSimpleName().toString();
            String viewType = variableElement.asType().toString();
            stringBuilder.append("\t context.");
            stringBuilder.append(viewName);
            stringBuilder.append(" = ");
            stringBuilder.append("(");
            stringBuilder.append(viewType);
            stringBuilder.append(")(((android.app.Activity)context).findViewById( ");
            stringBuilder.append(id);
            stringBuilder.append("));\n");
        }
        stringBuilder.append(" }\n");
    }
}
