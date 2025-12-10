package br.com.luizgcl.shop;

import br.com.luizgcl.command.CommandBase;

public class ShopCommand extends CommandBase {

    @Override
    public void setup() {
        createSimplePlayerCommand(player -> {
            new ShopMenu().open(player);
        }, "shop", "loja");
    }
}
