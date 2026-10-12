package com.example.scenarios.compliance;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Records every access to personal data. Required for EU/UK deployments only. */
@Component
@ConditionalOnRegion({"eu", "uk"})
public class GdprAuditTrail {

    private final List<String> entries = new CopyOnWriteArrayList<>();

    public void recordAccess(String subjectId, String purpose) {
        entries.add(subjectId + ":" + purpose);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
