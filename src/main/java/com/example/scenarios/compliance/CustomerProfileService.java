package com.example.scenarios.compliance;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Consumes an <em>optional</em> conditional bean. {@link ObjectProvider} lets the same code run in
 * every region: the audit trail is used where it exists and silently skipped elsewhere.
 */
@Service
public class CustomerProfileService {

    private final ObjectProvider<GdprAuditTrail> auditTrail;

    public CustomerProfileService(ObjectProvider<GdprAuditTrail> auditTrail) {
        this.auditTrail = auditTrail;
    }

    public CustomerProfile load(String customerId, String purpose) {
        auditTrail.ifAvailable(trail -> trail.recordAccess(customerId, purpose));
        return new CustomerProfile(customerId, "Customer " + customerId);
    }

    public boolean isAuditing() {
        return auditTrail.getIfAvailable() != null;
    }
}
