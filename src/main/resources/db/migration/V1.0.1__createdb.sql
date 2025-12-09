--
-- ER/Studio Data Architect SQL Code Generation
-- Project :      smat_V0.0.3.DM1
--
-- Date Created : Monday, February 10, 2025 01:21:51
-- Target DBMS : MySQL 8.x
--

-- 
-- TABLE: access_by_job_position 
--

CREATE TABLE access_by_job_position(
    id                  BIGINT      NOT NULL,
    cabinet_folder      BIGINT,
    archive_document    BIGINT,
    cabinet             BIGINT,
    access_type         SMALLINT    NOT NULL,
    job_position        SMALLINT    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: access_by_user_alias 
--

CREATE TABLE access_by_user_alias(
    id                  BIGINT      NOT NULL,
    user_alias          BIGINT,
    archive_document    BIGINT,
    cabinet_folder      BIGINT,
    cabinet             BIGINT,
    access_type         SMALLINT    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: access_management_type 
--

CREATE TABLE access_management_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: access_type 
--

CREATE TABLE access_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_access_management 
--

CREATE TABLE archive_access_management(
    id                        BIGINT      AUTO_INCREMENT,
    access_management_type    SMALLINT    NOT NULL,
    created_date              DATE,
    created_by                BIGINT      NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_document 
--

CREATE TABLE archive_document(
    id                            BIGINT                    AUTO_INCREMENT,
    organization                  BIGINT,
    root_organization             BIGINT,
    divan                         BIGINT,
    parent_archive_document       BIGINT,
    from_entity                   NATIONAL VARCHAR(200),
    is_encrypted                  BIT(1),
    is_deleted                    BIT(1),
    seq                           DECIMAL(10, 0),
    year                          DECIMAL(4, 0),
    code                          NATIONAL VARCHAR(60),
    arabic_name                   NATIONAL VARCHAR(100),
    english_name                  NATIONAL VARCHAR(100),
    subject                       NATIONAL VARCHAR(200),
    description                   NATIONAL VARCHAR(2000),
    lang                          VARCHAR(20),
    is_directory                  BIT(1),
    creator_user                  BIGINT,
    created_date                  DATE,
    is_locked                     BIT(1),
    locked_sys_user               BIGINT,
    owner_alias                   BIGINT,
    inherit_mail_permission       BIT(1),
    inherit_parent_attribute      BIT(1),
    inherit_attribute_to_child    BIT(1),
    archive_document_type         SMALLINT                  NOT NULL,
    archive_extra_property        BIGINT,
    deleted_by_sys_user           BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_document_file 
--

CREATE TABLE archive_document_file(
    id                   BIGINT                    AUTO_INCREMENT,
    name                 NATIONAL VARCHAR(250),
    code                 NATIONAL VARCHAR(60),
    version              DECIMAL(2, 1),
    version_date         DATE,
    version_author       NATIONAL VARCHAR(100),
    version_rationale    NATIONAL VARCHAR(400),
    minor_version        BIT(1),
    major_version        BIT(1),
    extension            NATIONAL VARCHAR(50),
    mime_type            NATIONAL VARCHAR(150),
    uuid                 NATIONAL VARCHAR(40),
    file_size            DECIMAL(10, 0),
    created_date         DATE,
    logical_path         NATIONAL VARCHAR(2000),
    server_path          NATIONAL VARCHAR(2000),
    archive_document     BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_document_tag 
--

CREATE TABLE archive_document_tag(
    id                  BIGINT                   AUTO_INCREMENT,
    arabic_name         NATIONAL VARCHAR(100),
    english_name        NATIONAL VARCHAR(100),
    archive_document    BIGINT                   NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_document_type 
--

CREATE TABLE archive_document_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_extra_property 
--

CREATE TABLE archive_extra_property(
    id                           BIGINT                   AUTO_INCREMENT,
    arabic_template_name         NATIONAL VARCHAR(100)    NOT NULL,
    english_template_name        NATIONAL VARCHAR(100)    NOT NULL,
    template_json_description    VARBINARY(4000)          NOT NULL,
    database_entity_name         NATIONAL VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: archive_sharing 
--

CREATE TABLE archive_sharing(
    id                      BIGINT                    AUTO_INCREMENT,
    sharing_type            SMALLINT                  NOT NULL,
    sharing_expired_date    DATE,
    sharing_date            DATE,
    sharing_by              BIGINT                    NOT NULL,
    sharing_link            NATIONAL VARCHAR(2000),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: box_type 
--

CREATE TABLE box_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    ar_code         NATIONAL VARCHAR(50),
    en_code         NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet 
--

CREATE TABLE cabinet(
    id                     BIGINT                    AUTO_INCREMENT,
    arabic_name            NATIONAL VARCHAR(100),
    english_name           NATIONAL VARCHAR(100),
    arabic_description     NATIONAL VARCHAR(1000),
    english_description    NATIONAL VARCHAR(1000),
    code                   NATIONAL VARCHAR(50),
    cabinet_location       BIGINT                    NOT NULL,
    organization           BIGINT,
    user_alias             BIGINT,
    cabinet_type           SMALLINT                  NOT NULL,
    created_date           DATE,
    created_by             BIGINT                    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet_definition 
--

CREATE TABLE cabinet_definition(
    id                     BIGINT                   AUTO_INCREMENT,
    drawer_arabic_name     NATIONAL VARCHAR(200),
    drawer_english_name    NATIONAL VARCHAR(200),
    code                   NATIONAL VARCHAR(50),
    cabinet                BIGINT                   NOT NULL,
    drawer_order           DECIMAL(5, 0),
    created_date           DATE,
    created_by             BIGINT                   NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet_folder 
--

CREATE TABLE cabinet_folder(
    id                     BIGINT                    AUTO_INCREMENT,
    arabic_name            NATIONAL VARCHAR(200),
    english_name           NATIONAL VARCHAR(200),
    arabic_description     NATIONAL VARCHAR(1000),
    english_description    NATIONAL VARCHAR(1000),
    code                   NATIONAL VARCHAR(100),
    cabinet_definition     BIGINT                    NOT NULL,
    created_date           DATE,
    created_by             BIGINT                    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet_folder_document 
--

CREATE TABLE cabinet_folder_document(
    id                  BIGINT    AUTO_INCREMENT,
    archive_document    BIGINT    NOT NULL,
    cabinet_folder      BIGINT    NOT NULL,
    created_date        DATE,
    created_by          BIGINT    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet_location 
--

CREATE TABLE cabinet_location(
    id              BIGINT                   AUTO_INCREMENT,
    location        NATIONAL VARCHAR(300),
    is_default      BIT(1),
    created_date    DATE,
    sys_user        BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: cabinet_type 
--

CREATE TABLE cabinet_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: checklist_data_source 
--

CREATE TABLE checklist_data_source(
    id             SMALLINT                  AUTO_INCREMENT,
    name           NATIONAL VARCHAR(200)     NOT NULL,
    description    NATIONAL VARCHAR(2000)    NOT NULL,
    code           NATIONAL VARCHAR(50)      NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: checklist_detail_data_source 
--

CREATE TABLE checklist_detail_data_source(
    id                       INT                      AUTO_INCREMENT,
    checklist_data_source    SMALLINT                 NOT NULL,
    item_value               NATIONAL VARCHAR(200)    NOT NULL,
    item_code                NATIONAL VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: column_content 
--

CREATE TABLE column_content(
    id                        INT                      AUTO_INCREMENT,
    general_equipment_item    INT,
    form_column               INT,
    alias_name                NATIONAL VARCHAR(200),
    read_only                 BIT(1),
    disabled                  BIT(1),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: company 
--

CREATE TABLE company(
    id         INT                       AUTO_INCREMENT,
    name       NATIONAL VARCHAR(200),
    phone      NATIONAL VARCHAR(100),
    mobile     NATIONAL VARCHAR(100),
    email      NATIONAL VARCHAR(200),
    code       NATIONAL VARCHAR(50)      NOT NULL,
    address    NATIONAL VARCHAR(2000),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence 
--

CREATE TABLE correspondence(
    id                      BIGINT                   AUTO_INCREMENT,
    root_organization       BIGINT,
    organization            BIGINT,
    divan                   BIGINT,
    seq                     DECIMAL(10, 0),
    year                    DECIMAL(5, 0),
    correspondence_date     DATE,
    title                   NATIONAL VARCHAR(200),
    subject                 VARBINARY(4000),
    html_subject            VARBINARY(4000),
    from_number             NATIONAL VARCHAR(100),
    from_entity             NATIONAL VARCHAR(200),
    from_date               DATE,
    priority_type           SMALLINT,
    correspondece_type      SMALLINT,
    creator_sys_user        BIGINT,
    created_date            DATE,
    delete_date             DATE,
    owner_alias             BIGINT,
    box_type                SMALLINT,
    correspondence_state    SMALLINT,
    deleted_by_sys_user     BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_archive 
--

CREATE TABLE correspondence_archive(
    id                  BIGINT    AUTO_INCREMENT,
    archive_document    BIGINT,
    correspondence      BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_note 
--

CREATE TABLE correspondence_note(
    id                       BIGINT             AUTO_INCREMENT,
    note_date                DATE,
    note                     VARBINARY(4000),
    html_note                VARBINARY(4000),
    deleted                  BIT(1),
    deleted_date             DATE,
    to_archive               BIT(1),
    seq_by_correspondence    DECIMAL(5, 0),
    correspondence           BIGINT,
    from_alias               BIGINT,
    divan                    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_recipient 
--

CREATE TABLE correspondence_recipient(
    id                     BIGINT      AUTO_INCREMENT,
    is_viewed              BIT(1),
    viewed_date            DATE,
    is_archived            BIT(1),
    archived_date          DATE,
    is_processed           BIT(1),
    processing_date        DATE,
    to_alias               BIGINT,
    correspondence         BIGINT,
    purpose_type           SMALLINT,
    priority_type          SMALLINT,
    divan                  BIGINT,
    correspondence_note    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_state 
--

CREATE TABLE correspondence_state(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_task 
--

CREATE TABLE correspondence_task(
    id                BIGINT    AUTO_INCREMENT,
    correspondence    BIGINT    NOT NULL,
    task              INT       NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_transmission 
--

CREATE TABLE correspondence_transmission(
    id                   BIGINT      AUTO_INCREMENT,
    transmission_date    DATE,
    correspondence       BIGINT,
    from_alias           BIGINT,
    to_alias             BIGINT,
    transmission_type    SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: correspondence_type 
--

CREATE TABLE correspondence_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: document_sharing 
--

CREATE TABLE document_sharing(
    id                  BIGINT      NOT NULL,
    user_alias          BIGINT,
    organization        BIGINT,
    archive_document    BIGINT      NOT NULL,
    sharing_to_type     SMALLINT    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: effect_type 
--

CREATE TABLE effect_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100)    NOT NULL,
    english_name    NATIONAL VARCHAR(100)    NOT NULL,
    code            VARCHAR(50)              NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: equipment_category 
--

CREATE TABLE equipment_category(
    id                     SMALLINT                  AUTO_INCREMENT,
    arabic_name            NATIONAL VARCHAR(200),
    english_name           NATIONAL VARCHAR(200),
    arabic_description     NATIONAL VARCHAR(2000),
    english_description    NATIONAL VARCHAR(2000),
    code                   NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: equipment_inspection_certificate 
--

CREATE TABLE equipment_inspection_certificate(
    id                           BIGINT             AUTO_INCREMENT,
    issue_date                   DATE               NOT NULL,
    certificate_document         VARBINARY(4000)    NOT NULL,
    is_printed                   BIT(1),
    company                      INT                NOT NULL,
    equipment_inspection_form    BIGINT             NOT NULL,
    created_by                   BIGINT             NOT NULL,
    created_date                 DATE               NOT NULL,
    allow_reprint_cert           BIT(1),
    allow_reprint_by             BIGINT,
    reprint_by                   BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: equipment_inspection_form 
--

CREATE TABLE equipment_inspection_form(
    id                              BIGINT                   AUTO_INCREMENT,
    report_no                       NATIONAL VARCHAR(100),
    time_sheet_no                   NATIONAL VARCHAR(100),
    job_no                          NATIONAL VARCHAR(100),
    sticker_no                      NATIONAL VARCHAR(100),
    name_and_address_of_employer    VARCHAR(3000),
    date_of_thorough_examination    DATE,
    next_examination_date           DATE,
    previous_examination_date       DATE,
    examination_type                SMALLINT,
    equipment_type                  SMALLINT,
    equipment_category              SMALLINT,
    company                         INT,
    created_date                    datetime DEFAULT NULL,
    pinned_by                       bigint DEFAULT NULL,
    sticker                         BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: equipment_inspection_form_item 
--

CREATE TABLE equipment_inspection_form_item(
    id                           INT                      AUTO_INCREMENT,
    equipment_inspection_form    BIGINT                   NOT NULL,
    general_equipment_item       INT                      NOT NULL,
    alias_name                   NATIONAL VARCHAR(200),
    item_value                   NATIONAL VARCHAR(4000),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: equipment_type 
--

CREATE TABLE equipment_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(200),
    english_name    NATIONAL VARCHAR(200),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: examination_type 
--

CREATE TABLE examination_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(200),
    english_name    NATIONAL VARCHAR(200),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: extra_property_definition 
--

CREATE TABLE extra_property_definition(
    id                        BIGINT                   AUTO_INCREMENT,
    property_name             NATIONAL VARCHAR(100)    NOT NULL,
    property_type             SMALLINT                 NOT NULL,
    archive_extra_property    BIGINT                   NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: folder_sharing 
--

CREATE TABLE folder_sharing(
    id                 BIGINT      NOT NULL,
    user_alias         BIGINT,
    organization       BIGINT,
    cabinet_folder     BIGINT      NOT NULL,
    sharing_to_type    SMALLINT    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: form_column 
--

CREATE TABLE form_column(
    id              INT                      AUTO_INCREMENT,
    form_row        INT                      NOT NULL,
    col_span        SMALLINT,
    row_span        SMALLINT,
    style           NATIONAL VARCHAR(500),
    style_class     NATIONAL VARCHAR(200),
    column_order    SMALLINT                 NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: form_row 
--

CREATE TABLE form_row(
    id               INT         AUTO_INCREMENT,
    form_template    INT         NOT NULL,
    is_header        BIT(1),
    is_footer        BIT(1),
    row_num          SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: form_template 
--

CREATE TABLE form_template(
    id                    INT                       AUTO_INCREMENT,
    title                 NATIONAL VARCHAR(2000)    NOT NULL,
    description           NATIONAL VARCHAR(2000),
    created_date          DATE,
    equipment_category    SMALLINT,
    sys_user              BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: general_equipment_item 
--

CREATE TABLE general_equipment_item(
    id             INT                      AUTO_INCREMENT,
    item_text      NATIONAL VARCHAR(200),
    item_code      NATIONAL VARCHAR(50),
    for_item            NATIONAL VARCHAR(50),
    item_type      SMALLINT                 NOT NULL,
    data_source    SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: group_member 
--

CREATE TABLE group_member(
    id                   BIGINT      AUTO_INCREMENT,
    created_date         DATE,
    is_disabled          BIT(1),
    working_group        SMALLINT,
    creator_sys_user     BIGINT,
    member_user_alias    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: hierarchy_system 
--

CREATE TABLE hierarchy_system(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100)    NOT NULL,
    english_name    NATIONAL VARCHAR(100)    NOT NULL,
    code            VARCHAR(50)              NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: hierarchy_system_def 
--

CREATE TABLE hierarchy_system_def(
    id                  SMALLINT                 AUTO_INCREMENT,
    arabic_name         NATIONAL VARCHAR(100)    NOT NULL,
    english_name        NATIONAL VARCHAR(100)    NOT NULL,
    code                VARCHAR(50),
    hierarchy_system    SMALLINT                 NOT NULL,
    hierarchy_type      SMALLINT                 NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: hierarchy_type 
--

CREATE TABLE hierarchy_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100)    NOT NULL,
    english_name    NATIONAL VARCHAR(100)    NOT NULL,
    code            VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: inspection_form_workflow 
--

CREATE TABLE inspection_form_workflow(
    id                           INT                AUTO_INCREMENT,
    equipment_inspection_form    BIGINT             NOT NULL,
    workflow_definition          INT                NOT NULL,
    inspection_form_document     VARBINARY(4000)    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: inspection_form_workflow_step 
--

CREATE TABLE inspection_form_workflow_step(
    id                           BIGINT                   AUTO_INCREMENT,
    equipment_inspection_form    BIGINT                   NOT NULL,
    workflow_definition          INT                      NOT NULL,
    inspection_form_document     VARBINARY(4000)          NOT NULL,
    sys_user_comment             NATIONAL VARCHAR(200)    NOT NULL,
    sys_user                     BIGINT                   NOT NULL,
    process_date                 DATE                     NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: item_type 
--

CREATE TABLE item_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(200),
    english_name    NATIONAL VARCHAR(200),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: job_position 
--

CREATE TABLE job_position(
    id                 SMALLINT                 AUTO_INCREMENT,
    arabic_name        NATIONAL VARCHAR(100),
    english_name       NATIONAL VARCHAR(100),
    code               NATIONAL VARCHAR(50),
    defined_level      DECIMAL(4, 0),
    low_send_level     DECIMAL(4, 0),
    high_send_level    DECIMAL(4, 0),
    effect_type        SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: job_position_hierarchy 
--

CREATE TABLE job_position_hierarchy(
    id                BIGINT           AUTO_INCREMENT,
    job_position      SMALLINT         NOT NULL,
    hierarchy_type    SMALLINT         NOT NULL,
    ranking           DECIMAL(5, 0)    NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: linked_correspondence 
--

CREATE TABLE linked_correspondence(
    id                       BIGINT    AUTO_INCREMENT,
    master_correspondence    BIGINT,
    linked_correspondence    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: notification 
--

CREATE TABLE notification(
    id                     BIGINT                    AUTO_INCREMENT,
    title                  NATIONAL VARCHAR(200),
    subject                NATIONAL VARCHAR(2000),
    notification_period    DECIMAL(10, 2),
    notification_type      SMALLINT,
    correspondence         BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: notification_setting 
--

CREATE TABLE notification_setting(
    id                    SMALLINT    AUTO_INCREMENT,
    notification_style    SMALLINT,
    period_type           SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: notification_style 
--

CREATE TABLE notification_style(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: notification_type 
--

CREATE TABLE notification_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: organization 
--

CREATE TABLE organization(
    id                       BIGINT                   AUTO_INCREMENT,
    parent_organization      BIGINT,
    arabic_name              NATIONAL VARCHAR(100),
    english_name             NATIONAL VARCHAR(100),
    code                     NATIONAL VARCHAR(50),
    prefix_code              NATIONAL VARCHAR(50),
    suffix_code              NATIONAL VARCHAR(50),
    is_divan                 BIT(1),
    has_private_hierarchy    BIT(1)                   NOT NULL,
    hierarchy_system         SMALLINT,
    hierarchy_system_def     SMALLINT                 NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: period_type 
--

CREATE TABLE period_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: priority_type 
--

CREATE TABLE priority_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: property_type 
--

CREATE TABLE property_type(
    id             SMALLINT                 AUTO_INCREMENT,
    name           NATIONAL VARCHAR(100),
    description    NATIONAL VARCHAR(500),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: purpose_type 
--

CREATE TABLE purpose_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: service_type 
--

CREATE TABLE service_type(
    id                     SMALLINT                  AUTO_INCREMENT,
    arabic_name            NATIONAL VARCHAR(200)     NOT NULL,
    english_name           NATIONAL VARCHAR(200)     NOT NULL,
    code                   NATIONAL VARCHAR(50)      NOT NULL,
    arabic_description     NATIONAL VARCHAR(2000),
    english_description    NATIONAL VARCHAR(2000),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sharing_to_type 
--

CREATE TABLE sharing_to_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sharing_type 
--

CREATE TABLE sharing_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: step 
--

CREATE TABLE step(
    id              SMALLINT        AUTO_INCREMENT,
    arabic_name     VARCHAR(200)    NOT NULL,
    english_name    VARCHAR(200)    NOT NULL,
    code            VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sticker 
--

CREATE TABLE sticker(
    id            BIGINT          AUTO_INCREMENT,
    seq           BIGINT,
    sticker_no    BIT(1),
    serial_no     VARCHAR(200),
    year          SMALLINT,
    is_printed    BIT(1),
    is_used       BIT(1),
    printed_by    BIGINT,
    created_by    BIGINT,
    deleted_by    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_permission 
--

CREATE TABLE sys_permission(
    id             SMALLINT                 AUTO_INCREMENT,
    name           NATIONAL VARCHAR(100),
    code           NATIONAL VARCHAR(50),
    description    NATIONAL VARCHAR(400),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_role 
--

CREATE TABLE sys_role(
    id             SMALLINT                 AUTO_INCREMENT,
    name           NATIONAL VARCHAR(100),
    code           NATIONAL VARCHAR(50),
    description    NATIONAL VARCHAR(400),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_role_permission 
--

CREATE TABLE sys_role_permission(
    id                SMALLINT    AUTO_INCREMENT,
    sys_permission    SMALLINT,
    sys_role          SMALLINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_user 
--

CREATE TABLE sys_user(
    id                             BIGINT                   AUTO_INCREMENT,
    first_name                     NATIONAL VARCHAR(100),
    last_name                      NATIONAL VARCHAR(100),
    mother_name                    NATIONAL VARCHAR(100),
    father_name                    NATIONAL VARCHAR(100),
    ar_display_name                NATIONAL VARCHAR(100),
    en_display_name                NATIONAL VARCHAR(100),
    user_name                      NATIONAL VARCHAR(100),
    password                       NATIONAL VARCHAR(100),
    pass_must_change_first_time    BIT(1),
    password_validity              DATE,
    pass_first_change_date         DATE,
    disabled                       BIT(1),
    disabled_until_date            DATE,
    organization                   BIGINT                   NOT NULL,
    root_organization              BIGINT                   NOT NULL,
    sys_user_code                  NATIONAL VARCHAR(50)     NOT NULL,
    is_super_admin                 BIT(1),
    is_admin                       BIT(1),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_user_role 
--

CREATE TABLE sys_user_role(
    id          BIGINT      AUTO_INCREMENT,
    sys_role    SMALLINT    NOT NULL,
    sys_user    BIGINT      NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: sys_user_setting 
--

CREATE TABLE sys_user_setting(
    id            BIGINT                   AUTO_INCREMENT,
    attr_key      NATIONAL VARCHAR(100),
    attr_value    NATIONAL VARCHAR(100),
    sys_user      BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: task 
--

CREATE TABLE task(
    id                    INT                       AUTO_INCREMENT,
    assigner              BIGINT                    NOT NULL,
    assign_to             BIGINT                    NOT NULL,
    company               INT,
    equipment_category    SMALLINT,
    task_description      NATIONAL VARCHAR(2000),
    is_completed          BIT(1),
    is_done               BIT(1),
    created_date          DATE                      NOT NULL,
    created_by            BIGINT                    NOT NULL,
    completed_date        DATE,
    done_date             DATE,
    service_type          SMALLINT                  NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: transmission_type 
--

CREATE TABLE transmission_type(
    id              SMALLINT                 AUTO_INCREMENT,
    arabic_name     NATIONAL VARCHAR(100),
    english_name    NATIONAL VARCHAR(100),
    code            NATIONAL VARCHAR(50),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: user_alias 
--

CREATE TABLE user_alias(
    id                   BIGINT                   AUTO_INCREMENT,
    ar_alias_name        NATIONAL VARCHAR(100),
    en_alias_name        NATIONAL VARCHAR(100),
    user_code            NATIONAL VARCHAR(50),
    is_default           BIT(1),
    job_position         SMALLINT,
    sys_user             BIGINT,
    root_organization    BIGINT,
    organization         BIGINT,
    divan                BIGINT,
    defined_level        DECIMAL(4, 0),
    low_send_level       DECIMAL(4, 0),
    high_send_level      DECIMAL(4, 0),
    weight               DECIMAL(3, 0),
    is_frozen            BIT(1),
    frozen_date          DATE,
    created_date         DATE,
    hierarchy_closure    BIT(1),
    creator_user         BIGINT,
    frozen_user          BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: workflow 
--

CREATE TABLE workflow(
    id              SMALLINT        AUTO_INCREMENT,
    arabic_name     VARCHAR(200)    NOT NULL,
    english_name    VARCHAR(200)    NOT NULL,
    code            VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: workflow_definition 
--

CREATE TABLE workflow_definition(
    id              INT         AUTO_INCREMENT,
    workflow        SMALLINT,
    step            SMALLINT,
    previous        INT,
    next            INT,
    initial_step    BIT(1),
    final_step      BIT(1),
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: working_group 
--

CREATE TABLE working_group(
    id                  SMALLINT                 AUTO_INCREMENT,
    arabic_name         NATIONAL VARCHAR(100),
    english_name        NATIONAL VARCHAR(100),
    code                NATIONAL VARCHAR(50),
    created_date        DATE,
    creator_sys_user    BIGINT,
    PRIMARY KEY (id)
)ENGINE=INNODB
;



-- 
-- TABLE: access_by_job_position 
--

ALTER TABLE access_by_job_position ADD CONSTRAINT Refarchive_access_management26 
    FOREIGN KEY (id)
    REFERENCES archive_access_management(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_job_position ADD CONSTRAINT Refjob_position27 
    FOREIGN KEY (job_position)
    REFERENCES job_position(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_job_position ADD CONSTRAINT Refaccess_type29 
    FOREIGN KEY (access_type)
    REFERENCES access_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_job_position ADD CONSTRAINT Refcabinet31 
    FOREIGN KEY (cabinet)
    REFERENCES cabinet(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_job_position ADD CONSTRAINT Refarchive_document33 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_job_position ADD CONSTRAINT Refcabinet_folder35 
    FOREIGN KEY (cabinet_folder)
    REFERENCES cabinet_folder(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: access_by_user_alias 
--

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refaccess_type36 
    FOREIGN KEY (access_type)
    REFERENCES access_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refcabinet38 
    FOREIGN KEY (cabinet)
    REFERENCES cabinet(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refcabinet_folder40 
    FOREIGN KEY (cabinet_folder)
    REFERENCES cabinet_folder(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refarchive_document42 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refuser_alias44 
    FOREIGN KEY (user_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE access_by_user_alias ADD CONSTRAINT Refarchive_access_management53 
    FOREIGN KEY (id)
    REFERENCES archive_access_management(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: archive_access_management 
--

ALTER TABLE archive_access_management ADD CONSTRAINT Refaccess_management_type134 
    FOREIGN KEY (access_management_type)
    REFERENCES access_management_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_access_management ADD CONSTRAINT Refsys_user135 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: archive_document 
--

ALTER TABLE archive_document ADD CONSTRAINT Refarchive_document_type45 
    FOREIGN KEY (archive_document_type)
    REFERENCES archive_document_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refarchive_extra_property47 
    FOREIGN KEY (archive_extra_property)
    REFERENCES archive_extra_property(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refsys_user55 
    FOREIGN KEY (deleted_by_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refarchive_document68 
    FOREIGN KEY (parent_archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Reforganization69 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Reforganization90 
    FOREIGN KEY (root_organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Reforganization91 
    FOREIGN KEY (divan)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refsys_user92 
    FOREIGN KEY (creator_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refsys_user93 
    FOREIGN KEY (locked_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_document ADD CONSTRAINT Refuser_alias94 
    FOREIGN KEY (owner_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: archive_document_file 
--

ALTER TABLE archive_document_file ADD CONSTRAINT Refarchive_document60 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: archive_document_tag 
--

ALTER TABLE archive_document_tag ADD CONSTRAINT Refarchive_document46 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: archive_sharing 
--

ALTER TABLE archive_sharing ADD CONSTRAINT Refsharing_type122 
    FOREIGN KEY (sharing_type)
    REFERENCES sharing_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE archive_sharing ADD CONSTRAINT Refsys_user130 
    FOREIGN KEY (sharing_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: cabinet 
--

ALTER TABLE cabinet ADD CONSTRAINT Reforganization110 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet ADD CONSTRAINT Refcabinet_location111 
    FOREIGN KEY (cabinet_location)
    REFERENCES cabinet_location(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet ADD CONSTRAINT Refcabinet_type112 
    FOREIGN KEY (cabinet_type)
    REFERENCES cabinet_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet ADD CONSTRAINT Refsys_user115 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet ADD CONSTRAINT Refuser_alias121 
    FOREIGN KEY (user_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: cabinet_definition 
--

ALTER TABLE cabinet_definition ADD CONSTRAINT Refcabinet113 
    FOREIGN KEY (cabinet)
    REFERENCES cabinet(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet_definition ADD CONSTRAINT Refsys_user116 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: cabinet_folder 
--

ALTER TABLE cabinet_folder ADD CONSTRAINT Refcabinet_definition114 
    FOREIGN KEY (cabinet_definition)
    REFERENCES cabinet_definition(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet_folder ADD CONSTRAINT Refsys_user117 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: cabinet_folder_document 
--

ALTER TABLE cabinet_folder_document ADD CONSTRAINT Refarchive_document118 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet_folder_document ADD CONSTRAINT Refcabinet_folder119 
    FOREIGN KEY (cabinet_folder)
    REFERENCES cabinet_folder(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE cabinet_folder_document ADD CONSTRAINT Refsys_user120 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: cabinet_location 
--

ALTER TABLE cabinet_location ADD CONSTRAINT Refsys_user109 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: checklist_detail_data_source 
--

ALTER TABLE checklist_detail_data_source ADD CONSTRAINT Refchecklist_data_source10 
    FOREIGN KEY (checklist_data_source)
    REFERENCES checklist_data_source(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: column_content 
--

ALTER TABLE column_content ADD CONSTRAINT Refgeneral_equipment_item165 
    FOREIGN KEY (general_equipment_item)
    REFERENCES general_equipment_item(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE column_content ADD CONSTRAINT Refform_column168 
    FOREIGN KEY (form_column)
    REFERENCES form_column(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence 
--

ALTER TABLE correspondence ADD CONSTRAINT Refpriority_type28 
    FOREIGN KEY (priority_type)
    REFERENCES priority_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refcorrespondence_type30 
    FOREIGN KEY (correspondece_type)
    REFERENCES correspondence_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refsys_user57 
    FOREIGN KEY (deleted_by_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refbox_type70 
    FOREIGN KEY (box_type)
    REFERENCES box_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Reforganization71 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Reforganization72 
    FOREIGN KEY (divan)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refcorrespondence_state73 
    FOREIGN KEY (correspondence_state)
    REFERENCES correspondence_state(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Reforganization95 
    FOREIGN KEY (root_organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refuser_alias96 
    FOREIGN KEY (owner_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence ADD CONSTRAINT Refsys_user97 
    FOREIGN KEY (creator_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence_archive 
--

ALTER TABLE correspondence_archive ADD CONSTRAINT Refarchive_document81 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_archive ADD CONSTRAINT Refcorrespondence82 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence_note 
--

ALTER TABLE correspondence_note ADD CONSTRAINT Refcorrespondence77 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_note ADD CONSTRAINT Refuser_alias78 
    FOREIGN KEY (from_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_note ADD CONSTRAINT Reforganization79 
    FOREIGN KEY (divan)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence_recipient 
--

ALTER TABLE correspondence_recipient ADD CONSTRAINT Refuser_alias32 
    FOREIGN KEY (to_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_recipient ADD CONSTRAINT Refcorrespondence34 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_recipient ADD CONSTRAINT Refpurpose_type74 
    FOREIGN KEY (purpose_type)
    REFERENCES purpose_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_recipient ADD CONSTRAINT Refpriority_type75 
    FOREIGN KEY (priority_type)
    REFERENCES priority_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_recipient ADD CONSTRAINT Reforganization76 
    FOREIGN KEY (divan)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_recipient ADD CONSTRAINT Refcorrespondence_note80 
    FOREIGN KEY (correspondence_note)
    REFERENCES correspondence_note(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence_task 
--

ALTER TABLE correspondence_task ADD CONSTRAINT Refcorrespondence152 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_task ADD CONSTRAINT Reftask153 
    FOREIGN KEY (task)
    REFERENCES task(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: correspondence_transmission 
--

ALTER TABLE correspondence_transmission ADD CONSTRAINT Refcorrespondence37 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_transmission ADD CONSTRAINT Refuser_alias39 
    FOREIGN KEY (from_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_transmission ADD CONSTRAINT Refuser_alias41 
    FOREIGN KEY (to_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE correspondence_transmission ADD CONSTRAINT Reftransmission_type43 
    FOREIGN KEY (transmission_type)
    REFERENCES transmission_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: document_sharing 
--

ALTER TABLE document_sharing ADD CONSTRAINT Refarchive_sharing123 
    FOREIGN KEY (id)
    REFERENCES archive_sharing(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE document_sharing ADD CONSTRAINT Refsharing_to_type125 
    FOREIGN KEY (sharing_to_type)
    REFERENCES sharing_to_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE document_sharing ADD CONSTRAINT Refarchive_document127 
    FOREIGN KEY (archive_document)
    REFERENCES archive_document(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE document_sharing ADD CONSTRAINT Reforganization128 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE document_sharing ADD CONSTRAINT Refuser_alias129 
    FOREIGN KEY (user_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: equipment_inspection_certificate 
--

ALTER TABLE equipment_inspection_certificate ADD CONSTRAINT Refcompany140 
    FOREIGN KEY (company)
    REFERENCES company(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_certificate ADD CONSTRAINT Refequipment_inspection_form141 
    FOREIGN KEY (equipment_inspection_form)
    REFERENCES equipment_inspection_form(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_certificate ADD CONSTRAINT Refsys_user142 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_certificate ADD CONSTRAINT Refsys_user143 
    FOREIGN KEY (allow_reprint_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_certificate ADD CONSTRAINT Refsys_user144 
    FOREIGN KEY (reprint_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: equipment_inspection_form 
--

ALTER TABLE equipment_inspection_form ADD CONSTRAINT Refexamination_type2 
    FOREIGN KEY (examination_type)
    REFERENCES examination_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_form ADD CONSTRAINT Refequipment_type3 
    FOREIGN KEY (equipment_type)
    REFERENCES equipment_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_form ADD CONSTRAINT Refequipment_category4 
    FOREIGN KEY (equipment_category)
    REFERENCES equipment_category(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_form ADD CONSTRAINT Refcompany139 
    FOREIGN KEY (company)
    REFERENCES company(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_form ADD CONSTRAINT Refsticker154 
    FOREIGN KEY (sticker)
    REFERENCES sticker(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: equipment_inspection_form_item 
--

ALTER TABLE equipment_inspection_form_item ADD CONSTRAINT Refequipment_inspection_form170 
    FOREIGN KEY (equipment_inspection_form)
    REFERENCES equipment_inspection_form(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE equipment_inspection_form_item ADD CONSTRAINT Refgeneral_equipment_item171 
    FOREIGN KEY (general_equipment_item)
    REFERENCES general_equipment_item(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: extra_property_definition 
--

ALTER TABLE extra_property_definition ADD CONSTRAINT Refproperty_type49 
    FOREIGN KEY (property_type)
    REFERENCES property_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE extra_property_definition ADD CONSTRAINT Refarchive_extra_property51 
    FOREIGN KEY (archive_extra_property)
    REFERENCES archive_extra_property(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: folder_sharing 
--

ALTER TABLE folder_sharing ADD CONSTRAINT Refarchive_sharing124 
    FOREIGN KEY (id)
    REFERENCES archive_sharing(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE folder_sharing ADD CONSTRAINT Refsharing_to_type126 
    FOREIGN KEY (sharing_to_type)
    REFERENCES sharing_to_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE folder_sharing ADD CONSTRAINT Refcabinet_folder131 
    FOREIGN KEY (cabinet_folder)
    REFERENCES cabinet_folder(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE folder_sharing ADD CONSTRAINT Reforganization132 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE folder_sharing ADD CONSTRAINT Refuser_alias133 
    FOREIGN KEY (user_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: form_column 
--

ALTER TABLE form_column ADD CONSTRAINT Refform_row167 
    FOREIGN KEY (form_row)
    REFERENCES form_row(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: form_row 
--

ALTER TABLE form_row ADD CONSTRAINT Refform_template166 
    FOREIGN KEY (form_template)
    REFERENCES form_template(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: form_template 
--

ALTER TABLE form_template ADD CONSTRAINT Refequipment_category163 
    FOREIGN KEY (equipment_category)
    REFERENCES equipment_category(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE form_template ADD CONSTRAINT Refsys_user164 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: general_equipment_item 
--

ALTER TABLE general_equipment_item ADD CONSTRAINT Refitem_type7 
    FOREIGN KEY (item_type)
    REFERENCES item_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE general_equipment_item ADD CONSTRAINT Refchecklist_data_source169 
    FOREIGN KEY (data_source)
    REFERENCES checklist_data_source(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: group_member 
--

ALTER TABLE group_member ADD CONSTRAINT Refworking_group54 
    FOREIGN KEY (working_group)
    REFERENCES working_group(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE group_member ADD CONSTRAINT Refsys_user56 
    FOREIGN KEY (creator_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE group_member ADD CONSTRAINT Refuser_alias58 
    FOREIGN KEY (member_user_alias)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: hierarchy_system_def 
--

ALTER TABLE hierarchy_system_def ADD CONSTRAINT Refhierarchy_system100 
    FOREIGN KEY (hierarchy_system)
    REFERENCES hierarchy_system(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE hierarchy_system_def ADD CONSTRAINT Refhierarchy_type104 
    FOREIGN KEY (hierarchy_type)
    REFERENCES hierarchy_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: inspection_form_workflow 
--

ALTER TABLE inspection_form_workflow ADD CONSTRAINT Refequipment_inspection_form24 
    FOREIGN KEY (equipment_inspection_form)
    REFERENCES equipment_inspection_form(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE inspection_form_workflow ADD CONSTRAINT Refworkflow_definition25 
    FOREIGN KEY (workflow_definition)
    REFERENCES workflow_definition(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: inspection_form_workflow_step 
--

ALTER TABLE inspection_form_workflow_step ADD CONSTRAINT Refequipment_inspection_form136 
    FOREIGN KEY (equipment_inspection_form)
    REFERENCES equipment_inspection_form(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE inspection_form_workflow_step ADD CONSTRAINT Refworkflow_definition137 
    FOREIGN KEY (workflow_definition)
    REFERENCES workflow_definition(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE inspection_form_workflow_step ADD CONSTRAINT Refsys_user138 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: job_position 
--

ALTER TABLE job_position ADD CONSTRAINT Refeffect_type108 
    FOREIGN KEY (effect_type)
    REFERENCES effect_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: job_position_hierarchy 
--

ALTER TABLE job_position_hierarchy ADD CONSTRAINT Refjob_position103 
    FOREIGN KEY (job_position)
    REFERENCES job_position(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE job_position_hierarchy ADD CONSTRAINT Refhierarchy_type105 
    FOREIGN KEY (hierarchy_type)
    REFERENCES hierarchy_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: linked_correspondence 
--

ALTER TABLE linked_correspondence ADD CONSTRAINT Refcorrespondence83 
    FOREIGN KEY (master_correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE linked_correspondence ADD CONSTRAINT Refcorrespondence84 
    FOREIGN KEY (linked_correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: notification 
--

ALTER TABLE notification ADD CONSTRAINT Refnotification_type52 
    FOREIGN KEY (notification_type)
    REFERENCES notification_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE notification ADD CONSTRAINT Refcorrespondence85 
    FOREIGN KEY (correspondence)
    REFERENCES correspondence(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: notification_setting 
--

ALTER TABLE notification_setting ADD CONSTRAINT Refnotification_style48 
    FOREIGN KEY (notification_style)
    REFERENCES notification_style(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE notification_setting ADD CONSTRAINT Refperiod_type50 
    FOREIGN KEY (period_type)
    REFERENCES period_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: organization 
--

ALTER TABLE organization ADD CONSTRAINT Reforganization62 
    FOREIGN KEY (parent_organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE organization ADD CONSTRAINT Refhierarchy_system_def101 
    FOREIGN KEY (hierarchy_system_def)
    REFERENCES hierarchy_system_def(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE organization ADD CONSTRAINT Refhierarchy_system102 
    FOREIGN KEY (hierarchy_system)
    REFERENCES hierarchy_system(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: sticker 
--

ALTER TABLE sticker ADD CONSTRAINT Refsys_user155 
    FOREIGN KEY (printed_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE sticker ADD CONSTRAINT Refsys_user156 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE sticker ADD CONSTRAINT Refsys_user157 
    FOREIGN KEY (deleted_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: sys_role_permission 
--

ALTER TABLE sys_role_permission ADD CONSTRAINT Refsys_permission64 
    FOREIGN KEY (sys_permission)
    REFERENCES sys_permission(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE sys_role_permission ADD CONSTRAINT Refsys_role65 
    FOREIGN KEY (sys_role)
    REFERENCES sys_role(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: sys_user 
--

ALTER TABLE sys_user ADD CONSTRAINT Reforganization106 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE sys_user ADD CONSTRAINT Reforganization107 
    FOREIGN KEY (root_organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: sys_user_role 
--

ALTER TABLE sys_user_role ADD CONSTRAINT Refsys_role66 
    FOREIGN KEY (sys_role)
    REFERENCES sys_role(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE sys_user_role ADD CONSTRAINT Refsys_user67 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: sys_user_setting 
--

ALTER TABLE sys_user_setting ADD CONSTRAINT Refsys_user99 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: task 
--

ALTER TABLE task ADD CONSTRAINT Refuser_alias145 
    FOREIGN KEY (assigner)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE task ADD CONSTRAINT Refuser_alias147 
    FOREIGN KEY (assign_to)
    REFERENCES user_alias(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE task ADD CONSTRAINT Refcompany148 
    FOREIGN KEY (company)
    REFERENCES company(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE task ADD CONSTRAINT Refequipment_category149 
    FOREIGN KEY (equipment_category)
    REFERENCES equipment_category(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE task ADD CONSTRAINT Refsys_user150 
    FOREIGN KEY (created_by)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE task ADD CONSTRAINT Refservice_type151 
    FOREIGN KEY (service_type)
    REFERENCES service_type(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: user_alias 
--

ALTER TABLE user_alias ADD CONSTRAINT Refjob_position61 
    FOREIGN KEY (job_position)
    REFERENCES job_position(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Refsys_user63 
    FOREIGN KEY (sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Reforganization86 
    FOREIGN KEY (root_organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Reforganization87 
    FOREIGN KEY (divan)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Refsys_user88 
    FOREIGN KEY (creator_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Refsys_user89 
    FOREIGN KEY (frozen_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE user_alias ADD CONSTRAINT Reforganization98 
    FOREIGN KEY (organization)
    REFERENCES organization(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: workflow_definition 
--

ALTER TABLE workflow_definition ADD CONSTRAINT Refworkflow20 
    FOREIGN KEY (workflow)
    REFERENCES workflow(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE workflow_definition ADD CONSTRAINT Refstep21 
    FOREIGN KEY (step)
    REFERENCES step(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE workflow_definition ADD CONSTRAINT Refworkflow_definition22 
    FOREIGN KEY (next)
    REFERENCES workflow_definition(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;

ALTER TABLE workflow_definition ADD CONSTRAINT Refworkflow_definition23 
    FOREIGN KEY (previous)
    REFERENCES workflow_definition(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


-- 
-- TABLE: working_group 
--

ALTER TABLE working_group ADD CONSTRAINT Refsys_user59 
    FOREIGN KEY (creator_sys_user)
    REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT
;


