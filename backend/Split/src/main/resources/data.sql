INSERT IGNORE INTO user
(`email`, `password`, `gender`, `height`, `nickname`, `total_game_count`,
 `highlight`, `total_pose_highscore`, `total_pose_avgscore`, `elbow_angle_score`,
 `arm_stability_score`, `arm_speed_score`, `thema`)
VALUES
    ('user1@example.com', 'hashed_password_1', 1, 175, 'Bowler1', 10, '',
     95.50, 85.30, 78.40, 82.50, 90.20, 2),
    ('user2@example.com', 'hashed_password_2', 2, 160, 'Bowler2', 15,
     'https://s3.example.com/highlight2.mp4', 92.00, 88.00, 79.60, 85.00, 88.40, 1);

INSERT IGNORE INTO device (`serial_number`) VALUES (1);
INSERT IGNORE INTO device (`serial_number`) VALUES (2);
