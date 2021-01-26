package ink.anyway.component.common.plugin;

import ink.anyway.component.common.pojo.DecoratorPath;

public class CustomConfigPlugin {

    private Object ssoExtendFunction;

    private String[] ssoExcludes;

    private String[] securityAntMatchers;

    private String[] siteMeshUrlPatterns;

    private DecoratorPath[] siteMeshDecoratorPaths;

    private String[] siteMeshExcludedPaths;

    public Object getSsoExtendFunction() {
        return ssoExtendFunction;
    }

    public String[] getSsoExcludes() {
        return ssoExcludes;
    }

    public String[] getSecurityAntMatchers() {
        return securityAntMatchers;
    }

    public String[] getSiteMeshUrlPatterns() {
        return siteMeshUrlPatterns;
    }

    public DecoratorPath[] getSiteMeshDecoratorPaths() {
        return siteMeshDecoratorPaths;
    }

    public String[] getSiteMeshExcludedPaths() {
        return siteMeshExcludedPaths;
    }

    public static CustomConfigPlugin.Builder builder() {
        return new CustomConfigPlugin.Builder();
    }

    public static class Builder {

        private Object ssoExtendFunction;

        private String[] ssoExcludes;

        private String[] securityAntMatchers;

        private String[] siteMeshUrlPatterns;

        private DecoratorPath[] siteMeshDecoratorPaths;

        private String[] siteMeshExcludedPaths;

        private Builder() {}

        public CustomConfigPlugin build() {
            return new CustomConfigPlugin(this.ssoExtendFunction, this.ssoExcludes, this.securityAntMatchers, this.siteMeshUrlPatterns, this.siteMeshDecoratorPaths, this.siteMeshExcludedPaths);
        }

        public Builder setSsoExtendFunction(Object ssoExtendFunction) {
            this.ssoExtendFunction = ssoExtendFunction;
            return this;
        }

        public Builder setSsoExcludes(String... ssoExcludes) {
            this.ssoExcludes = ssoExcludes;
            return this;
        }

        public Builder setSecurityAntMatchers(String... securityAntMatchers) {
            this.securityAntMatchers = securityAntMatchers;
            return this;
        }

        public Builder setSiteMeshUrlPatterns(String... siteMeshUrlPatterns) {
            this.siteMeshUrlPatterns = siteMeshUrlPatterns;
            return this;
        }

        public Builder setSiteMeshDecoratorPaths(DecoratorPath... siteMeshDecoratorPaths) {
            this.siteMeshDecoratorPaths = siteMeshDecoratorPaths;
            return this;
        }

        public Builder setSiteMeshExcludedPaths(String... siteMeshExcludedPaths) {
            this.siteMeshExcludedPaths = siteMeshExcludedPaths;
            return this;
        }
    }

    private CustomConfigPlugin(Object ssoExtendFunction, String[] ssoExcludes, String[] securityAntMatchers, String[] siteMeshUrlPatterns, DecoratorPath[] siteMeshDecoratorPaths, String[] siteMeshExcludedPaths) {
        this.ssoExtendFunction = ssoExtendFunction;
        this.ssoExcludes = ssoExcludes;
        this.securityAntMatchers = securityAntMatchers;
        this.siteMeshUrlPatterns = siteMeshUrlPatterns;
        this.siteMeshDecoratorPaths = siteMeshDecoratorPaths;
        this.siteMeshExcludedPaths = siteMeshExcludedPaths;
    }
}
