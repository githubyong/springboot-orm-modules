package org.example.sdj.mutidb.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class DBConfigConditional implements Condition {

    @SneakyThrows
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EnableDbConfig.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("org.example.sdj")) {
            Class clz = Class.forName(bd.getBeanClassName());
            EnableDbConfig ann = (EnableDbConfig) clz.getAnnotation(EnableDbConfig.class);
            List<String> enableDbs = Arrays.stream(ann.value()).map(it -> it.getName()).collect(Collectors.toList());
            return metadata.getAnnotations().stream().filter(it->enableDbs.contains(it.getSource().toString())).findAny().isPresent();
        }
        return false;
    }
}
