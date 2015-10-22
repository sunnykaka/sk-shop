update ordertable set client = 'WEB' where client = 'Browser';

update ordertable set client = 'iOS' where client = 'IOSApp';

update trade set client = 'WEB' where client = 'Browser';

update trade set client = 'iOS' where client = 'IOSApp';