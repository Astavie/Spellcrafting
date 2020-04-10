package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.IFocusStack;

public class BeadStack implements IBeadStack {

	private final int id;
	private final IBead bead;
	private final IFocusStack[] foci;

	public BeadStack(int id, IBead bead) {
		this.id = id;
		this.bead = bead;
		this.foci = new IFocusStack[bead.getFocusCount()];

		for (int i = 0; i < foci.length; i++) {
			foci[i] = bead.getFixedFocus(i);
		}
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
	public IFocusStack getFocus(int index) {
		return foci[index];
	}

	@Override
	public void setFocus(int index, IFocusStack stack) {
		foci[index] = stack;
	}

}
