package astavie.spellcrafting.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Needs to be implemented on a public static {@link ISpellcraftingAPI} field.
 */
@Target(ElementType.FIELD)
public @interface SpellcraftingAPIInject {
}
