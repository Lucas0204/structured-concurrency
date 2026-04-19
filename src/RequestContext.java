public class RequestContext {
    private static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    public static String sessionUserId() {
        return USER_ID.isBound() ? USER_ID.get() : null;
    }

    public static void create(String userId, Runnable action) {
        ScopedValue.where(USER_ID, userId).run(action);
    }
}
