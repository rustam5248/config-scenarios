package com.example.scenarios.outbox;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Runs while Spring parses configuration classes, before any bean exists. It receives the metadata
 * of the class annotated with {@link EnableOutbox} and returns the configuration classes to import:
 * always the core, plus the storage selected by the annotation attribute.
 */
public class OutboxImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        OutboxStore store = importingClassMetadata.getAnnotations()
                .get(EnableOutbox.class)
                .getEnum("store", OutboxStore.class);

        String storageConfiguration = switch (store) {
            case JDBC -> OutboxConfigurations.Jdbc.class.getName();
            case IN_MEMORY -> OutboxConfigurations.InMemory.class.getName();
        };
        return new String[] {OutboxConfigurations.Core.class.getName(), storageConfiguration};
    }
}
