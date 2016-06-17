
CREATE TABLE IF NOT EXISTS att_error_desc
(
att_error_desc_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
error_desc VARCHAR(255) UNIQUE NOT NULL
) ENGINE=MyISAM COMMENT='Error Description Table';

ALTER TABLE att_scalar_devdouble_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devdouble_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devdouble_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devdouble_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devlong_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devlong_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devlong_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devlong_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devboolean_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devboolean_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devboolean_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devboolean_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devuchar_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devuchar_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devuchar_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devuchar_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devshort_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devshort_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devshort_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devshort_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devushort_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devushort_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devushort_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devushort_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devulong_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devulong_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devulong_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devulong_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devlong64_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devlong64_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devlong64_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devlong64_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devulong64_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devulong64_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devulong64_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devulong64_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devfloat_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devfloat_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devfloat_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devfloat_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devstring_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devstring_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devstring_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devstring_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devstate_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devstate_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devstate_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devstate_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;

ALTER TABLE att_scalar_devencoded_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_scalar_devencoded_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devencoded_ro ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;
ALTER TABLE att_array_devencoded_rw ADD COLUMN att_error_desc_id INT UNSIGNED NULL DEFAULT NULL;


INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devdouble_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devdouble_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devdouble_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devdouble_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devlong_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devlong_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devlong_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devlong_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devboolean_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devboolean_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devboolean_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devboolean_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devuchar_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devuchar_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devuchar_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devuchar_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devshort_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devshort_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devshort_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devshort_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devushort_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devushort_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devushort_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devushort_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devulong_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devulong_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devulong_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devulong_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devlong64_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devlong64_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devlong64_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devlong64_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devulong64_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devulong64_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devulong64_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devulong64_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devfloat_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devfloat_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devfloat_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devfloat_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devstring_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devstring_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devstring_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devstring_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devstate_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devstate_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devstate_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devstate_rw t WHERE t.error_desc IS NOT NULL;

INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devencoded_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_scalar_devencoded_rw t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devencoded_ro t WHERE t.error_desc IS NOT NULL;
INSERT IGNORE INTO att_error_desc (error_desc) SELECT error_desc FROM att_array_devencoded_rw t WHERE t.error_desc IS NOT NULL;



UPDATE att_scalar_devdouble_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devdouble_ro.error_desc SET att_scalar_devdouble_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devdouble_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devdouble_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devdouble_rw.error_desc SET att_scalar_devdouble_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devdouble_rw.error_desc IS NOT NULL;
UPDATE att_array_devdouble_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devdouble_ro.error_desc SET att_array_devdouble_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devdouble_ro.error_desc IS NOT NULL;
UPDATE att_array_devdouble_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devdouble_rw.error_desc SET att_array_devdouble_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devdouble_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devlong_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong_ro.error_desc SET att_scalar_devlong_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devlong_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong_rw.error_desc SET att_scalar_devlong_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong_rw.error_desc IS NOT NULL;
UPDATE att_array_devlong_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong_ro.error_desc SET att_array_devlong_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong_ro.error_desc IS NOT NULL;
UPDATE att_array_devlong_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong_rw.error_desc SET att_array_devlong_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devboolean_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devboolean_ro.error_desc SET att_scalar_devboolean_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devboolean_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devboolean_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devboolean_rw.error_desc SET att_scalar_devboolean_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devboolean_rw.error_desc IS NOT NULL;
UPDATE att_array_devboolean_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devboolean_ro.error_desc SET att_array_devboolean_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devboolean_ro.error_desc IS NOT NULL;
UPDATE att_array_devboolean_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devboolean_rw.error_desc SET att_array_devboolean_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devboolean_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devuchar_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devuchar_ro.error_desc SET att_scalar_devuchar_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devuchar_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devuchar_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devuchar_rw.error_desc SET att_scalar_devuchar_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devuchar_rw.error_desc IS NOT NULL;
UPDATE att_array_devuchar_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devuchar_ro.error_desc SET att_array_devuchar_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devuchar_ro.error_desc IS NOT NULL;
UPDATE att_array_devuchar_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devuchar_rw.error_desc SET att_array_devuchar_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devuchar_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devshort_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devshort_ro.error_desc SET att_scalar_devshort_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devshort_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devshort_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devshort_rw.error_desc SET att_scalar_devshort_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devshort_rw.error_desc IS NOT NULL;
UPDATE att_array_devshort_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devshort_ro.error_desc SET att_array_devshort_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devshort_ro.error_desc IS NOT NULL;
UPDATE att_array_devshort_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devshort_rw.error_desc SET att_array_devshort_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devshort_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devushort_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devushort_ro.error_desc SET att_scalar_devushort_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devushort_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devushort_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devushort_rw.error_desc SET att_scalar_devushort_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devushort_rw.error_desc IS NOT NULL;
UPDATE att_array_devushort_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devushort_ro.error_desc SET att_array_devushort_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devushort_ro.error_desc IS NOT NULL;
UPDATE att_array_devushort_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devushort_rw.error_desc SET att_array_devushort_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devushort_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devulong_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devulong_ro.error_desc SET att_scalar_devulong_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devulong_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devulong_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devulong_rw.error_desc SET att_scalar_devulong_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devulong_rw.error_desc IS NOT NULL;
UPDATE att_array_devulong_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devulong_ro.error_desc SET att_array_devulong_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devulong_ro.error_desc IS NOT NULL;
UPDATE att_array_devulong_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devulong_rw.error_desc SET att_array_devulong_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devulong_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devlong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong64_ro.error_desc SET att_scalar_devlong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong64_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devlong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong64_rw.error_desc SET att_scalar_devlong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong64_rw.error_desc IS NOT NULL;
UPDATE att_array_devlong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong64_ro.error_desc SET att_array_devlong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong64_ro.error_desc IS NOT NULL;
UPDATE att_array_devlong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong64_rw.error_desc SET att_array_devlong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong64_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devlong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong64_ro.error_desc SET att_scalar_devlong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong64_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devlong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devlong64_rw.error_desc SET att_scalar_devlong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devlong64_rw.error_desc IS NOT NULL;
UPDATE att_array_devlong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong64_ro.error_desc SET att_array_devlong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong64_ro.error_desc IS NOT NULL;
UPDATE att_array_devlong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devlong64_rw.error_desc SET att_array_devlong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devlong64_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devulong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devulong64_ro.error_desc SET att_scalar_devulong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devulong64_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devulong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devulong64_rw.error_desc SET att_scalar_devulong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devulong64_rw.error_desc IS NOT NULL;
UPDATE att_array_devulong64_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devulong64_ro.error_desc SET att_array_devulong64_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devulong64_ro.error_desc IS NOT NULL;
UPDATE att_array_devulong64_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devulong64_rw.error_desc SET att_array_devulong64_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devulong64_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devfloat_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devfloat_ro.error_desc SET att_scalar_devfloat_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devfloat_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devfloat_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devfloat_rw.error_desc SET att_scalar_devfloat_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devfloat_rw.error_desc IS NOT NULL;
UPDATE att_array_devfloat_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devfloat_ro.error_desc SET att_array_devfloat_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devfloat_ro.error_desc IS NOT NULL;
UPDATE att_array_devfloat_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devfloat_rw.error_desc SET att_array_devfloat_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devfloat_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devstring_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devstring_ro.error_desc SET att_scalar_devstring_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devstring_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devstring_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devstring_rw.error_desc SET att_scalar_devstring_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devstring_rw.error_desc IS NOT NULL;
UPDATE att_array_devstring_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devstring_ro.error_desc SET att_array_devstring_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devstring_ro.error_desc IS NOT NULL;
UPDATE att_array_devstring_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devstring_rw.error_desc SET att_array_devstring_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devstring_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devstate_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devstate_ro.error_desc SET att_scalar_devstate_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devstate_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devstate_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devstate_rw.error_desc SET att_scalar_devstate_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devstate_rw.error_desc IS NOT NULL;
UPDATE att_array_devstate_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devstate_ro.error_desc SET att_array_devstate_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devstate_ro.error_desc IS NOT NULL;
UPDATE att_array_devstate_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devstate_rw.error_desc SET att_array_devstate_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devstate_rw.error_desc IS NOT NULL;

UPDATE att_scalar_devencoded_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devencoded_ro.error_desc SET att_scalar_devencoded_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devencoded_ro.error_desc IS NOT NULL;
UPDATE att_scalar_devencoded_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_scalar_devencoded_rw.error_desc SET att_scalar_devencoded_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_scalar_devencoded_rw.error_desc IS NOT NULL;
UPDATE att_array_devencoded_ro INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devencoded_ro.error_desc SET att_array_devencoded_ro.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devencoded_ro.error_desc IS NOT NULL;
UPDATE att_array_devencoded_rw INNER JOIN att_error_desc ON att_error_desc.error_desc = att_array_devencoded_rw.error_desc SET att_array_devencoded_rw.att_error_desc_id = att_error_desc.att_error_desc_id WHERE att_array_devencoded_rw.error_desc IS NOT NULL;



ALTER TABLE att_scalar_devdouble_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devdouble_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devdouble_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devdouble_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devlong_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devlong_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devlong_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devlong_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devboolean_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devboolean_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devboolean_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devboolean_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devuchar_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devuchar_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devuchar_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devuchar_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devshort_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devshort_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devshort_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devshort_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devushort_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devushort_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devushort_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devushort_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devulong_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devulong_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devulong_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devulong_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devlong64_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devlong64_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devlong64_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devlong64_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devlong64_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devlong64_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devlong64_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devlong64_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devulong64_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devulong64_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devulong64_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devulong64_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devfloat_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devfloat_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devfloat_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devfloat_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devstring_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devstring_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devstring_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devstring_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devstate_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devstate_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devstate_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devstate_rw DROP COLUMN error_desc;

ALTER TABLE att_scalar_devencoded_ro DROP COLUMN error_desc;
ALTER TABLE att_scalar_devencoded_rw DROP COLUMN error_desc;
ALTER TABLE att_array_devencoded_ro DROP COLUMN error_desc;
ALTER TABLE att_array_devencoded_rw DROP COLUMN error_desc;

