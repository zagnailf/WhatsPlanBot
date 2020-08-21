package ru.whatsplan.bot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.whatsplan.bot.spring.annotation.*;
import ru.whatsplan.bot.spring.annotation.Scene;
import ru.whatsplan.bot.spring.bot.BotSceneMode;
import ru.whatsplan.bot.spring.bot.BotScript;
import ru.whatsplan.bot.spring.bot.BotScriptContainer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TelegramUpdateHandlerBeanPostProcessor implements BeanPostProcessor, Ordered {

    private BotApiMethodContainer oldContainer = BotApiMethodContainer.getInstance();
    private BotScriptContainer container = BotScriptContainer.getInstance();
    private Map<String, Class<?>> botControllerMap = new HashMap<>();
    private Map<String, Object> botSceneBeanMap = new HashMap<>();

    private Map<String, BotScript> botStartSceneCommandMap = new HashMap<>();
    private Set<BotScript> botControlSceneCommands = new HashSet<>();

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
        // TODO Исправить момент с принудительным объявлением StartBotController после всех контроллеров
        // То есть искуственно изменил название класса, чтобы сначала добавлялись все конроллеры в мапу botControllerMap
        // И в последнюю очередь выполнялся контроллер StartBotController для полной инициализации скрипта

        // Если имя бина не содержится в мапе
        if (!botControllerMap.containsKey(beanName)) return bean;
        // Сохранить объект бина существующий в botControllerMap
        botSceneBeanMap.put(beanName, bean);
        // Если в бине методы не содержат анатацию @StartBot, то вернуть пришедший бин
        boolean isStartBotBean = Arrays.stream(botControllerMap.get(beanName).getMethods()).anyMatch(method -> method.isAnnotationPresent(StartBot.class));
        if (!isStartBotBean) return bean;

        Class<?> original = botControllerMap.get(beanName);
        Method startBotMethod = getStartBotMethod(original);
        // Получить все методы помеченные анатацией @SceneStep
        List<Method> sceneStepMethods = getSceneMethods();

        // Начинаем с метода помеченного анатацией @StartBot,
        // затем проходим по методам которые соответсвуют значению поля анатации next()
        generateStartController(bean, startBotMethod, sceneStepMethods);

        return bean;
    }

    private List<Method> getSceneMethods() {
        return botControllerMap.values().stream()
                .flatMap(controller -> Arrays.stream(controller.getMethods()))
                .filter(method -> method.isAnnotationPresent(Scene.class))
                .collect(Collectors.toList());
    }

    private Method getStartBotMethod(Class<?> original) {
        return Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(StartBot.class))
                .findFirst().orElse(null);
    }

    private void generateStartController(Object bean, Method superMethod, List<Method> sceneSteps) {
        StartBot startScene = superMethod.getAnnotation(StartBot.class);
        // TODO: Сделать для множественной команды
        String startSceneCommand = getFirstCommand(startScene.value());
        String[] startSceneNextCommands = startScene.next();

        // Прохожу по всем следующим командам
        // [/remind, /settings, ...]
        Set<BotScript> nextScripts = generateNextController(sceneSteps, startSceneNextCommands);
        BotApiMethodController controller = createProcessTextController(bean, superMethod);
        BotScript startBotScript = new BotScript(BotSceneMode.START, startSceneCommand, controller, nextScripts);
        container.setScript(startBotScript, botStartSceneCommandMap);
    }

    private Set<BotScript> generateNextController(List<Method> sceneSteps, String[] startSceneNextCommands) {
        Set<BotScript> nextScripts = new HashSet<>();

        for (String nextCommand : startSceneNextCommands) {
            addNextCommand(sceneSteps, nextScripts, nextCommand);
        }

        return nextScripts;
    }

    private void addNextCommand(List<Method> sceneSteps, Set<BotScript> nextScripts, String nextCommand) {
        Method method = getNextMethod(sceneSteps, nextCommand);

        if (method != null) {
            Scene sceneStep = method.getAnnotation(Scene.class);
            BotSceneMode mode = sceneStep.mode();
            String[] next = sceneStep.next();
            List<Pattern> patterns = getScenePatterns(sceneStep);

            String beanName = getBeanName(method);
            Object bean = botSceneBeanMap.get(beanName);

            BotApiMethodController controller = createProcessTextController(bean, method);

            addBotScript(sceneSteps, nextScripts, nextCommand, mode, next, patterns, controller);
        }
    }

    private void addBotScript(List<Method> sceneSteps, Set<BotScript> nextScripts, String nextCommand, BotSceneMode mode,
                              String[] next, List<Pattern> patterns, BotApiMethodController controller) {
        switch (mode) {
            case START:
                // TODO: придумать как здесь добавить controls
                botControlSceneCommands.clear();
                BotScript botScript = new BotScript(mode, nextCommand, controller, patterns, generateNextController(sceneSteps, next));
                botStartSceneCommandMap.put(nextCommand, botScript);
                nextScripts.add(botScript);
                break;
            case CONTROL:
                botControlSceneCommands.add(new BotScript(mode, nextCommand, controller, patterns, generateNextController(sceneSteps, next)));
                break;
            case STEP:
            default:
                nextScripts.add(new BotScript(mode, nextCommand, controller, patterns, generateNextController(sceneSteps, next), botControlSceneCommands));
        }
    }

    private BotApiMethodController getApiMethodController(Method method, Object bean) {
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasMessage() && update.getMessage().hasText();
            }
        };
    }

    private String getBeanName(Method method) {
        return botControllerMap.entrySet().stream()
                .filter(entry -> Arrays.asList(entry.getValue().getMethods()).contains(method))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    private List<Pattern> getScenePatterns(Scene sceneStep) {
        return Arrays.stream(sceneStep.patterns())
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    private Method getNextMethod(List<Method> sceneSteps, String nextCommand) {
        return sceneSteps.stream()
                .filter(currentMethod -> Arrays.asList(
                            currentMethod.getAnnotation(Scene.class).value())
                                .contains(nextCommand))
                .findFirst().orElse(null);
    }

    private String getFirstCommand(String[] commands) {
        if (commands.length > 0)
            return commands[0];
        return null;
    }

    private BotApiMethodController createProcessCallbackController(Object bean, Method method) {
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update != null && update.hasCallbackQuery() && update.getCallbackQuery().getData() != null;
            }
        };
    }

    private BotApiMethodController createProcessTextController(Object bean, Method method) {
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
