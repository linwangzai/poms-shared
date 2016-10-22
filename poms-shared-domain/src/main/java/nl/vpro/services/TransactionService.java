package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Please note that transactions are only rolled back in case of a runtime exception
 * @author Danny Sedney
 */
public interface TransactionService {
    /**
     * Run callable in new transaction. Transaction is rolled back ONLY in case of a runtime exception
     * @param callable The jobs to be processed in the new transaction
     * @return The result of the callable
     * @throws Exception The exception thrown by the callable
     */
    <T> T executeInNewTransaction(Callable<T> callable) throws Exception;

    <T> T getInNewTransaction(Supplier<T> supplier);

    void executeInNewTransaction(Runnable runnable);

    <T, S> T executeInNewTransaction(S argument, Function<S, T> function);

    <T> void executeInNewTransaction(T argument, Consumer<T> consumer);


    <T> T executeInReadonlyTransaction(Callable<T> callable) throws Exception;

    <T> T getInReadonlyTransaction(Supplier<T> supplier);

    void executeInReadonlyTransaction(Runnable runnable);

    <T, S> T executeInReadonlyTransaction(S argument, Function<S, T> function);

    <T> void executeInReadonlyTransaction(T argument, Consumer<T> consumer);


}
