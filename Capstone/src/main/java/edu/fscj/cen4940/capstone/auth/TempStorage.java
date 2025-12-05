package edu.fscj.cen4940.capstone.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TempStorage {

    private final Map<String, TempRegistration> store = new ConcurrentHashMap<>();

    private TempStorage() {}

    private static final TempStorage INSTANCE = new TempStorage();

    public static TempStorage getInstance() {
        return INSTANCE;
    }

    public void save(String email, TempRegistration temp) {
        store.put(email, temp);
    }

    public TempRegistration get(String email) {
        return store.get(email);
    }

    public void remove(String email) {
        store.remove(email);
    }
}
