package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;

public class CharmStack {

    private final Charm charm;
    private final ElementStack[] arguments;

    public CharmStack(@NotNull Charm charm) {
        this.charm = charm;
        this.arguments = new ElementStack[charm.getArgumentTypes().length];
        for (int i = 0; i < this.arguments.length; i++) {
            arguments[i] = new ElementStack();
        }
    }

    public @NotNull Charm getCharm() {
        return charm;
    }

    public @NotNull ElementStack getElementStack(int i) {
        return arguments[i];
    }

    public void cast(@NotNull ActiveSpell context, int id) {
        Object[] arguments = new Object[this.arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = this.arguments[i].transform(context);
        }
        charm.cast(context, arguments, id);
    }
    
}
