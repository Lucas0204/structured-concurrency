import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface Task<V> extends Callable<V> {
    String name();

    default Task<V> delay(int seconds) {
        return new Task<>() {
            @Override
            public String name() {
                return Task.this.name();
            }

            @Override
            public V call() throws Exception {
                var result = Task.this.call();
                Thread.sleep(seconds * 1000L);
                return result;
            }
        };
    };

    default Task<V> verbose() {
        return new Task<>() {
            @Override
            public String name() {
                return Task.this.name();
            }

            @Override
            public V call() throws Exception {
                System.out.println(Thread.currentThread() + " " + name() + " [START] UserId=" + RequestContext.sessionUserId());
                try {
                    var result = Task.this.call();
                    System.out.println(name() + " [SUCCESS] " + result);
                    return result;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(name() + " [INTERRUPTED]");
                    return null;
                } catch (Exception e) {
                    System.out.println(name() + " [FAILED]");
                    throw e;
                }
            }
        };
    };

    static <T> Task<T> returning(String name, Supplier<T> supplier) {
        return new Task<>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public T call() {
                return supplier.get();
            }
        };
    };

    static Task<Void> failing(String name) {
        return new Task<>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Void call() throws Exception {
                throw new Exception("Eu sempre falho");
            }
        };
    }
}
