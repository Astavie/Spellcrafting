package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.ISpell;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.List;

public class Spell implements ISpell {

	private final List<IBeadStack> beads = NonNullList.create();

	@Override
	public List<IBeadStack> getBeads() {
		return beads;
	}

	@Nullable
	@Override
	public IBeadStack getBead(int id) {
		for (IBeadStack stack : beads)
			if (stack.getId() == id)
				return stack;
		return null;
	}

}
