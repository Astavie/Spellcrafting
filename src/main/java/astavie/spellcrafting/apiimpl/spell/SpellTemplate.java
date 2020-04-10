package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.ISpellTemplate;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.List;

public class SpellTemplate implements ISpellTemplate {

	private final List<IBeadStack> beads = NonNullList.create();

	@Override
	public List<IBeadStack> getBeads() {
		return beads;
	}

	@Nullable
	@Override
	public IBeadStack getBeadFromId(int id) {
		for (IBeadStack stack : beads)
			if (stack.getId() == id)
				return stack;
		return null;
	}

	@Override
	public int getPosition(IBeadStack stack) {
		for (int i = 0; i < beads.size(); i++)
			if (beads.get(i).getId() == stack.getId())
				return i;
		return -1;
	}

}
