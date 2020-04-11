package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.spell.event.Events;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class Spell implements ISpell {

	private final INBT caster;
	private final ISpellTemplate spell;
	private final Map<Integer, INBT[]> objects = new HashMap<>();

	private int position = 0;
	private Location center;

	public Spell(ICaster caster, ISpellTemplate spell) {
		this.caster = SpellcraftingAPI.instance().focusTypes().caster().writeToNBT(caster);
		this.spell = spell;
	}

	public Spell(CompoundNBT nbt) {
		caster = nbt.get("caster");

		spell = SpellcraftingAPI.instance().createSpellTemplate();
		spell.readFromNbt(nbt.getCompound("spell"));

		position = nbt.getInt("position");

		ListNBT objs = nbt.getList("objects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < objs.size(); i++) {
			CompoundNBT compound = objs.getCompound(i);

			int id = compound.getInt("id");
			IBeadStack reference = spell.getBeadFromId(id);
			objects.put(id, new INBT[reference.getBead().getFocusCount()]);

			CompoundNBT objects = compound.getCompound("objects");
			for (int j = 0; j < reference.getBead().getFocusCount(); j++) {
				if (objects.contains(Integer.toString(j))) {
					this.objects.get(id)[j] = objects.get(Integer.toString(j));
				}
			}
		}
	}

	@Override
	public Object getFocus(IBeadStack stack, int i) {
		INBT calculate = objects.get(stack.getId())[i];
		return calculate == null ? null : stack.getFocus(i).getType(spell).readFromNBT(calculate);
	}

	@Override
	public ICaster getCaster() {
		return SpellcraftingAPI.instance().focusTypes().caster().readFromNBT(this.caster);
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
		check();
		proceed(false);
	}

	private void check() {
		// Refresh caster
		getCaster();

		// Refresh objects
		for (Map.Entry<Integer, INBT[]> entry : objects.entrySet())
			for (int i = 0; i < entry.getValue().length; i++)
				getFocus(spell.getBeadFromId(entry.getKey()), i);
	}

	private void proceed(boolean cast) {
		for (; position < spell.getBeads().size(); position++) {
			IBeadStack stack = spell.getBeads().get(position);

			// Calculate foci
			objects.computeIfAbsent(stack.getId(), i -> new INBT[stack.getBead().getFocusCount()]);

			for (int i = 0; i < stack.getBead().getFocusCount(); i++) {
				IFocusStack focus = stack.getFocus(i);
				IFocusType<?> type = focus.getType(spell);
				putObject(stack.getId(), i, focus, type);
			}

			// Cast bead
			if (!stack.getBead().shouldContinue(this, stack, cast))
				break;

			stack.getBead().cast(this, stack); // TODO: Do something with output

			// Only apply to first bead
			cast = false;
		}
	}

	private <I> void putObject(int id, int i, IFocusStack focus, IFocusType<I> type) {
		//noinspection unchecked
		I calculate = (I) focus.calculate(this);
		objects.get(id)[i] = calculate == null ? null : type.writeToNBT(calculate);
	}

	@Override
	public boolean isFinished() {
		return position >= spell.getBeads().size();
	}

	@Override
	public boolean isWaitingForCast() {
		return !isFinished() && spell.getBeads().get(position).getBead() == Events.CAST;
	}

	@Override
	public CompoundNBT writeToNbt() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("caster", caster);
		nbt.put("spell", spell.writeToNbt());
		nbt.putInt("position", position);

		ListNBT objs = new ListNBT();

		for (Map.Entry<Integer, INBT[]> entry : objects.entrySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("id", entry.getKey());

			CompoundNBT list = new CompoundNBT();
			for (int i = 0; i < entry.getValue().length; i++) {
				if (entry.getValue()[i] != null) {
					list.put(Integer.toString(i), entry.getValue()[i]);
				}
			}
			tag.put("objects", list);

			objs.add(tag);
		}

		nbt.put("objects", objs);
		return nbt;
	}

	@Override
	public Location getCenter() {
		return center;
	}

	@Override
	public void setCenter(Location location) {
		center = location;
	}

	@Override
	public boolean isInRange(Location location) {
		double range = spell.getRange();
		return location.getDimension() == center.getDimension() && location.getPos().squareDistanceTo(center.getPos()) < range * range;
	}

	@Override
	public boolean isLoaded() {
		IBeadStack stack = spell.getBeads().get(position);
		return stack.getBead().isLoaded(this, stack);
	}

}
