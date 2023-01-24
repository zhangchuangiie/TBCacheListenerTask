SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `number` bigint NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  `snapshot` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `json` json NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60004 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `update_date`;
delimiter ;;
CREATE TRIGGER `update_date` AFTER UPDATE ON `user` FOR EACH ROW INSERT INTO user_listener (type, data_id, time) VALUES ("upt", new.id, NOW())
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `delete_data`;
delimiter ;;
CREATE TRIGGER `delete_data` AFTER DELETE ON `user` FOR EACH ROW INSERT INTO user_listener (type, data_id, time) VALUES ("del", old.id, NOW())
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `insert_data`;
delimiter ;;
CREATE TRIGGER `insert_data` AFTER INSERT ON `user` FOR EACH ROW INSERT INTO user_listener (type, data_id, time) VALUES ("add", new.id, NOW())
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
