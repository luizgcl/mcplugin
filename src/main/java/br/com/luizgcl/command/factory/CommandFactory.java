package br.com.luizgcl.command.factory;

import br.com.luizgcl.command.builder.CommandBuilder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandFactory {

    public static CommandBuilder make(CommandBuilder base) {
        return base.executor(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
    }

}
