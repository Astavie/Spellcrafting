package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IArgumentStack;
import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IBeadStack;

public class BeadStack implements IBeadStack {

	private final int id;
	private final IBead bead;
	private final IArgumentStack[] arguments;

	public BeadStack(int id, IBead bead) {
		this.id = id;
		this.bead = bead;
		this.arguments = new IArgumentStack[bead.getArgumentCount()];
	}

	@Override
	public IBead getBead() {
		return bead;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public IArgumentStack getArgument(int index) {
		return arguments[index];
	}

	@Override
	public void setArgument(int index, IArgumentStack stack) {
		arguments[index] = stack;
	}

}
