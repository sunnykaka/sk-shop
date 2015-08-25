package api.response.user;

import usercenter.models.Designer;

/**
 * Created by liubin on 15-8-25.
 */
public class DesignerDto {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 快递公司
     */
    private String defaultLogistics;

    /**
     * 介绍
     */
    private String description;

    /**
     * 是否删除
     */
    private boolean isDelete;

    /**
     * 是否发布
     */
    private boolean isPublished;

    /**
     * 优先级
     */
    private Integer priority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultLogistics() {
        return defaultLogistics;
    }

    public void setDefaultLogistics(String defaultLogistics) {
        this.defaultLogistics = defaultLogistics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public static DesignerDto build(Designer designer) {
        if(designer == null) return null;
        DesignerDto designerDto = new DesignerDto();
        if(designer.getDefaultLogistics() != null) {
            designerDto.setDefaultLogistics(designer.getDefaultLogistics().name());
        }
        designerDto.setDelete(designer.getIsDelete());
        designerDto.setDescription(designer.getDescription());
        designerDto.setId(designer.getId());
        designerDto.setName(designer.getName());
        designerDto.setPriority(designer.getPriority());
        designerDto.setPublished(designer.getIsPublished());

        return designerDto;
    }
}
