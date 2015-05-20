package dtos;

import java.util.List;

/**
 * Created by liubin on 15-5-18.
 */
public class SkuCandidate {

    private SkuProp skuProp;

    private List<SkuValue> skuValueList;

    public SkuCandidate(SkuProp skuProp, List<SkuValue> skuValueList) {
        this.skuProp = skuProp;
        this.skuValueList = skuValueList;
    }

    public SkuProp getSkuProp() {
        return skuProp;
    }

    public List<SkuValue> getSkuValueList() {
        return skuValueList;
    }

    public static class SkuProp {

        private Integer id;

        private String value;

        private int priority;

        public SkuProp(Integer id, String value, int priority) {
            this.id = id;
            this.value = value;
            this.priority = priority;
        }

        public Integer getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public int getPriority() {
            return priority;
        }
    }

    public static class SkuValue {

        private Integer id;

        private String value;

        private long pidvid;

        private int priority;

        public SkuValue(Integer id, String value, long pidvid, int priority) {
            this.id = id;
            this.value = value;
            this.pidvid = pidvid;
            this.priority = priority;
        }

        public Integer getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public long getPidvid() {
            return pidvid;
        }

        public int getPriority() {
            return priority;
        }
    }


}
