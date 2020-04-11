package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
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

	@Override
	public CompoundNBT writeToNbt() {
		CompoundNBT nbt = new CompoundNBT();

		if (reference != null) {
			nbt.putInt("referenceBead", reference.getLeft());
			nbt.putInt("referenceFocus", reference.getRight());
		} else {
			nbt.putString("focus", focus.getRegistryName().toString());
		}

		ListNBT list = new ListNBT();
		for (IAugment augment : augments) {
			list.add(StringNBT.valueOf(augment.getRegistryName().toString()));
		}

		nbt.put("augments", list);
		return nbt;
	}

	@Override
	public void readFromNbt(CompoundNBT nbt) {
		if (nbt.contains("referenceBead")) {
			reference = Pair.of(nbt.getInt("referenceBead"), nbt.getInt("referenceFocus"));
		} else {
			focus = SpellcraftingAPI.instance().spellRegistry().getFocus(new ResourceLocation(nbt.getString("focus")));
		}

		ListNBT list = nbt.getList("augments", Constants.NBT.TAG_STRING);
		for (int i = 0; i < list.size(); i++) {
			augments.add(SpellcraftingAPI.instance().spellRegistry().getAugment(new ResourceLocation(list.getString(i))));
		}
	}

}
