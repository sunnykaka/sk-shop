package api.response.product;


import usercenter.models.DesignerSize;

/**
 * Created by liubin on 15-8-21.
 */
public class DesignerSizeDto {

    private Integer id;

    //尺码名
    private String name;

    //提示
    private String prompt;

    //表格内容
    private String content;


    private boolean deleted;


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

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public static DesignerSizeDto build(DesignerSize designerSize) {
        if(designerSize == null) return null;
        DesignerSizeDto designerSizeDto = new DesignerSizeDto();
        designerSizeDto.setId(designerSize.getId());
        designerSizeDto.setContent(designerSize.getContent());
        designerSizeDto.setDeleted(designerSize.isDeleted());
        designerSizeDto.setName(designerSize.getName());
        designerSizeDto.setPrompt(designerSize.getPrompt());
        return designerSizeDto;
    }
}
