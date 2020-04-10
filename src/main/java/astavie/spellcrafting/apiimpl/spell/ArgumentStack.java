package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.*;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class ArgumentStack implements IArgumentStack {

	private final List<IModifier> modifiers = NonNullList.create();

	private Pair<Integer, Integer> reference;
	private IArgument argument;

	public void setArgument(IBeadStack stack, int argument) {
		this.reference = Pair.of(stack.getId(), argument);
		this.argument = null;
	}

	@Override
	public void setArgument(IArgument argument) {
		this.reference = null;
		this.argument = argument;
	}

	@Nullable
	@Override
	public IArgument getArgument() {
		return argument;
	}

	@Nullable
	@Override
	public Pair<IBeadStack, Integer> getReference(ISpell spell) {
		return reference == null ? null : Pair.of(spell.getBead(reference.getLeft()), reference.getRight());
	}

	@Override
	public List<IModifier> getModifiers() {
		return modifiers;
	}

	@Override
	public IArgumentType<?> getType(ISpell spell) {
		return modifiers.isEmpty() ? argument == null ?
				spell.getBead(reference.getLeft()).getArgument(reference.getRight()).getType(spell) :
				argument.getType() :
				modifiers.get(modifiers.size() - 1).getType();
	}

}
