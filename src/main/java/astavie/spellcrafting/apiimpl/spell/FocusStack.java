package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.*;
import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class FocusStack implements IFocusStack {

	private final List<IAugment<?>> augments = NonNullList.create();

	private Pair<Integer, Integer> reference;
	private IFocus<?> focus;

	public void setFocus(IBeadStack stack, int focus) {
		this.reference = Pair.of(stack.getId(), focus);
		this.focus = null;
	}

	@Nullable
	@Override
	public IFocus getFocus() {
		return focus;
	}

	@Override
	public void setFocus(IFocus<?> focus) {
		this.reference = null;
		this.focus = focus;
	}

	@Nullable
	@Override
	public Pair<IBeadStack, Integer> getReference(ISpellTemplate spell) {
		return reference == null ? null : Pair.of(spell.getBeadFromId(reference.getLeft()), reference.getRight());
	}

	@Override
	public List<IAugment<?>> getAugments() {
		return augments;
	}

	@Override
	public IFocusType<?> getType(ISpellTemplate spell) {
		return augments.isEmpty() ? focus == null ?
				spell.getBeadFromId(reference.getLeft()).getFocus(reference.getRight()).getType(spell) :
				focus.getType() :
				augments.get(augments.size() - 1).getType();
	}

	@Override
	public Object calculate(ISpell spell) {
		Object object;

		if (reference != null) {
			object = spell.getFocus(spell.getSpellTemplate().getBeadFromId(reference.getLeft()), reference.getRight());
		} else {
			object = focus.calculate(spell);
		}

		for (IAugment<?> augment : augments) {
			object = augment.apply(spell, object);
		}

		return object;
	}

}
