package edu.fscj.cen4940.capstone.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TempPasswordResetStorage {

    private final Map<String, TempPasswordReset> store = new ConcurrentHashMap<>();

    private TempPasswordResetStorage() {}

    private static final TempPasswordResetStorage INSTANCE = new TempPasswordResetStorage();

    public static TempPasswordResetStorage getInstance() {
        return INSTANCE;
    }

    public void save(String email, TempPasswordReset temp) {
        store.put(email, temp);
    }

    public TempPasswordReset get(String email) {
        return store.get(email);
    }

    public void remove(String email) {
        store.remove(email);
    }
}
