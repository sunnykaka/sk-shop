package productcenter.util;

/**
 * 处理PidVid的工具类，pidvid用一个64位的long型数据表示
 * 这个工具类负责把pidvid合并为long和从一个long拆开成pid,vid
 *
 * User: lidujun
 * Date: 2015-04-27
 */
public class PropertyValueUtil {

    /**
     * 根据属性ID和值ID生成long,高32位为属性ID，低32位为值ID
     *
     * @param propertyId
     * @param valueId
     * @return
     */
    public static long mergePidVidToLong(int propertyId, int valueId) {
        return ((long) propertyId << 32) + (long) valueId;
    }

    /**
     * 将long型的pidvid转换为int的pid和vid对，数组中第0个元素表示propertyId，第1个元素
     * valueId
     *
     * @param pidvid
     * @return
     */
    public static PV parseLongToPidVid(long pidvid) {
        int[] result = new int[2];
        result[0] = (int) (pidvid >> 32); //得到高32位
        result[1] = (int) pidvid;         //得到低32位
        return new PV(result[0], result[1]);
    }


    public static class PV {

        public PV(int pid, int vid) {
            this.pid = pid;
            this.vid = vid;
        }

        public int pid;

        public int vid;
    }

    public static void main(String[] age){
        int pid = 71;
        int vid = 11951;
        long PVid = PropertyValueUtil.mergePidVidToLong(pid,vid);
        System.out.println("pvid = " + PVid);
        PV pv = PropertyValueUtil.parseLongToPidVid(PVid);
        System.out.println("pid = " + pv.pid + ";vid = " + pv.vid);
    }

}
