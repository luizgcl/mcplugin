package br.com.luizgcl.backpack;

import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;

@Getter
@AllArgsConstructor
public enum BackpackType {
    
    LEATHER( "Couro", Material.LEATHER,  TextColor.color(139, 69, 19), 32, 27),
    IRON( "Ferro", Material.IRON_INGOT,  TextColor.color(192, 192, 192), 64, 36),
    GOLD("Ouro", Material.GOLD_INGOT,  TextColor.color(255, 215, 0), 128, 45),
    DIAMOND("Diamante", Material.DIAMOND,  TextColor.color(0, 191, 255), 256, 54);

    private String name;
    private Material materialType;
    private TextColor color;
    private int costAmount;
    private int inventorySize;
}
