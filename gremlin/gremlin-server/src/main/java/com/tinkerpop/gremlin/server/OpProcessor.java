package com.tinkerpop.gremlin.server;

import com.tinkerpop.gremlin.server.op.OpProcessorException;

/**
 * Interface for providing commands that websocket requests will respond to.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface OpProcessor {

    /**
     * The name of the processor which requests must refer to "processor" field on a request.
     */
    public String getName();

    /**
     * Given the context (which contains the RequestMessage), return back a Consumer function that will be
     * executed with the context.  A typical implementation will simply check the "op" field on the RequestMessage
     * and return the Consumer function for that particular operation.
     */
    public ConsumerThatThrows<Context> select(final Context ctx) throws OpProcessorException;

    @FunctionalInterface
    public interface ConsumerThatThrows<Context> {
        public void accept(final Context ctx) throws OpProcessorException;
    }
}
