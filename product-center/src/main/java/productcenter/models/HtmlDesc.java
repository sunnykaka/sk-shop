package productcenter.models;

import java.util.List;

/**
 * 对商品进行HTML描述
 * 这是内存模型被缓存起来
 * User: lidujun
 * Date: 2015-04-24
 */
public class HtmlDesc {

    private int productId;

    private List<Html> htmlList;

    /**
     * 判断是否包含了某个描述
     *
     * @param name
     * @return
     */
    public boolean hasHtml(String name) {
        for (Html html : htmlList) {
            if (html.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    public List<Html> getHtmlList() {
        return htmlList;
    }

    public void setHtmlList(List<Html> htmlList) {
        this.htmlList = htmlList;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
