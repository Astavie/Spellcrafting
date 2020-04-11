package astavie.spellcrafting.apiimpl;

import astavie.spellcrafting.api.ISpellcraftingAPI;
import astavie.spellcrafting.api.SpellcraftingAPIInject;
import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.apiimpl.spell.*;
import astavie.spellcrafting.apiimpl.spell.type.FocusTypes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpellcraftingAPI implements ISpellcraftingAPI {

	private static final Logger LOGGER = LogManager.getLogger(SpellcraftingAPI.class);
	private static final SpellcraftingAPI INSTANCE = new SpellcraftingAPI();

	private final FocusTypes types = new FocusTypes();
	private final SpellRegistry registry = new SpellRegistry();

	private SpellcraftingAPI() {
	}

	public static SpellcraftingAPI instance() {
		return INSTANCE;
	}

	public static void deliver() {
		// Majorly copied from Refined Storage's API

		Type annotationType = Type.getType(SpellcraftingAPIInject.class);

		List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
				.map(ModFileScanData::getAnnotations)
				.flatMap(Collection::stream)
				.filter(a -> annotationType.equals(a.getAnnotationType()))
				.collect(Collectors.toList());

		LOGGER.info("Found {} Spellcrafting API injection {}", annotations.size(), annotations.size() == 1 ? "point" : "points");

		for (ModFileScanData.AnnotationData annotation : annotations) {
			try {
				Class clazz = Class.forName(annotation.getClassType().getClassName());
				Field field = clazz.getField(annotation.getMemberName());

				if (field.getType() == ISpellcraftingAPI.class) {
					field.set(null, INSTANCE);
				}

				LOGGER.info("Injected Spellcrafting API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName());
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
				LOGGER.error("Could not inject Spellcrafting API in {} {}", annotation.getClassType().getClassName(), annotation.getMemberName(), e);
			}
		}
	}

	@Override
	public IFocusStack createFocusStack() {
		return new FocusStack();
	}

	@Override
	public IBeadStack createBeadStack(int id, IBead bead) {
		return new BeadStack(id, bead);
	}

	@Override
	public ISpellTemplate createSpellTemplate() {
		return new SpellTemplate();
	}

	@Override
	public ISpell createSpell(ICaster caster, ISpellTemplate spell) {
		return new Spell(caster, spell);
	}

	@Override
	public ISpell readSpell(CompoundNBT nbt) {
		return new Spell(nbt);
	}

	@Override
	public IFocusTypes focusTypes() {
		return types;
	}

	@Override
	public ISpellRegistry spellRegistry() {
		return registry;
	}

}
