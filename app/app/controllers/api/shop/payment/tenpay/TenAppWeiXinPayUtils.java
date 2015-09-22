package controllers.api.shop.payment.tenpay;

import ordercenter.models.Trade;
import ordercenter.payment.tenpay.TenpayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import play.Logger;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * 财付通app支付工具类
 * 已经在TenpayUtils中出现且可以公用的不包行在此util中
 *
 * @author lidujun
 * @version 1.0
 * @since 15-09-07
 */
public class TenAppWeiXinPayUtils {

    public static final String PAY_GATEWAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String KEY = "yezaoshu1234fashiongeekershangke";

    public static final String APPID = "wx56f8a533542cb7d7";

    public static final String MCH_ID = "1265042301";

    /**
     * 产生32位随机字符串
     * @return
     */
    public static String genNonceStr() {
        Random random = new Random();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return TenpayUtils.byteArrayToHexString(md.digest((random.nextInt(10000) + "").getBytes()));
        } catch (Exception ignored) {
            return UUID.randomUUID().toString().trim().replaceAll("-", "");
        }
    }

    public static String buildSign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Set es = params.entrySet();
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + TenAppWeiXinPayUtils.KEY);
        Logger.info("-----------key:" + sb.toString());
        return TenpayUtils.MD5Encode(sb.toString(), null).toUpperCase();
    }

    /**
     * 验证返回签名正确性
     * @param params
     * @return
     */
    public static boolean verify(Map<String, String> params) {
        String mysign = buildSign(params);
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        //验证签名
        return mysign.equals(sign);
    }

    /**
     * 构建app端需要的签名
     * @param xmlMap
     * @return
     */
    public static Map<String,String> buildAppSign(Map<String,String> xmlMap) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid",xmlMap.get("appid"));
        params.put("noncestr", TenAppWeiXinPayUtils.genNonceStr());
        params.put("package", "Sign=WXPay");
        params.put("partnerid", xmlMap.get("mch_id"));
        params.put("prepayid", xmlMap.get("prepay_id"));
        params.put("timestamp", getTimeStamp());
        params.put("sign", buildSign(params));
        return params;
    }

    /**
     * map to xml
     * 如：<xml><appid>wx2421b1c4370ec43b</appid> </xml>
     * @param map
     * @return
     */
    public static String map2Xml(Map map) {
        Document document = DocumentHelper.createDocument();
        Element nodeElement = document.addElement("xml");
        for (Object obj : map.keySet()) {
            Element keyElement = nodeElement.addElement(String.valueOf(obj));
            keyElement.setText(String.valueOf(map.get(obj)));
        }
        return doc2String(document);
    }

    /**
     *将Document转化为字符串
     * @param document
     * @return
     */
    public static String doc2String(Document document) {
        String s = "";
        try {
            // 使用输出流来进行转化
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputFormat format = new OutputFormat("   ", true);
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            //s = out.toString("UTF-8");   // 使用UTF-8编码
            s = out.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    /**
     * xml转换为map
     * @param xmlStr
     * @return
     */
    public static Map<String, String> xml2map(String xmlStr) {
        SortedMap<String, String> map = new TreeMap<String, String>();
        try {
            Document doc = DocumentHelper.parseText(xmlStr);
            Element rootElement = doc.getRootElement();
            List<Element> elements = rootElement.elements();
            for (Element ele : elements) {
                map.put(ele.getName(), ele.getTextTrim());
            }
        } catch (Exception e) {

        }
        return map;
    }

    /**
     * 设置支付返回代码 setXML("SUCCESS", "")
     * @param return_code
     * @param return_msg
     * @return
     */
    public static String setReturnInfoXml(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }

    /**
     * 获取时间戳
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 构建查询订单需要参数
     * @param trade
     * @return
     */
    public static Map<String, String> searchPayResult(Trade trade) {
        Map<String, String> param = new TreeMap();
        //公众账号ID
        param.put("appid", TenAppWeiXinPayUtils.APPID);
        // 商户号
        param.put("mch_id", TenAppWeiXinPayUtils.MCH_ID);

        param.put("transaction_id", trade.getOuterTradeNo());

        //随机字符串
        param.put("nonce_str", TenAppWeiXinPayUtils.genNonceStr());

        param.put("sign", TenAppWeiXinPayUtils.buildSign(param));

        String paramStr = TenAppWeiXinPayUtils.map2Xml(param); //xml字符

        String url = String.format("https://api.mch.weixin.qq.com/pay/orderquery");

        byte[] buf = TenPayHttpClientUtil.httpPost(url, paramStr);

        String content = new String(buf);
        Logger.info("查询订单支付返回内容: " + content);

        Map<String,String> xmlMap= TenAppWeiXinPayUtils.xml2map(content);
        if("SUCCESS".equalsIgnoreCase(xmlMap.get("return_code"))
                &&  "SUCCESS".equalsIgnoreCase(xmlMap.get("result_code"))) {
            //签名验证
            if(TenAppWeiXinPayUtils.verify(xmlMap)) {
                return xmlMap;
            } else {
                return null;
            }
        } else {
            Logger.info("错误代码:" + xmlMap.get("err_code"));
            Logger.info("错误代码描述:" + xmlMap.get("err_code_des"));
        }
        return null;
    }

    public static void main(String[] args) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid","wx56f8a533542cb7d7");
        params.put("bank_type","CFT");
        params.put("cash_fee","1");
        params.put("fee_type","CNY");

        params.put("is_subscribe","N");
        params.put("mch_id","1265042301");
        params.put("nonce_str","cfe8504bda37b575c70ee1a8276f3486");
        params.put("openid", "oHUz9v7_spkO8QnuGkn77CLn8KJo");

        params.put("out_trade_no", "144281442213810798");
        params.put("result_code", "SUCCESS");
        params.put("return_code", "SUCCESS");
        params.put("time_end", "20150914145721");
        params.put("total_fee", "1");
        params.put("trade_type", "APP");
        params.put("transaction_id", "1001290291201509140877435712");

        System.out.println("--------sign----------: " + buildSign(params));
    }

}
