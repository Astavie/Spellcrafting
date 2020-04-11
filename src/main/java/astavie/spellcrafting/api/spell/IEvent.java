package astavie.spellcrafting.api.spell;

public interface IEvent extends IBead {

	@Override
	boolean shouldContinue(ISpell spell, IBeadStack stack, boolean castEvent);

	@Override
	boolean isLoaded(ISpell spell, IBeadStack stack);

}
