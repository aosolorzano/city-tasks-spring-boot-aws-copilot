INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (1, 'Task 1', 'Task 1 Description', 'ACT', 'abcd', '123', 'ACTIVATE', 12, 0, 'MON,WED,FRI', '2025-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (2, 'Task 2', 'Task 2 Description', 'INA', 'abcd', '456', 'ACTIVATE', 12, 30, 'TUE,FRI', '2026-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (3, 'Task 3', 'Task 3 Description', 'ACT', 'defg', '123', 'DEACTIVATE', 23, 0, 'THU,FRI', '2024-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (4, 'Task 4', 'Task 4 Description', 'ACT', 'hijk', '456', 'DEACTIVATE', 21, 15, 'TUE,WED', '2027-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;
