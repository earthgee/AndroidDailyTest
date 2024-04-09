package com.earthgee.apt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by zhaoruixuan1 on 2024/4/9
 * CopyRight (c) haodf.com
 * 功能：自动注入findviewbyid
 */
public class BindingProcessor extends AbstractProcessor {

    Messager messager;
    Filer filer;
    Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(BindView.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<Element>> fieldMap = new HashMap<>();
        for(Element element: roundEnv.getElementsAnnotatedWith(BindView.class)) {
            String className = element.getEnclosingElement().getSimpleName().toString();
            if(fieldMap.get(className) != null) {
                List<Element> elementList = fieldMap.get(className);
                elementList.add(element);
            } else {
                List<Element> elements = new ArrayList<>();
                elements.add(element);
                fieldMap.put(className, elements);
            }
        }

        for(Map.Entry<String, List<Element>> entry: fieldMap.entrySet()) {
            try {
                generateCode(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void generateCode(String className, List<Element> elements) throws IOException {
        String packageName = elementUtils.getPackageOf(elements.get(0)).getQualifiedName().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("package ");
        sb.append(packageName);
        sb.append(";\n");

        //public class XXXActivityViewBinding
        String classDefine = "public class"+className+"ViewBinding { \n";
        sb.append(classDefine);

        //构造函数开头
        String constructorName = "public "+className+"ViewBinding("+className+" activity){ \n";
        sb.append(constructorName);

        for(Element e: elements) {
            sb.append("activity."+e.getSimpleName()+"=activity.findViewById("+e.getAnnotation(BindView.class).value()+");\n");
        }

        sb.append("\n }");
        sb.append("\n }");

        JavaFileObject sourceFile = filer.createSourceFile(className+"ViewBinding");
        Writer writer = sourceFile.openWriter();
        writer.write(sb.toString());
        writer.close();
    }

}
















