package client;

import shared.AbstractCommand;

import java.io.IOException;

@FunctionalInterface
public interface Callback {
    void call(AbstractCommand message) throws IOException;
}
