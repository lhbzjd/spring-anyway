package ink.anyway.component.common.pojo;

import lombok.Data;

@Data
public class DecoratorPath {

    private String contentPath;

    private String decoratorPath;

    public DecoratorPath(String contentPath, String decoratorPath) {
        this.contentPath = contentPath;
        this.decoratorPath = decoratorPath;
    }
}
