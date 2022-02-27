package org.example.sdj.advance.support;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.sdj.advance.support.extractor.BaseEntityResultSetExtractor;
import org.example.sdj.advance.support.extractor.BaseEntityRowMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

import java.util.Set;


@Configuration
@Slf4j
@Component
public class ResultMapperFactory implements ApplicationContextAware {

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("applicationContext ResultMapperFactory..........");
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Table.class));
        Set<BeanDefinition> dataEntitySet = scanner.findCandidateComponents("org.example.sdj.advance.entity");
        dataEntitySet.addAll(scanner.findCandidateComponents("org.example.sdj.advance.dto"));
        for (BeanDefinition bd : dataEntitySet) {
            Class clz = Class.forName(bd.getBeanClassName());
            registerBean(context, BaseEntityResultSetExtractor.class, clz);
            registerBean(context, BaseEntityRowMapper.class, clz);
        }
    }

    private void registerBean(ConfigurableApplicationContext context, Class rootCls, Class domainType) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(rootCls);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(domainType);
        String beanName = getBeanName(rootCls, domainType);
        log.debug("registerBean {} {}", rootCls,beanName);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    private String getBeanName(Class rootCls, Class domainType) {
        String excuter = "";
        if (BaseEntityResultSetExtractor.class.equals(rootCls)) {
            excuter = "ResultSetExtractor";
        } else if (BaseEntityRowMapper.class.equals(rootCls)) {
            excuter = "RowMapper";
        }
        return domainType.getSimpleName() + excuter;
    }


}
