CREATE TABLE IF NOT EXISTS ROLES (
    name VARCHAR(50) NOT NULL PRIMARY KEY,
    description VARCHAR(255)
    );

-- Thêm các vai trò mặc định
INSERT INTO ROLES (name, description) VALUES ('admin', 'Quản trị toàn bộ quyền');
INSERT INTO ROLES (name, description) VALUES ('employee', 'Nhân viên với một vài quyền');
INSERT INTO ROLES (name, description) VALUES ('user', 'Người dùng cơ bản');
