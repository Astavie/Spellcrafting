package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.IFocusStack;
import astavie.spellcrafting.api.spell.ISpellTemplate;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

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

	@Override
	public ListNBT writeToNbt() {
		ListNBT list = new ListNBT();

		for (IBeadStack stack : beads) {
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("id", stack.getId());
			tag.putString("bead", stack.getBead().getRegistryName().toString());

			ListNBT foci = new ListNBT();
			for (int i = 0; i < stack.getBead().getFocusCount(); i++) {
				foci.add(stack.getFocus(i).writeToNbt());
			}

			tag.put("foci", foci);
			list.add(tag);
		}

		return list;
	}

	@Override
	public void readFromNbt(ListNBT nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			CompoundNBT tag = nbt.getCompound(i);

			int id = tag.getInt("id");
			IBead bead = SpellcraftingAPI.instance().spellRegistry().getBead(new ResourceLocation(tag.getString("bead")));
			IBeadStack stack = SpellcraftingAPI.instance().createBeadStack(id, bead);

			ListNBT foci = tag.getList("foci", Constants.NBT.TAG_COMPOUND);
			for (int j = 0; j < foci.size(); j++) {
				IFocusStack focus = SpellcraftingAPI.instance().createFocusStack();
				focus.readFromNbt(foci.getCompound(j));
				stack.setFocus(j, focus);
			}

			beads.add(stack);
		}
	}

}
