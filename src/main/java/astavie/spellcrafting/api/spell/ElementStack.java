package astavie.spellcrafting.api.spell;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementStack {

    private final LinkedList<Element<?, ?>> elements = new LinkedList<>();

    public void addElement(@NotNull Element<?, ?> element) {
        if (getTargetType() != element.getOriginType()) {
            throw new IllegalArgumentException();
        }

        elements.add(element);
    }

    public void addElements(@NotNull Iterable<Element<?, ?>> elements) {
        for (Element<?, ?> element : elements) addElement(element);
    }

    public @NotNull List<Element<?, ?>> cutFrom(int i) {
        List<Element<?, ?>> list = new LinkedList<>();
        while (i < elements.size()) {
            list.add(elements.remove(i));
        }
        return list;
    }

    public @NotNull Class<?> getTargetType() {
        return elements.isEmpty() ? ActiveSpell.class : elements.getLast().getTargetType();
    }

    public @NotNull List<Element<?, ?>> getList() {
        return Collections.unmodifiableList(elements);
    }

    public @Nullable Object transform(Object o) {
        for (Element<?, ?> element : elements) {
            o = transform(element, o);
        }
        return o;
    }

    @SuppressWarnings("unchecked")
    private static <F> Object transform(Element<F, ?> element, Object from) {
        return element.transform((F) from);
    }
    
}
