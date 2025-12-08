-- +FlywayNonTransactional

-- =============================================
-- جدول مسودات المهام (Task Drafts)
-- =============================================
CREATE TABLE task_draft (
                            id bigint NOT NULL AUTO_INCREMENT,
                            task_id int DEFAULT NULL,  -- غيرت من bigint إلى int لتتوافق مع جدول task
                            task_type varchar(50) NOT NULL,
                            user_id bigint NOT NULL,
                            draft_json longtext NOT NULL,
                            version int NOT NULL DEFAULT 1,
                            created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            is_active tinyint(1) DEFAULT 1,
                            draft_owner_name varchar(255) DEFAULT NULL,
                            PRIMARY KEY (id)
) ENGINE=INNODB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- =============================================
-- إضافة الفهارس لتحسين الأداء
-- =============================================
ALTER TABLE task_draft
    ADD INDEX idx_task_draft_user_id (user_id),
ADD INDEX idx_task_draft_task_id (task_id),
ADD INDEX idx_task_draft_task_type (task_type),
ADD INDEX idx_task_draft_active (is_active),
ADD INDEX idx_task_draft_created (created_at);

-- =============================================
-- إضافة القيود المرجعية (المصححة)
-- =============================================
ALTER TABLE task_draft
    ADD CONSTRAINT fk_task_draft_user
        FOREIGN KEY (user_id)
            REFERENCES sys_user(id)
            ON DELETE CASCADE ON UPDATE RESTRICT;

-- هذا القيد سيعمل الآن لأن task_id أصبح int مثل id في جدول task
ALTER TABLE task_draft
    ADD CONSTRAINT fk_task_draft_task
        FOREIGN KEY (task_id)
            REFERENCES task(id)
            ON DELETE SET NULL ON UPDATE RESTRICT;

-- =============================================
-- تعليقات الجدول للتوثيق
-- =============================================
ALTER TABLE task_draft
    COMMENT = 'جدول تخزين المسودات المؤقتة للمهام قبل حفظها النهائي';