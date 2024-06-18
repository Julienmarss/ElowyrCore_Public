CREATE TABLE IF NOT EXISTS `{users}` (`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, `unique_id` VARCHAR(36) NOT NULL UNIQUE, `username` VARCHAR(255) NOT NULL, `serial_kill` INT NOT NULL, `fly_time` INT NOT NULL, `boost_delay` INT NOT NULL, `votes` INT NOT NULL);

CREATE TABLE IF NOT EXISTS `{boosts}` (`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, `user` BIGINT NOT NULL, `type` ENUM('EXPERIENCE', 'MONEY', 'POINTS') NOT NULL, `value` INT NOT NULL, `time` INT NOT NULL, FOREIGN KEY (`user`) REFERENCES `{users}`(`id`));

CREATE TABLE IF NOT EXISTS `{faction_classification}` (`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` VARCHAR(255) NOT NULL, `display` VARCHAR(255) NOT NULL, `pvp` DOUBLE NOT NULL, `farm` DOUBLE NOT NULL);

CREATE TABLE IF NOT EXISTS `{user_classification}` (`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `display` VARCHAR(255) NOT NULL, `points` DOUBLE NOT NULL, `kill` DOUBLE NOT NULL, `death` DOUBLE NOT NULL, `mob_kill` DOUBLE NOT NULL, `time_played` DOUBLE NOT NULL, `block_broken` DOUBLE NOT NULL, `votes` DOUBLE NOT NULL, `gambling` INT NOT NULL, `joyaux` INT NOT NULL, `votecount` INT NOT NULL);

CREATE TABLE IF NOT EXISTS `{aps}` (`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, `x` INT NOT NULL, `z` INT NOT NULL, `world` VARCHAR(36) NOT NULL, `price` BIGINT NOT NULL, `vip` TINYINT(1) NOT NULL, `owner-id` VARCHAR(36) UNIQUE, `date` BIGINT NOT NULL, `chunk` VARCHAR(255) UNIQUE);