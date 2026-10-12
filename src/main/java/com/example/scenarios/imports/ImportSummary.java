package com.example.scenarios.imports;

import java.util.List;

public record ImportSummary(String jobId, int imported, List<String> rejectedLines) {
}
