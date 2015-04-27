package common.models.utils;

import org.joda.time.DateTime;

/**
 * User: zhb
 * Date: 15-4-24
 */
public interface TableTimeData {

    void setCreateDate(DateTime createDate);

    DateTime getCreateDate();

    default void setUpdateDate(DateTime updateDate) {}

    default DateTime getUpdateDate() {return null;}
}
