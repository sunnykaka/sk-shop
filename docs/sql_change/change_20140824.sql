use boss;

ALTER TABLE ordertable
ADD COLUMN sendPayRemind BIT NOT NULL DEFAULT 0 AFTER brush;

ALTER TABLE recommendproduct
ADD COLUMN priority INT NOT NULL DEFAULT 0 AFTER recommendType;
