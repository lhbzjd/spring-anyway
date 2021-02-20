package ink.anyway.component.web.function;

import org.sitemesh.builder.SiteMeshFilterBuilder;

@FunctionalInterface
public interface SiteMeshBuilderFunction {

    void handleBuilder(SiteMeshFilterBuilder builder);

}
