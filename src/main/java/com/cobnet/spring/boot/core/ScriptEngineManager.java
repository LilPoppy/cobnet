package com.cobnet.spring.boot.core;

import com.cobnet.event.polyglot.PolyglotContextCloseEvent;
import com.cobnet.interfaces.PolyglotContextGroup;
import com.cobnet.polyglot.*;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.graalvm.polyglot.Context;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO: set directory and load script from file
public class ScriptEngineManager implements PolyglotContextGroup {

    private final Map<String, PolyglotContext> contexts = new ConcurrentHashMap<>();

    ScriptEngineManager() {}

    public PolyglotContext get(String name) {

        return this.contexts.get(name);
    }

    public boolean remove(String name) {

        return remove(name, false);
    }

    public boolean remove(String name, boolean cancelIfExecuting) {

        PolyglotContext context = this.contexts.get(name);

        if(context != null) {

            context.close(cancelIfExecuting);

            return true;
        }

        return false;
    }

    public boolean remove(PolyglotContext context) {

        return remove(context.getName());
    }

    public boolean remove(PolyglotContext context, boolean cancelIfExecuting) {

        return  remove(context.getName(), cancelIfExecuting);
    }

    public static Collection<PolyglotLanguage> getPolyglotLanguages() {

        try (PolyglotEngine engine = PolyglotEngine.create()) {

            return engine.getLanguages().values();
        }
    }

    public static List<String> getAvailableLanguages() {

        return getPolyglotLanguages().stream().map(lang -> lang.getName()).toList();
    }

    public static Set<PolyglotContext> getContexts() {

        return Collections.unmodifiableSet(ProjectBeanHolder.getScriptEngineManager());
    }

    public static PolyglotContext newContext(String name, String... permittedLanguages) {

        ProjectBeanHolder.getScriptEngineManager().remove(name);

        PolyglotContext context = PolyglotContext.create(name, permittedLanguages);

        ProjectBeanHolder.getScriptEngineManager().contexts.put(name, context);

        return context;
    }

    public static PolyglotContext getOrNewContext(String name) {

        if(ProjectBeanHolder.getScriptEngineManager().size() > 0) {

            return ProjectBeanHolder.getScriptEngineManager().contexts.getOrDefault(name, newContext(name));
        }

        return newContext(name);
    }

    public static PolyglotContext firstOrNewContext() {

        return ProjectBeanHolder.getScriptEngineManager().stream().findFirst().orElse(newContext("undefined"));
    }

    public static PolyglotValue eval(PolyglotContext context, PolyglotSource source) {

        return context.eval(source);
    }

    public static PolyglotValue evalFromResource(PolyglotContext context, String language, String path) {

        return eval(context, PolyglotSource.readFromResource(language, path));
    }

    public static PolyglotValue evalFromResource(String name, String language, String path) {

        return evalFromResource(getOrNewContext(name), language, path);
    }

    public static PolyglotValue evalFromResource(String language, String path) {

        return evalFromResource(firstOrNewContext(), language, path);
    }

    public static PolyglotValue evalFromFile(PolyglotContext context, String language, File file) throws FileNotFoundException, IOException {

        return eval(context, PolyglotSource.readFromFile(language, file));
    }

    public static PolyglotValue evalFromFile(PolyglotContext context, String language, String file) throws FileNotFoundException, IOException {

        return eval(context, PolyglotSource.readFromFIle(language, file));
    }

    public static PolyglotValue evalFromFile(String name, String language, File file) throws FileNotFoundException, IOException {

        return evalFromFile(getOrNewContext(name), language, file);
    }

    public static PolyglotValue evalFromFile(String language, File file) throws FileNotFoundException, IOException {

        return evalFromFile(firstOrNewContext(), language, file);
    }

    public static PolyglotValue evalFromFile(String name, String language, String path) throws FileNotFoundException, IOException {

        return evalFromFile(getOrNewContext(name), language, path);
    }

    public static PolyglotValue evalFromFile(String language, String path) throws FileNotFoundException, IOException {

        return evalFromFile(firstOrNewContext(), language, path);
    }

    public static PolyglotValue eval(String name, PolyglotSource source) {

        return getOrNewContext(name).eval(source);
    }

    public static PolyglotValue eval(PolyglotSource source) {

        return firstOrNewContext().eval(source);
    }

    public static PolyglotValue execute(PolyglotContext context, String language, String member, Object... args) {

        return context.getBindings(language).getMember(member).execute(args);
    }

    public static PolyglotValue execute(String name, String language, String member, Object... args) {

        return execute(getOrNewContext(name), language, member, args);
    }

    public static PolyglotValue execute(String language, String member, Object... args) {

        return execute(firstOrNewContext(), language, member, args);
    }

    @Override
    public int size() {

        return this.contexts.size();
    }

    @Override
    public boolean isEmpty() {

        return this.size() <= 0;
    }

    @Override
    public boolean contains(Object o) {

        if(o instanceof String) {

            return this.contexts.containsKey(o);
        }

        if(o instanceof PolyglotContext) {

            return this.contexts.containsKey(((PolyglotContext)o).getName());
        }

        if(o instanceof  Context) {

            return  this.contexts.values().stream().anyMatch(context -> context.getContext().equals(o));
        }

        return false;
    }

    @Override
    public Iterator<PolyglotContext> iterator() {

        return this.contexts.values().iterator();
    }

    @Override
    public Object[] toArray() {

        return this.contexts.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {

        return this.contexts.values().toArray(a);
    }

    @Override
    public boolean add(PolyglotContext context) {

        if(!this.contexts.containsKey(context.getName())) {

            this.contexts.put(context.getName(), context);

            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {

        if(o instanceof String) {

            return this.remove((String) o);
        }

        if(o instanceof PolyglotContext) {

            return this.remove((PolyglotContext) o);
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {

        collection = getKeys(collection);

        return collection.stream().allMatch(this.contexts::containsKey);
    }

    @Override
    public boolean addAll(Collection<? extends PolyglotContext> collection) {

        Boolean result = true;

        for(PolyglotContext context : collection) {

            result = this.add(context);
        }

        return result;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {

        collection = getKeys(collection);

        Objects.requireNonNull(collection);

        boolean modified = false;

        Iterator<PolyglotContext> it = contexts.values().iterator();

        while (it.hasNext()) {

            PolyglotContext context = it.next();

            if (!collection.contains(context.getName())) {

                this.remove(context.getName());

                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {

        collection = getKeys(collection);

        Objects.requireNonNull(collection);

        boolean modified = false;

        Iterator<PolyglotContext> it = contexts.values().iterator();

        while (it.hasNext()) {

            PolyglotContext context = it.next();

            if (collection.contains(context.getName())) {

                this.remove(context.getName());

                modified = true;
            }
        }

        return modified;


    }

    private Collection<?> getKeys(Collection<?> collection) {

        return collection.stream().map(element -> {

            if(element instanceof String) {

                return element;
            }

            if(element instanceof PolyglotContext) {

                return ((PolyglotContext)element).getName();
            }

            if(element instanceof Context) {

                throw new UnsupportedOperationException(Context.class.getName() + " is not supported");
            }

            throw new IllegalArgumentException("Use " + String.class.getName() + " or " + PolyglotContext.class.getName());

        }).collect(Collectors.toSet());
    }

    @Override
    public void clear() {

        this.contexts.keySet().stream().toList().forEach(this::remove);
    }

    @Component
    final static class PolyglotContextCloseEventListener implements ApplicationListener<PolyglotContextCloseEvent> {

        @Override
        public void onApplicationEvent(PolyglotContextCloseEvent event) {

            ProjectBeanHolder.getScriptEngineManager().contexts.remove(event.getSource().getName());
        }
    }

}
