package br.com.luizgcl.cook;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CookUtils {

    /**
     * Procura no servidor qual o resultado de assar este item.
     * Funciona para Comida, Minérios, Areia, etc.
     */
    public static ItemStack getSmeltingResult(ItemStack source) {
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                // Verifica se o input da receita bate com o item que temos
                if (furnaceRecipe.getInputChoice().test(source)) {
                    // Verificação extra: Queremos apenas COMIDA? 
                    // Se quiser liberar minérios também, remova a verificação isEdible()
                    ItemStack result = furnaceRecipe.getResult();
                    if (result.getType().isEdible()) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    // Conta quanto carvão/carvão vegetal tem
    public static int countCoal(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && (item.getType() == Material.COAL || item.getType() == Material.CHARCOAL)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    // Remove a quantidade exata de carvão
    public static void removeCoal(Player player, int amountToRemove) {
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (amountToRemove <= 0) break;
            
            if (item != null && (item.getType() == Material.COAL || item.getType() == Material.CHARCOAL)) {
                int amount = item.getAmount();
                if (amount <= amountToRemove) {
                    item.setAmount(0); // Remove tudo
                    amountToRemove -= amount;
                } else {
                    item.setAmount(amount - amountToRemove);
                    amountToRemove = 0;
                }
            }
        }
    }

}
