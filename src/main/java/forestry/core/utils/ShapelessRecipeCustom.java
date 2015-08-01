package forestry.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

public class ShapelessRecipeCustom implements IRecipe {

	private final List<ItemStack> ingredients;
	private final ItemStack product;
	private boolean preserveNBT = false;

	public ShapelessRecipeCustom(ItemStack product, ItemStack... ingredients) {
		this.ingredients = Arrays.asList(ingredients);
		this.product = product;
	}

	public ShapelessRecipeCustom setPreserveNBT() {
		this.preserveNBT = true;
		return this;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>(this.ingredients);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				ItemStack itemstack = inventoryCrafting.getStackInRowAndColumn(j, i);

				if (itemstack != null) {
					boolean flag = false;

					for (ItemStack itemstack1 : arraylist) {
						if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || itemstack.getItemDamage() == itemstack1.getItemDamage())) {
							flag = true;
							arraylist.remove(itemstack1);
							break;
						}
					}

					if (!flag) {
						return false;
					}
				}
			}
		}

		return arraylist.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		ItemStack result = product.copy();

		if (preserveNBT) {
			NBTTagCompound craftingNbt = RecipeUtil.getCraftingNbt(inventoryCrafting);
			if (craftingNbt == null) {
				return null;
			}

			result.setTagCompound(craftingNbt);
		}

		return result;
	}

	@Override
	public int getRecipeSize() {
		return ingredients.size();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return product.copy();
	}

	@SuppressWarnings("unchecked")
	public static ShapelessRecipeCustom buildRecipe(ItemStack product, ItemStack... ingredients) {
		ShapelessRecipeCustom recipe = new ShapelessRecipeCustom(product, ingredients);
		CraftingManager.getInstance().getRecipeList().add(recipe);
		return recipe;
	}

	@SuppressWarnings("unchecked")
	public static ShapelessRecipeCustom buildPriorityRecipe(ItemStack product, ItemStack... ingredients) {
		ShapelessRecipeCustom recipe = new ShapelessRecipeCustom(product, ingredients);
		CraftingManager.getInstance().getRecipeList().add(0, recipe);
		return recipe;
	}
}
