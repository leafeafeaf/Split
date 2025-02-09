INSERT IGNORE INTO user
(`email`, `password`, `gender`, `height`, `nickname`, `total_game_count`,
 `highlight`, `total_pose_highscore`, `total_pose_avgscore`, `elbow_angle_score`,
 `arm_stability_score`, `arm_speed_score`, `thema`)
VALUES
    ('user1@example.com', 'hashed_password_1', 1, 175, 'Bowler1', 10, '',
     95.50, 85.30, 78.40, 82.50, 90.20, 2),
    ('user2@example.com', 'hashed_password_2', 2, 160, 'Bowler2', 15,
     'https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/highlight1.mp4', 92.00, 88.00, 79.60, 85.00, 88.40, 1);

INSERT IGNORE INTO device (`serial_number`) VALUES (1);
INSERT IGNORE INTO device (`serial_number`) VALUES (2);



INSERT IGNORE INTO game
(user_id, game_date, is_skip, pose_highscore, pose_lowscore, pose_avgscore, elbow_angle_score, arm_stability_score, arm_speed)
VALUES
    (1, '2024-02-06 10:00:00', 0, 95.50, 82.30, 88.70, 90.20, 87.50, 85.00),
    (2, '2024-02-06 10:30:00', 0, 94.80, 81.20, 87.90, 89.50, 86.80, 84.50),
    (3, '2024-02-06 11:00:00', 0, 96.20, 83.40, 89.60, 91.00, 88.20, 86.00);




INSERT IGNORE INTO game_rank
(game_id, user_id, nickname, highlight, total_game_count, game_date,
 pose_highscore, pose_losescore, pose_avgscore, elbow_angle_score, arm_stability_score, arm_speed)
VALUES
    (1, 1, 'Player1', 'https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/highlight1.mp4', 15, '2024-02-06 10:00:00', 95.50, 82.30, 88.70, 90.20, 87.50, 85.00),
    (2, 2, 'Player2', 'https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/highlight2.mp4', 20, '2024-02-06 10:30:00', 94.80, 81.20, 87.90, 89.50, 86.80, 84.50),
    (3, 3, 'Player3', 'https://split-bucket-first-1.s3.ap-northeast-2.amazonaws.com/highlight3.mp4', 25, '2024-02-06 11:00:00', 96.20, 83.40, 89.60, 91.00, 88.20, 86.00);