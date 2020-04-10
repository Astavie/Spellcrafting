package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.ISpellTemplate;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.common.spell.event.Events;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class Spell implements ISpell {

	private final ICaster caster;
	private final ISpellTemplate spell;
	private final Map<Pair<Integer, Integer>, Object> objects = new HashMap<>();

	private int position = 0;

	public Spell(ICaster caster, ISpellTemplate spell) {
		this.caster = caster;
		this.spell = spell;
	}

	@Override
	public Object getFocus(IBeadStack stack, int focus) {
		return objects.get(Pair.of(stack.getId(), focus));
	}

	@Override
	public ICaster getCaster() {
		return caster;
	}

	@Override
	public ISpellTemplate getSpellTemplate() {
		return spell;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void cast() {
		proceed(true);
	}

	@Override
	public void tick() {
		proceed(false);
	}

	private void proceed(boolean cast) {
		for (; position < spell.getBeads().size(); position++) {
			IBeadStack stack = spell.getBeads().get(position);

			// Calculate foci
			for (int i = 0; i < stack.getBead().getFocusCount(); i++) {
				objects.put(Pair.of(stack.getId(), i), stack.getFocus(i).calculate(this));
			}

			// Cast bead
			if (!stack.getBead().shouldContinue(this, stack, cast))
				break;

			stack.getBead().cast(this, stack); // TODO: Do something with output

			// Only apply to first bead
			cast = false;
		}
	}

	@Override
	public boolean isFinished() {
		return position >= spell.getBeads().size();
	}

	@Override
	public boolean isWaitingForCast() {
		return !isFinished() && spell.getBeads().get(position).getBead() == Events.CAST;
	}

}
