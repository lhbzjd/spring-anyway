package ink.anyway.component.web.filter;

import ink.anyway.component.web.function.SiteMeshBuilderFunction;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 16-11-30 下午1:26
 * <br>@version : 1.0
 */
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {

    /**
     * https://github.com/leelance/spring-boot-all/tree/master/spring-boot-sitemesh
     *
     *
     * http://www.cnblogs.com/luotaoyeah/p/3776879.html
     */

    private SiteMeshBuilderFunction siteMeshBuilderFunction;

    public SiteMeshFilter(SiteMeshBuilderFunction siteMeshBuilderFunction){
        this.siteMeshBuilderFunction = siteMeshBuilderFunction;
    }

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        siteMeshBuilderFunction.handleBuilder(builder);
    }

}
