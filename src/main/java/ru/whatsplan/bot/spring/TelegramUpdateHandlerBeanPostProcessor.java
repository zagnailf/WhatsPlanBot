package ru.whatsplan.bot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.whatsplan.bot.spring.annotation.BotController;
import ru.whatsplan.bot.spring.annotation.BotRequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TelegramUpdateHandlerBeanPostProcessor implements BeanPostProcessor, Ordered {

    private BotApiMethodContainer container = BotApiMethodContainer.getInstance();
    private Map<String, Class<?>> botControllerMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(BotController.class)) {
            botControllerMap.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!botControllerMap.containsKey(beanName)) {
            return bean;
        }

        Class<?> original = botControllerMap.get(beanName);
        Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(BotRequestMapping.class))
                .forEach(method -> generateController(bean, method));
        return bean;
    }

    private void generateController(Object bean, Method method) {
        BotController botController = bean.getClass().getAnnotation(BotController.class);
        BotRequestMapping botRequestMapping = method.getAnnotation(BotRequestMapping.class);

        String path = String.format("%s%s",
                botController.value().length != 0 ? botController.value()[0] : "",
                botRequestMapping.value().length != 0 ? botRequestMapping.value()[0] : "");

        switch (botRequestMapping.method()[0]) {
            case MSG:
                container.addBotController(path, createControllerUpdate2ApiMethod(bean, method));
                break;
            case EDIT:
                container.addBotController(path, createProcessListForController(bean, method));
        }
    }

    private BotApiMethodController createProcessListForController(Object bean, Method method) {
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasCallbackQuery() && update.getCallbackQuery().getData() != null;
            }
        };
    }

    private BotApiMethodController createControllerUpdate2ApiMethod(Object bean, Method method) {
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasMessage() && update.getMessage().hasText();
            }
        };
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
