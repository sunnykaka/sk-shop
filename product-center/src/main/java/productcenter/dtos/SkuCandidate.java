package productcenter.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by liubin on 15-5-18.
 */
public class SkuCandidate {

    private SkuProp skuProp;

    private List<SkuValue> skuValueList;

    @JsonCreator
    public SkuCandidate(
            @JsonProperty("skuProp") SkuProp skuProp,
            @JsonProperty("skuValueList") List<SkuValue> skuValueList) {
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

        @JsonCreator
        public SkuProp(
                @JsonProperty("id") Integer id,
                @JsonProperty("value") String value,
                @JsonProperty("priority") int priority) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SkuProp)) return false;

            SkuProp skuProp = (SkuProp) o;

            if (!id.equals(skuProp.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static class SkuValue {

        private Integer id;

        private String value;

        private long pidvid;

        private int priority;

        @JsonCreator
        public SkuValue(
                @JsonProperty("id") Integer id,
                @JsonProperty("value") String value,
                @JsonProperty("pidvid") long pidvid,
                @JsonProperty("priority") int priority) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SkuValue)) return false;

            SkuValue skuValue = (SkuValue) o;

            if (!id.equals(skuValue.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }


}
