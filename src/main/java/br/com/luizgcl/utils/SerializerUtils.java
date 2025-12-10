package br.com.luizgcl.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializerUtils {

    // Transforma um array de itens em uma String criptografada
    public static String itemStackArrayToBase64(ItemStack[] items) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            
            dataOutput.writeInt(items.length);
            
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
    }

    // Pega a String e transforma de volta em itens
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            
            for (int i = 0; i < items.length; i++) {
                try {
                    items[i] = (ItemStack) dataInput.readObject();
                } catch (ClassNotFoundException e) {
                    throw new IOException("Unable to decode class type.", e);
                }
            }
            
            return items;
        }
    }
}