use boss;

delete from address;

delete from attentioninfo;

delete from backgoods;

delete from backgoodsitem;

delete from backgoodslog;

delete from brand;

delete from brandpicture;

delete from browsinghistory;

delete from buginfo;

delete from cart;

delete from cartitem;

delete from categoryassociation;

delete from categoryproperty;

delete from categorypropertyvalue;

delete from consultation;

delete from coupon;

delete from couponset;

delete from customer;

delete from customeraccount;

delete from html;

delete from invoicecompany;

delete from limitedtimediscount;

delete from logistics;

delete from logisticsinfo;

delete from lottery;

delete from mealitem;

delete from mealset;

delete from messagetask;

delete from navcategoryproperty;

delete from navcategorypropertyvalue;

delete from navigatecategory;

delete from navigatecategorydetail;

delete from orderitem;

delete from ordermessage;

delete from orderstatehistory;

delete from ordertable;

delete from product;

delete from productactivity;

delete from productcategory;

delete from productcollect;

delete from productintegralconversion;

delete from productofplatform;

delete from productpicture;

delete from productpicture_backup;

delete from productproperty;

delete from productstorage;

delete from productsuperconversion;

delete from property;

delete from propertyvaluedetail;

delete from questionnaire_answer;

delete from questionnaire_question;

delete from questionnaire_result;

delete from questionnaire_result_detail;

delete from questionnaire_survey;

delete from recommendproduct;

delete from refundtrade;

delete from refundtradeorder;

delete from rotary;

delete from rotarymeed;

delete from seo;

update shop set name = 'Boobee商城';

update shopdomain set domainName = 'Boobee';

delete from skustorage;

delete from skutraderesult;

delete from spacepicture;

delete from spaceproperty where spaceName != '默认';

delete from statisticsentry;

delete from stockkeepingunit;

delete from subscriptioninfo;

delete from systemlog;

delete from t_import_valuation;

delete from t_order_gift;

delete from trade;

delete from tradeorder;

delete from umpayrequest;

delete from unsubscribe;

delete from user;

delete from userdata;

delete from usergradehistory;

delete from usergraderule;

delete from userouter;

delete from userpoint;

delete from usersession;

delete from usersignin;

delete from valuation;

delete from value;

delete from virtualorder;

delete from weibo;

delete from weibobuy;

delete from weibotoken;

delete from employee.account where id != 1;

delete from employee.employee_boss;

delete from account_role where userId != 1;