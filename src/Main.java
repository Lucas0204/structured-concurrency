import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.Joiner.allSuccessfulOrThrow;

public class Main {
    public static void main(String[] args) {
        RequestContext.create("12345", () -> {
            try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow(),
                    config -> config.withTimeout(Duration.ofSeconds(5)))) {
                var subtask1 = scope.fork(Task.returning("T1", () -> "Valor de T1").delay(3).verbose());
                var subtask2 = scope.fork(Task.failing("T2").delay(1).verbose());
                var subtask3 = scope.fork(Task.returning("T3", () -> "Valor de T3").verbose());

                try {
                    scope.join();
                    var valorT1 = subtask1.get();
                    var valorT3 = subtask3.get();
                    System.out.println("T1=" + valorT1);
                    System.out.println("T3=" + valorT3);
                } catch (Exception e) {
                    System.out.println("Interrompido");
                }
            }

            System.out.println("Terminou!");
        });

        System.out.println("User Id out of context: " + RequestContext.sessionUserId());
    }
}
